from typing import Any
from typing import Dict

from fastapi import APIRouter, Response, Depends
from sqlalchemy.orm.session import Session

from pocketc_ai.app.db.session import get_db
from pocketc_ai.app.repository.category import SubCategoryRepository
from pocketc_ai.app.schemas.transaction import CategoryResponse, CategoryRequest, Transaction
from pocketc_ai.app.services.categorize.categorization import CategorizationService

router = APIRouter()

@router.get("/categories")
def get_transactions() :
    return "categories"

@router.post("/categories", response_model=CategoryResponse, status_code=201)
def create_category(req: CategoryRequest, response: Response, db: Session = Depends(get_db)) -> CategoryResponse:
    try:
        categorizer = CategorizationService(fallback="기타")

        pred = categorizer.classify(req.merchantName, req.amount, req.transactedAt)
        repo = SubCategoryRepository(db=db)
        category = repo.get_category(pred)

        data = Transaction(
            transactionId=req.transactionId,
            amount=req.amount,
            merchantName=req.merchantName,
            transactedAt=req.transactedAt,

            majorCategory=category.major_id,
            subCategory=category.sub_id,
            isFixed=category.is_fixed,
        )
        payload: Dict[str, Any] = {
            "status": 201,
            "message": "카테고리 생성 완료",
            "data": data,
        }
        return CategoryResponse(**payload)
    except Exception as e:
        print(e)
        response.status_code = 400
        return CategoryResponse(status=400, message="오류")
