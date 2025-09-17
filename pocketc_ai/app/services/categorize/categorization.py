from __future__ import annotations

from datetime import datetime

from pocketc_ai.app.services.categorize.category_lib import REGEX_RULES
from pocketc_ai.app.services.categorize.rules.chain import Categorizer
from pocketc_ai.app.services.categorize.rules.heuristics import CafeRule, ConvenienceRule, DiningTimeRule
from pocketc_ai.app.services.categorize.rules.regex_rule import RegexRule


class CategorizationService:
    def __init__(self, fallback: str = "기타"):
        self._categorizer = Categorizer(
            rules=[
                RegexRule(REGEX_RULES),
                CafeRule(),
                ConvenienceRule(),
                DiningTimeRule(),
            ],
            fallback=fallback,
        )

    def classify(self, merchant: str, amount: int, trans_time: datetime) -> str:
        return self._categorizer.classify(merchant, amount, trans_time.hour)
