from datetime import datetime

from fastapi import Depends
from requests.sessions import Session

from pocketc_ai.app.db.session import get_db
from pocketc_ai.app.services.mission.mission import MissionService


def get_mission_service(db: Session = Depends(get_db)) -> MissionService:
    return MissionService(db)

def get_user_transactions(today: datetime):
    return