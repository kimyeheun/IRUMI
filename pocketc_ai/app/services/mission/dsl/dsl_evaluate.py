from datetime import datetime, time
from typing import Dict, Any, List
import pandas as pd


def _in_dawn_range(t: time, start: time, end: time) -> bool:
    if start <= end:
        return start <= t < end
    return t >= start or t < end

def evaluate_dsl(transactions: pd.DataFrame, dsl: Dict[str, Any]) -> Dict[str, Any]:
    user_id = dsl["target"]["user_id"]
    sub_id  = dsl["target"]["sub_id"]

    # 날짜 필터
    start_date = dsl["window"]["range"]["start_date"]
    end_date   = dsl["window"]["range"]["end_date"]
    df = transactions[
        (transactions["user_id"] == user_id) &
        (transactions["sub_id"] == sub_id) &
        (transactions["transacted_at"].dt.strftime("%Y-%m-%d") >= start_date) &
        (transactions["transacted_at"].dt.strftime("%Y-%m-%d") <= end_date)
    ].copy()

    def check(cond: Dict[str, Any]) -> bool:
        t = cond["type"]; op = cond["comparator"]; v = float(cond["value"])
        if t == "count":
            val = float(len(df))
        elif t == "per_txn_max":
            val = float(df["amount"].max() if not df.empty else 0.0)
        elif t == "total_spend":
            val = float(df["amount"].sum() if not df.empty else 0.0)
        elif t == "count_in_time":
            s = cond["params"]["start"]; e = cond["params"]["end"]
            s_t = datetime.strptime(s, "%H:%M").time(); e_t = datetime.strptime(e, "%H:%M").time()
            if "time_only" not in df:
                df["time_only"] = df["transacted_at"].dt.time
            val = float((df["time_only"].apply(lambda tt: _in_dawn_range(tt, s_t, e_t))).sum())
        else:
            return True
        if op == "==": return val == v
        if op == "!=": return val != v
        if op == "<=": return val <= v
        if op == "<":  return val <  v
        if op == ">=": return val >= v
        if op == ">":  return val >  v
        return True

    results: List[bool] = [check(c) for c in dsl.get("conditions", [])]
    success = all(results) if results else True
    return {"success": success, "details": results}
