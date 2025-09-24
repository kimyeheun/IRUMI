from __future__ import annotations

import logging
import random
from collections import defaultdict
from typing import Dict, List

from app.services.mission.template.template_lib import TemplateName, EXCLUDED, BASE_WEIGHTS


def pick_template(
        category: str,
        user_stats: Dict[str, float] = None,
        epsilon: float = 0.1,
        exclude: List[TemplateName] = None,
) -> TemplateName:

    if category in EXCLUDED:
        logging.info(f"{category}는 미션의 대상 카테고리가 아닙니다.")
        raise ValueError(f"카테고리 '{category}'는 일일 절감 미션 대상에서 제외하는 것을 권장합니다.")

    base = (
        BASE_WEIGHTS.get(category))
    if base is None:
        base = [("SPEND_CAP_DAILY",0.5), ("PER_TXN_DAILY",0.3), ("COUNT_CAP_DAILY",0.2)]

    if exclude:
        base = [(name, w) for name, w in base if name not in exclude]
        if not base:
             raise ValueError(f"카테고리 '{category}'에 대해 선택할 수 있는 템플릿이 없습니다.")

    weights = defaultdict(float)
    for name, w in base:
        weights[name] += w

    us = user_stats or {}
    mean_daily_count = us.get("mean_daily_count", 0.0)
    night_ratio = us.get("night_ratio", 0.0)
    per_txn_std = us.get("per_txn_std", 0.0)
    daily_sum_vol = us.get("daily_sum_volatility", 0.0)

    weights["COUNT_CAP_DAILY"] += 0.1 * min(1.0, mean_daily_count / 2.0)  # 건수 ↑
    weights["TIME_BAN_DAILY"]  += 0.3 * min(1.0, night_ratio)             # 야간비중 ↑
    weights["PER_TXN_DAILY"]   += 0.1 * min(1.0, per_txn_std / 10000.0)   # 금액 분산 ↑
    weights["SPEND_CAP_DAILY"] += 0.1 * min(1.0, daily_sum_vol / 30000.0) # 일일 총액 변동 ↑

    items = list(weights.items())
    total = sum(w for _, w in items)
    probs = [w/total for _, w in items]

    if random.random() < epsilon:
        return random.choice(items)[0]

    r = random.random()
    cum = 0.0
    for (name, _), p in zip(items, probs):
        cum += p
        if r <= cum:
            return name
    return items[-1][0]
