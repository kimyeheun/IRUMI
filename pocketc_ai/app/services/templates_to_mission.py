import random
from collections import defaultdict
from typing import Dict, List, Tuple

TemplateName = str

BASE_WEIGHTS: Dict[str, List[Tuple[TemplateName, float]]] = {
    # 습관성·소액다건
    "커피": [("COUNT_CAP_DAILY",0.5), ("PER_TXN_DAILY",0.3), ("CATEGORY_BAN_DAILY",0.2)],
    "간식": [("COUNT_CAP_DAILY",0.5), ("PER_TXN_DAILY",0.3), ("CATEGORY_BAN_DAILY",0.2)],
    "음료": [("COUNT_CAP_DAILY",0.5), ("PER_TXN_DAILY",0.3), ("CATEGORY_BAN_DAILY",0.2)],
    "게임/콘텐츠": [("COUNT_CAP_DAILY",0.5), ("PER_TXN_DAILY",0.3), ("CATEGORY_BAN_DAILY",0.2)],

    # 야간충동
    "배달음식": [("TIME_BAN_DAILY",0.6), ("SPEND_CAP_DAILY",0.25), ("PER_TXN_DAILY",0.15)],

    # 변동·중대금액
    "외식": [("SPEND_CAP_DAILY",0.6), ("PER_TXN_DAILY",0.4), ("COUNT_CAP_DAILY",0.1)],
    "온라인 쇼핑몰": [("SPEND_CAP_DAILY",0.6), ("PER_TXN_DAILY",0.3), ("COUNT_CAP_DAILY",0.1)],
    "의류/패션": [("SPEND_CAP_DAILY",0.6), ("PER_TXN_DAILY",0.3), ("COUNT_CAP_DAILY",0.1)],
    "뷰티/미용": [("SPEND_CAP_DAILY",0.6), ("PER_TXN_DAILY",0.3), ("COUNT_CAP_DAILY",0.1)],
    "생활용품": [("SPEND_CAP_DAILY",0.6), ("PER_TXN_DAILY",0.3), ("COUNT_CAP_DAILY",0.1)],
    "영화/공연": [("SPEND_CAP_DAILY",0.6), ("PER_TXN_DAILY",0.3), ("COUNT_CAP_DAILY",0.1)],
    "여행": [("SPEND_CAP_DAILY",0.6), ("PER_TXN_DAILY",0.3), ("COUNT_CAP_DAILY",0.1)],

    # 이동수단
    "택시/대리": [("PER_TXN_DAILY",0.5), ("SPEND_CAP_DAILY",0.4), ("COUNT_CAP_DAILY",0.1)],

    # 기타/충동
    "취미/오락": [("SPEND_CAP_DAILY",0.45), ("PER_TXN_DAILY",0.35), ("COUNT_CAP_DAILY",0.2)],
    "충동구매": [("SPEND_CAP_DAILY",0.45), ("PER_TXN_DAILY",0.35), ("COUNT_CAP_DAILY",0.2)],
    "도서/교재": [("SPEND_CAP_DAILY",0.45), ("PER_TXN_DAILY",0.35), ("COUNT_CAP_DAILY",0.2)],
    "경조사비": [("SPEND_CAP_DAILY",0.45), ("PER_TXN_DAILY",0.35), ("COUNT_CAP_DAILY",0.2)],
    "기타": [("SPEND_CAP_DAILY",0.45), ("PER_TXN_DAILY",0.35), ("COUNT_CAP_DAILY",0.2)],
}

# 고정/재무/필수: 대상 제외(필요 시 따로 월간 템플릿에서 다룸)
EXCLUDED = {
    "월세/관리비","전기세","수도세","가스비","세금/보험",
    "휴대폰 요금","인터넷","대출/이자","저축","투자","송금","병원","대중교통","유류비","약국","학원/수강료","자격증","OTT/구독서비스"
}

def pick_template_for_category(
    category: str,
    user_stats: Dict[str, float] = None,
    epsilon: float = 0.1
) -> TemplateName:
    """
    category에 대해 후보 템플릿 분포를 만들고, 사용자 지표(user_stats)로 가중치 보정 후 한 개 샘플링.
    user_stats 예:
      - mean_daily_count: float (해당 카테고리 하루 평균 결제 횟수)
      - night_ratio: float (22~06 비중 0~1)
      - per_txn_std: float (건별 금액 표준편차)
      - daily_sum_volatility: float (일일 총액 변동성)
    """
    if category in EXCLUDED:
        raise ValueError(f"카테고리 '{category}'는 일일 절감 미션 대상에서 제외하는 것을 권장합니다.")

    base = BASE_WEIGHTS.get(category)
    if base is None: # 그룹 미지정: 안전한 기본값
        base = [("SPEND_CAP_DAILY",0.5), ("PER_TXN_DAILY",0.3), ("COUNT_CAP_DAILY",0.2)]

    weights = defaultdict(float)
    for name, w in base:
        weights[name] += w

    # 데이터 기반 가중치 보정
    us = user_stats or {}
    mean_daily_count = us.get("mean_daily_count", 0.0)
    night_ratio = us.get("night_ratio", 0.0)
    per_txn_std = us.get("per_txn_std", 0.0)
    daily_sum_vol = us.get("daily_sum_volatility", 0.0)

    # 간단한 선형 보정 (스케일은 경험적으로 조정)
    weights["COUNT_CAP_DAILY"] += 0.1 * min(1.0, mean_daily_count / 2.0)  # 건수↑
    weights["TIME_BAN_DAILY"]  += 0.3 * min(1.0, night_ratio)             # 야간비중↑
    weights["PER_TXN_DAILY"]   += 0.1 * min(1.0, per_txn_std / 10000.0)   # 금액 분산↑
    weights["SPEND_CAP_DAILY"] += 0.1 * min(1.0, daily_sum_vol / 30000.0) # 일일 총액 변동↑

    # 정규화
    items = list(weights.items())
    total = sum(w for _, w in items)
    probs = [w/total for _, w in items]

    # ε-탐색: 소량의 랜덤으로 새로운 전략도 시도
    if random.random() < epsilon:
        return random.choice(items)[0]

    # 가중치 샘플링
    r = random.random()
    cum = 0.0
    for (name, _), p in zip(items, probs):
        cum += p
        if r <= cum:
            return name
    return items[-1][0]
