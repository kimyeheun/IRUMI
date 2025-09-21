from __future__ import annotations

from typing import Optional

from app.services.categorize.category_lib import CAFE_HINT, CONVENIENCE_HINT  # 패턴 재사용
from app.services.categorize.rules.base import Rule


class CafeRule(Rule):
    def apply(self, merchant: str, amount: int, kst_hour: int) -> Optional[str]:
        if CAFE_HINT.search(merchant):
            return "커피"
        return None

class ConvenienceRule(Rule):
    def apply(self, merchant: str, amount: int, kst_hour: int) -> Optional[str]:
        if CONVENIENCE_HINT.search(merchant):
            return "간식"
        return None

class DiningTimeRule(Rule):
    def apply(self, merchant: str, amount: int, kst_hour: int) -> Optional[str]:
        if (11 <= kst_hour <= 14 or 18 <= kst_hour <= 21) and amount > 10_000:
            return "외식"
        return None
