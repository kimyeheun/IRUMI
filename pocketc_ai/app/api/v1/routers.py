from fastapi import APIRouter

from app.api.v1.endpoints import categorization, mission

api_router = APIRouter()

api_router.include_router(categorization.router, prefix="/users/{userId}",tags=["categorization"])
api_router.include_router(mission.router, prefix="/missions/{userId}", tags=["missions"])
