from datetime import datetime, time
from typing import Dict, Any, List
import pandas as pd


def _in_range(t: time, start: time, end: time) -> bool:
    # 22~06처럼 자정을 넘는 구간 처리
    if start <= end:
        return start <= t < end
    return t >= start or t < end

def compile_plan(dsl: Dict[str, Any]) -> Dict[str, Any]:
    """
    실행 시 필요한 필터/집계 키만 뽑아놓는 간단한 컴파일.
    여기선 dsl 그대로 반환해도 충분하지만, 훗날 최적화를 위해 별도 함수로 분리.
    """
    return {"plan": dsl}

def evaluate_dsl(transactions: pd.DataFrame, dsl: Dict[str, Any]) -> Dict[str, Any]:
    """
    transactions: DataFrame cols = [user_id, sub_id, transacted_at, amount, ...]
    dsl: build_dsl_for_template 로 생성된 dict
    """
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
            val = float((df["time_only"].apply(lambda tt: _in_range(tt, s_t, e_t))).sum())
        else:
            return True  # 알 수 없는 조건은 통과

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
