import logging
from typing import Any
from typing import Dict

from fastapi import APIRouter, Depends

from pocketc_ai.app.schemas.transaction import CategoryResponse, CategoryRequest
from pocketc_ai.app.services.categorize.category_service import CategoryService, get_category_service

router = APIRouter()

@router.get("/")
def get_transactions() :
    return "categories"

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
        return CategoryResponse(status=400, message=f"{e}")
