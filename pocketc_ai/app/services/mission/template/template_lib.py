from __future__ import annotations

from typing import Dict, Tuple, List

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
