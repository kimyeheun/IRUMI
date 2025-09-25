import logging
from datetime import datetime
from types import NoneType
from typing import Dict, Any

from fastapi import APIRouter, Depends

from app.schemas.mission import MissionResponse
from app.services.mission.mission import MissionService
from app.services.mission.mission_service import get_mission_service
from app.utils.buf_data import BUF_MONTHLY_MISSION, BUF_WEEKLY_MISSION

router = APIRouter()

@router.get("/")
def get_mission() :
    return "mission"

@router.get("/daily", response_model=MissionResponse | NoneType, status_code=201)
def create_daily_mission(
        userId: int,
        service: MissionService = Depends(get_mission_service)
):
    today = datetime.now()

    try:
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
        data = {"userId": userId, "date": today.date(), "missions": []}

        payload: Dict[str, Any] = {
            "status": 201,
            "message": "데일리 미션 생성 완료 - 비어 있음!",
            "data": data,
        }
        return MissionResponse(**payload)

@router.get("/weekly", response_model=MissionResponse | NoneType, status_code=201)
def create_weekly_mission(
        userId: int,
        service: MissionService = Depends(get_mission_service)
):
    today = datetime.now()
    try:
        missions = service.create_weekly_mission(user_id=userId, now=today)
        data = {"userId": userId, "date": today.date(), "missions": missions}

        payload: Dict[str, Any] = {
            "status": 201,
            "message": "주간 미션 생성 완료",
            "data": data,
        }
        return MissionResponse(**payload)
    except Exception as e:
        logging.warning(f"An error occurred: {e}")

        BUF_WEEKLY_MISSION[0]["validFrom"] = today.replace(hour=0, minute=0, second=0, microsecond=0)
        BUF_WEEKLY_MISSION[0]["validTo"] = today.replace(hour=23, minute=59, second=59, microsecond=999999)
        data = {"userId": userId, "date": today.date(), "missions": BUF_WEEKLY_MISSION}

        payload: Dict[str, Any] = {
            "status": 201,
            "message": "주간 미션 생성 완료 - 비어 있음!",
            "data": data,
        }
        return MissionResponse(**payload)

@router.get("/monthly", response_model=MissionResponse | NoneType, status_code=201)
def create_monthly_mission(
        userId: int,
        service: MissionService = Depends(get_mission_service)
):
    today = datetime.now()
    try:
        missions = service.create_monthly_mission(user_id=userId, now=today)
        data = {"userId": userId, "date": today.date(), "missions": missions}

        payload: Dict[str, Any] = {
            "status": 201,
            "message": "월간 미션 생성 완료",
            "data": data,
        }
        return MissionResponse(**payload)
    except Exception as e:
        logging.warning(f"An error occurred: {e}")
        BUF_MONTHLY_MISSION[0]["validFrom"] = today.replace(hour=0, minute=0, second=0, microsecond=0)
        BUF_MONTHLY_MISSION[0]["validTo"] = today.replace(hour=23, minute=59, second=59, microsecond=999999)
        data = {"userId": userId, "date": today.date(), "missions": BUF_MONTHLY_MISSION}

        payload: Dict[str, Any] = {
            "status": 201,
            "message": "월간 미션 생성 완료 - 비어 있음!",
            "data": data,
        }
        return MissionResponse(**payload)