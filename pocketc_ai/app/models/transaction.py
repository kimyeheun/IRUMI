from sqlalchemy.sql.functions import func
from sqlalchemy.sql.schema import Column
from sqlalchemy.sql.sqltypes import Integer, String, DateTime, Boolean, BigInteger

from pocketc_ai.app.db.base import Base


class Transaction(Base):
    __tablename__ = "transactions"

    transaction_id = Column(Integer, primary_key=True, autoincrement=True)
    user_id = Column(Integer, nullable=False)
    sub_id = Column(Integer, nullable=False)
    major_id = Column(Integer, nullable=False)

    transacted_at = Column(DateTime, nullable=False)
    amount = Column(BigInteger, nullable=False)
    merchant_name = Column(String(255))
    is_applied = Column(Boolean, nullable=False, server_default='0')
    is_fixed = Column(Boolean, nullable=False, server_default='0')

    created_at = Column(DateTime, nullable=False, server_default=func.now())
    updated_at = Column(DateTime, nullable=False, server_default=func.now(), onupdate=func.now())
