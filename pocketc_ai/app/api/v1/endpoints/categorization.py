import logging
from typing import Any
from typing import Dict

from fastapi import APIRouter, Depends
from sqlalchemy.orm.session import Session

from pocketc_ai.app.db.session import get_db
from pocketc_ai.app.schemas.transaction import CategoryResponse, CategoryRequest
from pocketc_ai.app.services.categorize.categorization import CategoryService

router = APIRouter()

@router.get("/")
def get_transactions() :
    return "categories"


def get_category_service(db: Session = Depends(get_db)) -> CategoryService:
    return CategoryService(db, fallback="기타")

@router.post("/categories", response_model=CategoryResponse, status_code=201)
def create_category(
        req: CategoryRequest,
        service: CategoryService = Depends(get_category_service)) -> CategoryResponse:
    try:
        data = service.create_category(req)
        payload: Dict[str, Any] = {
            "status": 201,
            "message": "카테고리 생성 완료",
            "data": data,
        }
        return CategoryResponse(**payload)
    except Exception as e:
        logging.warning(f"An error occurred: {e}")
        return CategoryResponse(status=400, message="오류")
