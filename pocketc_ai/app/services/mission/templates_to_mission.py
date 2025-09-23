from __future__ import annotations

from datetime import datetime, timedelta
from typing import Dict, Any, Tuple, List


def _default_caps(stats: Dict[str, float]) -> Dict[str, Any]:
    mean_cnt = float(stats.get("mean_daily_count", 0)) or 1.0
    per_std = float(stats.get("per_txn_std", 0.0)) or 0.0
    max_mean = float(stats.get("max_per_txn_mean", 0.0)) or 10000.0
    budget = float(stats.get("daily_sum_volatility", 0.0)) or max(20000.0, max_mean * 1.5)
    return {
        "count_cap": max(1, int(mean_cnt * 0.6)),
        "per_txn_ceiling": int(max_mean * 0.8) if max_mean > 0 else 10000,
        "daily_budget": int(budget),
    }

def _get_valid_time(now: datetime, params: Dict[str, Any], scope: str) -> Tuple[datetime, datetime]:
    if scope == "weekly":
        start_of_week = now - timedelta(days=now.weekday())
        end_of_week = start_of_week + timedelta(days=6)
        valid_from = start_of_week.replace(hour=0, minute=0, second=0, microsecond=0)
        valid_to = end_of_week.replace(hour=23, minute=59, second=59, microsecond=999999)
    elif scope == "monthly":
        start_of_month = now.replace(day=1)
        # 다음 달 1일에서 하루를 빼서 이달의 마지막 날을 구합니다.
        next_month = (start_of_month.replace(day=28) + timedelta(days=4)).replace(day=1)
        end_of_month = next_month - timedelta(days=1)
        valid_from = start_of_month.replace(hour=0, minute=0, second=0, microsecond=0)
        valid_to = end_of_month.replace(hour=23, minute=59, second=59, microsecond=999999)
    else:  # daily
        valid_from = now.replace(hour=0, minute=0, second=0, microsecond=0)
        valid_to = now.replace(hour=23, minute=59, second=59, microsecond=999999)

    # 시간 제한 미션인 경우 (데일리 미션에만 해당될 수 있음)
    if 'time_label' in params and 'time_ranges' in params and params['time_ranges']:
        first_range = params['time_ranges'][0]
        start_h, start_m = map(int, first_range['start'].split(':'))
        end_h, end_m = map(int, first_range['end'].split(':'))
        # 시작 시간과 종료 시간 객체 생성
        valid_from = now.replace(hour=start_h, minute=start_m, second=0, microsecond=0)
        valid_to = now.replace(hour=end_h, minute=end_m, second=59, microsecond=999999)
        # 자정을 넘으면, 종료 날짜를 다음 날로 설정
        if valid_from > valid_to:
            valid_to += timedelta(days=1)

    return valid_from, valid_to


class _SafeDict(dict):
    def __missing__(self, key):
        return "{%s}" % key

def _derive_time_params(stats: Dict[str, float]) -> Tuple[str, List[Dict[str,str]]]:
    night = float(stats.get("night_ratio", 0.0) or 0.0)
    morning = float(stats.get("morning_ratio", 0.0) or 0.0)
    afternoon = float(stats.get("afternoon_ratio", 0.0) or 0.0)

    label = "저녁"
    ranges = [{"start": "18:00", "end": "22:00"}]
    if night >= max(morning, afternoon, 0.33):
        label = "야간"
        ranges = [{"start": "22:00", "end": "06:00"}]
    elif morning >= max(afternoon, 0.33):
        label = "오전"
        ranges = [{"start": "06:00", "end": "11:00"}]
    return label, ranges

def _compute_params_for_template(tmpl_code: str, sub_id: int, sub_name: str, stats: Dict[str, float]) -> Dict[str, Any]:
    caps = _default_caps(stats)
    params: Dict[str, Any] = {"label": sub_name, "sub_id": sub_id}

    # stats에서 주간/월간 데이터 추출
    weekly_sum = float(stats.get("weekly_sum", caps["daily_budget"] * 5))
    weekly_count = int(stats.get("weekly_count", caps["count_cap"] * 5))
    monthly_sum = float(stats.get("monthly_sum", caps["daily_budget"] * 20))
    monthly_count = int(stats.get("monthly_count", caps["count_cap"] * 20))

    # --- 데일리 ---
    if tmpl_code == "CATEGORY_BAN_DAILY":
        pass
    elif tmpl_code == "SPEND_CAP_DAILY":
        params["amount"] = caps["daily_budget"]
    elif tmpl_code == "PER_TXN_DAILY":
        params["per_txn"] = caps["per_txn_ceiling"]
    elif tmpl_code == "COUNT_CAP_DAILY":
        params["N"] = caps["count_cap"]
    elif tmpl_code == "TIME_BAN_DAILY":
        time_label, time_ranges = _derive_time_params(stats)
        params["time_label"] = time_label
        params["time_ranges"] = time_ranges
    # --- 위클리 ---
    elif tmpl_code == "DAY_BAN_WEEKLY":
        # TODO: 가장 소비가 잦은 요일 계산 로직 추가 (예: '월')
        params["day_label"] = "월"
    elif tmpl_code == "SPEND_CAP_WEEKLY":
        params["amount"] = int(weekly_sum * 0.8)
    elif tmpl_code == "COUNT_CAP_WEEKLY":
        params["N"] = max(1, int(weekly_count * 0.8))
    # --- 먼슬리 ---
    elif tmpl_code == "SPEND_CAP_MONTHLY":
        params["amount"] = int(monthly_sum * 0.8)
    elif tmpl_code == "SPEND_CAP_MONTHLY":
        params["N"] = max(1, int(monthly_count * 0.8))

    return params

def _build_dsl_for_template(name: str, user_id: int, params: Dict[str, Any], scope:str) -> Dict[str, Any]:
    base = {
        "window": {"scope": scope, "tz": "Asia/Seoul"},
        "target": {"user_id": user_id, "sub_id": params["sub_id"]}
    }
    if name in ["COUNT_CAP_DAILY", "COUNT_CAP_WEEKLY", "COUNT_CAP_MONTHLY"]:
        base["conditions"] = [{"type": "count", "comparator": "<=", "value": params["N"]}]
    elif name == "PER_TXN_DAILY":
        base["conditions"] = [{"type": "per_txn_max", "comparator": "<=", "value": params["per_txn"]}]
    elif name in ["SPEND_CAP_DAILY", "SPEND_CAP_WEEKLY", "SPEND_CAP_MONTHLY"]:
        base["conditions"] = [{"type": "total_spend", "comparator": "<=", "value": params["amount"]}]
    elif name == "TIME_BAN_DAILY":
        base["window"]["time_of_day"] = params["time_ranges"]
        base["conditions"] = [{"type": "count", "comparator": "==", "value": 0}]
    elif name == "CATEGORY_BAN_DAILY":
        base["conditions"] = [{"type": "count", "comparator": "==", "value": 0}]
    elif name == "DAY_BAN_WEEKLY":
        # 예: 월요일은 0, 일요일은 6
        day_map = {"월": 0, "화": 1, "수": 2, "목": 3, "금": 4, "토": 5, "일": 6}
        base["window"]["day_of_week"] = [day_map.get(params["day_label"], 0)]
        base["conditions"] = [{"type": "count", "comparator": "==", "value": 0}]
    else:
        base["conditions"] = [{"type": "total_spend", "comparator": "<=", "value": params.get("amount", 20000)}]
    return base

def build_mission_details(
        tmpl_code: str,
        user_id: int,
        sub_id: int,
        sub_name: str,
        stats: Dict[str, float],
        now: datetime,
        repo,
) -> Tuple[str, Dict[str, Any], Tuple[datetime, datetime]]:
    tmpl = repo.get_by_code(tmpl_code)
    print(tmpl)
    raw_sentence: str = getattr(tmpl, "mission", "")
    print(raw_sentence)
    # 1. 미션 파라미터 설정 (동적 시간대 포함)
    params = _compute_params_for_template(tmpl_code, sub_id, sub_name, stats)
    # 2. 미션 시간
    scope = "daily"
    if tmpl_code.endswith("_WEEKLY"): scope = "weekly"
    elif tmpl_code.endswith("_MONTHLY"): scope = "monthly"
    (valid_from, valid_to) = _get_valid_time(now, params, scope)
    # 3. 미션 문장 생성
    sentence = raw_sentence.format_map(_SafeDict(params))
    # 4. 동일한 파라미터를 사용해 DSL 생성
    dsl = _build_dsl_for_template(tmpl_code, user_id, params, scope)
    return sentence, dsl, (valid_from, valid_to)
