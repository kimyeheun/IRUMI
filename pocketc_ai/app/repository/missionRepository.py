from typing import Optional

from sqlalchemy.orm.session import Session

from app.models.mission import MissionTemplate


class MissionRepository:
    def __init__(self, db: Session):
        self.db = db

    def get_by_code(self, code: str) -> Optional[MissionTemplate]:
        return self.db.query(MissionTemplate).filter(MissionTemplate.name == code).limit(1).first()

    def get_all_mission_template(self) -> list[type[MissionTemplate]]:
        return self.db.query(MissionTemplate).all()

    def get_mission_template_by_id(self, template_id: int) -> MissionTemplate | None:
        return (self.db.query(MissionTemplate)
                .filter(MissionTemplate.template_id == template_id)
                .first())
