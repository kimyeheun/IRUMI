from __future__ import annotations

from abc import ABC, abstractmethod
from typing import Optional


class Rule(ABC):
    @abstractmethod
    def apply(self, merchant: str, amount: int, kst_hour: int) -> Optional[str]:
        ...
