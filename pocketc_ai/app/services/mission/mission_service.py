from fastapi import Depends
from requests.sessions import Session

from app.db.session import get_db
from app.services.mission.mission import MissionService


def get_mission_service(db: Session = Depends(get_db)) -> MissionService:
    return MissionService(db)
