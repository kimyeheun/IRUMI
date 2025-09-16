from fastapi import APIRouter
from pocketc_ai.app.api.v1.endpoints import categorization

api_router = APIRouter()
api_router.include_router(categorization.router, prefix="/categories", tags=["categorization"])