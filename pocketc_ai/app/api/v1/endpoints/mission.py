import logging
from datetime import datetime
from types import NoneType
from typing import Dict, Any

from fastapi import APIRouter, Depends

from app.schemas.mission import MissionResponse
from app.services.mission.mission import MissionService
from app.services.mission.mission_service import get_mission_service

router = APIRouter()

@router.get("/")
def get_mission() :
    return "mission"

@router.post("/daily", response_model=MissionResponse | NoneType, status_code=201)
def create_daily_mission(
        userId: int,
        service: MissionService = Depends(get_mission_service)
):
    try:
        today = datetime.now()

        missions = service.create_daily_mission(user_id=userId, now=today)
        data = {"userId": userId, "date": today.date(), "missions": missions}

        payload: Dict[str, Any] = {
            "status": 201,
            "message": "데일리 미션 생성 완료",
            "data": data,
        }
        return MissionResponse(**payload)
    except Exception as e:
        logging.warning(f"An error occurred: {e}")
        return None