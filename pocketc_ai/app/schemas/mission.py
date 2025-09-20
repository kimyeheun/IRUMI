from datetime import date, datetime
from typing import List

from pydantic import BaseModel


class Mission(BaseModel):
    missionId: int
    mission: str
    subId: int       # 소분류 카테고리 보내기
    missionDsl: str  # 미션 해결을 위한 DSL
    missionType: int  # 데일리 0, 주간 1, 월간 2
    validFrom: datetime
    validTo: datetime

class Missions(BaseModel):
    userId : int
    date: date
    missions: List[Mission]

class MissionResponse(BaseModel):
    status: int
    message: str
    data: Missions
