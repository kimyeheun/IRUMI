from datetime import datetime

from sqlalchemy.sql.schema import Column, ForeignKey
from sqlalchemy.sql.sqltypes import Integer, String, JSON, TIMESTAMP

from app.db.base import Base


class Mission(Base):
    __tablename__ = "missions"

    mission_id = Column(Integer, primary_key=True, autoincrement=True)
    user_id = Column(Integer, ForeignKey('user.user_id'), nullable=False)
    template_id = Column(Integer, ForeignKey('template.template_id'), nullable=False)
    sub_id = Column(Integer, ForeignKey('sub.sub_id'), nullable=False)
    params = Column(JSON, nullable=False)
    dsl = Column(JSON, nullable=False)
    compiled_plan = Column(JSON, nullable=False)
    mission = Column(String(200), nullable=False)


class MissionTemplate(Base):
    __tablename__ = "mission_templates"

    template_id = Column(Integer, primary_key=True, autoincrement=True)
    name = Column(String(100), nullable=False)
    mission = Column(String(200), nullable=False)
    placeholders = Column(JSON, nullable=False)
    dsl = Column(JSON, nullable=False)
    version = Column(Integer, nullable=False, default=1)
    created_at = Column(TIMESTAMP, nullable=False, default=datetime.now())
