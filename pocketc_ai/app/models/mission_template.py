from datetime import datetime

from sqlalchemy.sql.schema import Column
from sqlalchemy.sql.sqltypes import Integer, String, JSON, TIMESTAMP

from pocketc_ai.app.db.base import Base


class MissionTemplate(Base):
    __tablename__ = "mission_template"

    template_id = Column(Integer, primary_key=True),
    name = Column(String(100), nullable=False),
    mission = Column(String(200), nullable=False),
    placeholders = Column(JSON, nullable=False),
    dsl = Column(JSON, nullable=False),
    version = Column(Integer, nullable=False, default=1),
    created_at = Column(TIMESTAMP, nullable=False, default=datetime.now()),
