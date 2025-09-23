from datetime import datetime
from typing import Optional

from pydantic import BaseModel


class CategoryRequest(BaseModel):
    amount: int
    merchantName: str
    transactedAt: datetime

class Transaction(BaseModel):
    amount: int
    merchantName: str
    transactedAt: datetime
    majorId: int
    subId: int
    isFixed: bool         # 고정비 1, 변동비 0

class CategoryResponse(BaseModel):
    status: int
    message: str
    data: Optional[Transaction] = None
