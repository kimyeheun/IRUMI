from datetime import datetime
from typing import Optional

from pydantic import BaseModel


class Category(BaseModel):
    majorCategory: int    # 대분류 코드 (MajorCategory)
    subCategory: int      # 소분류 코드 (SubCategory)
    isFixed: bool         # 고정비 1, 변동비 0
    subCategoryName: str

class CategoryRequest(BaseModel):
    transactionId: int
    amount: int
    merchantName: str
    transactedAt: datetime

class Transaction(BaseModel):
    transactionId: int
    amount: int
    merchantName: str
    transactedAt: datetime
    majorCategory: int    # 대분류 코드 (MajorCategory)
    subCategory: int      # 소분류 코드 (SubCategory)
    isFixed: bool         # 고정비 1, 변동비 0

class CategoryResponse(BaseModel):
    status: int
    message: str
    data: Optional[Transaction] = None
