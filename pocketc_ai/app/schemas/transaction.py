from datetime import datetime
from typing import List, Optional

from pydantic import BaseModel


class CategoryRequest(BaseModel):
    amount: int
    merchantName: str
    transactedAt: str

class CategoryResponse(BaseModel):
    transactionId: Optional[int] = None
    amount: int
    merchantName: str
    transactedAt: datetime
    majorId: int
    subId: int
    isFixed: bool         # 고정비 1, 변동비 0

class CategoriesResponse(BaseModel):
    transactions: List[CategoryResponse]
