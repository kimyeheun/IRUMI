from __future__ import annotations

import re
from typing import List, Tuple, Optional

from pocketc_ai.app.services.categorize.rules.base import Rule


class RegexRule(Rule):
    def __init__(self, patterns: List[Tuple[re.Pattern, str]]):
        self.patterns = patterns

    def apply(self, merchant: str, amount: int, kst_hour: int) -> Optional[str]:
        for pat, sub_name in self.patterns:
            if pat.search(merchant):
                return sub_name
        return None
