from datetime import datetime

from pydantic import BaseModel


class CategoryRequest(BaseModel):
    amount: int
    merchantName: str
    transactedAt: str

class CategoryResponse(BaseModel):
    amount: int
    merchantName: str
    transactedAt: datetime
    majorId: int
    subId: int
    isFixed: bool         # 고정비 1, 변동비 0
