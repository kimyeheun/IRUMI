from datetime import datetime, time
from typing import Dict, Any, List
import pandas as pd


def _in_dawn_range(t: time, start: time, end: time) -> bool:
    if start <= end:
        return start <= t < end
    return t >= start or t < end


def evaluate_dsl(transactions: pd.DataFrame, dsl: Dict[str, Any]) -> Dict[str, Any]:
    user_id = dsl["target"]["user_id"]
    sub_id = dsl["target"]["sub_id"]

    # 날짜 필터
    start_date = dsl["window"]["range"]["start_date"]
    end_date = dsl["window"]["range"]["end_date"]
    df = transactions[
        (transactions["user_id"] == user_id) &
        (transactions["sub_id"] == sub_id) &
        (transactions["transacted_at"].dt.strftime("%Y-%m-%d") >= start_date) &
        (transactions["transacted_at"].dt.strftime("%Y-%m-%d") <= end_date)
        ].copy()

    # window 레벨 시간 필터링 (TIME_BAN_DAILY 용)
    if "time_of_day" in dsl["window"]:
        time_ranges = dsl["window"]["time_of_day"]
        if "time_only" not in df:
            df["time_only"] = df["transacted_at"].dt.time

        mask = pd.Series([False] * len(df), index=df.index)
        for time_range in time_ranges:
            s_t = datetime.strptime(time_range["start"], "%H:%M").time()
            e_t = datetime.strptime(time_range["end"], "%H:%M").time()
            mask = mask | (df["time_only"].apply(lambda tt: _in_dawn_range(tt, s_t, e_t)))
        df = df[mask]

    def check(cond: Dict[str, Any]) -> bool:
        df_filtered = df.copy()

        # params.filter 조건 처리 (PER_TXN_DAILY 용)
        if "params" in cond and "filter" in cond["params"]:
            filters = cond["params"]["filter"]
            for col, op_val in filters.items():
                for op, val in op_val.items():
                    if op == ">":
                        df_filtered = df_filtered[df_filtered[col] > float(val)]
                    elif op == "<":
                        df_filtered = df_filtered[df_filtered[col] < float(val)]
                    elif op == ">=":
                        df_filtered = df_filtered[df_filtered[col] >= float(val)]
                    elif op == "<=":
                        df_filtered = df_filtered[df_filtered[col] <= float(val)]
                    elif op == "==":
                        df_filtered = df_filtered[df_filtered[col] == float(val)]
                    elif op == "!=":
                        df_filtered = df_filtered[df_filtered[col] != float(val)]

        t = cond["type"]
        op = cond.get("comparator", "==")  # comparator가 없는 경우를 대비
        v = float(cond["value"])

        val = 0.0
        if t == "count":
            val = float(len(df_filtered))
        elif t == "per_txn_max":
            val = float(df_filtered["amount"].max() if not df_filtered.empty else 0.0)
        elif t == "total_spend" or t == "spend":
            val = float(df_filtered["amount"].sum() if not df_filtered.empty else 0.0)
        else:
            # 알 수 없는 타입은 True 반환하여 무시
            return True

        # 비교 연산자 처리
        if op == "==": return val == v
        if op == "!=": return val != v
        if op == "<=": return val <= v
        if op == "<":  return val < v
        if op == ">=": return val >= v
        if op == ">":  return val > v
        return True

    results: List[bool] = [check(c) for c in dsl.get("conditions", [])]
    success = all(results) if results else True
    return {"success": success, "details": results}