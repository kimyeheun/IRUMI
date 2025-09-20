from datetime import datetime
from typing import Dict, Any


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

def build_dsl_for_template(name: str, user_id: int, sub_id: int, now: datetime, stats: Dict[str, float]) -> Dict[str, Any]:
    caps = _default_caps(stats)
    base = {
        "window": {"scope": "daily", "tz": "Asia/Seoul", "range": {"start_date": now.strftime("%Y-%m-%d"), "end_date": now.strftime("%Y-%m-%d")}},
        "target": {"user_id": user_id, "sub_id": sub_id}
    }
    if name == "COUNT_CAP_DAILY":
        base["conditions"] = [{"type": "count", "comparator": "<=", "value": caps["count_cap"]}]
    elif name == "PER_TXN_DAILY":
        base["conditions"] = [{"type": "per_txn_max", "comparator": "<=", "value": caps["per_txn_ceiling"]}]
    elif name == "SPEND_CAP_DAILY":
        base["conditions"] = [{"type": "total_spend", "comparator": "<=", "value": caps["daily_budget"]}]
    elif name == "TIME_BAN_DAILY":
        base["conditions"] = [{
            "type": "count_in_time",
            "params": {"start": "22:00", "end": "06:00"},
            "comparator": "==",
            "value": 0
        }]
    elif name == "CATEGORY_BAN_DAILY":
        base["conditions"] = [{"type": "count", "comparator": "==", "value": 0}]
    else:
        base["conditions"] = [{"type": "total_spend", "comparator": "<=", "value": caps["daily_budget"]}]
    return base
