import logging
from typing import Any
from typing import Dict

from fastapi import APIRouter, Depends

from app.schemas.mission import Missions
from app.schemas.transaction import CategoryRequest, CategoryResponse
from app.services.categorize.category_service import CategoryService, get_category_service

router = APIRouter()

@router.get("")
def get_transactions() :
    return "categories"

@router.post("", response_model=CategoryResponse, status_code=201)
def create_category(
        req: CategoryRequest,
        service: CategoryService = Depends(get_category_service)) -> CategoryResponse:
    try:
        return service.create_category(req)
    except Exception as e:
        logging.warning(f"An error occurred: {e}")
        return CategoryResponse(
            amount=req.amount,
            merchantName=req.merchantName,
            transactedAt=transactedAt,
            majorId=category.major_id,
            subId=category.sub_id,
            isFixed=category.is_fixed,
        )
