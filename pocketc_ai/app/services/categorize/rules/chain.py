from __future__ import annotations

from typing import List

from pocketc_ai.app.services.categorize.rules.base import Rule


class Categorizer:
    def __init__(self, rules: List[Rule], fallback: str = "기타"):
        self.rules = rules
        self.fallback = fallback

    def classify(self, merchant: str, amount: int, trans_time: int) -> str:
        for r in self.rules:
            hit = r.apply(merchant, amount, trans_time)
            if hit:
                return hit
        return self.fallback
