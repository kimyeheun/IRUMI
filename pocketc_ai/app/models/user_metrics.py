from sqlalchemy.orm import relationship
from sqlalchemy.sql.schema import Column, ForeignKey, Index
from sqlalchemy.sql.sqltypes import Integer, Date

from pocketc_ai.app.db.base import Base


class UserMetrics(Base):
    __tablename__ = 'user_metrics'

    user_id = Column(Integer, primary_key=True)
    sub_id = Column(
        Integer,
        ForeignKey("sub_categories.sub_id", onupdate="CASCADE", ondelete="RESTRICT"),
        primary_key=True
    )
    d = Column(Date, primary_key=True, comment="KST 기준 일자")
    day_count = Column(Integer, nullable=False, comment="하루에 몇 번")
    day_sum = Column(Integer, nullable=False, comment="총 출금액")
    night_count = Column(Integer, nullable=False, comment="새벽 (22~06) 건수")
    morning_count = Column(Integer, nullable=False, comment="아침 (07~10) 건수")
    afternoon_count = Column(Integer, nullable=False, comment="오후 (12~19) 건수")
    max_per_txn = Column(Integer, nullable=False, comment="1건 최대 금액")

    sub_category = relationship("SubCategory")

    # user_id = Column(Integer, ForeignKey("users.user_id"), primary_key=True)
    __table_args__ = (
        Index('idx_user_day', 'user_id', 'd'),
    )
