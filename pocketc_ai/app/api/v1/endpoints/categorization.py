import logging

from fastapi import APIRouter, Depends

from app.schemas.transaction import CategoryRequest, CategoryResponse, CategoriesResponse
from app.services.categorize.category_service import CategoryService, get_category_service

router = APIRouter()

@router.get("")
def get_transactions() :
    return "categories"

@router.post("/{userId}", response_model=CategoriesResponse, status_code=201)
def create_categories(
        userId: int,
        service: CategoryService = Depends(get_category_service)) -> CategoriesResponse | None:
    try:
        return service.create_categories(userId)
    except Exception as e:
        logging.warning(f"An error occurred: {e}")

@router.post("", response_model=CategoryResponse, status_code=201)
def create_category(
        req: CategoryRequest,
        service: CategoryService = Depends(get_category_service)) -> CategoryResponse:
    try:
        return service.create_category(req)
    except Exception as e:
        logging.warning(f"An error occurred: {e}")
        from datetime import datetime
        return CategoryResponse(
            amount=req.amount,
            merchantName=req.merchantName,
            transactedAt=datetime.fromisoformat(req.transactedAt),
            majorId=10,
            subId=39,
            isFixed=False,
        )
