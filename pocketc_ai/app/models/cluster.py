from sqlalchemy.orm import Mapped, mapped_column, DeclarativeBase
from sqlalchemy.sql.sqltypes import Integer

class Base(DeclarativeBase):
    pass

class Cluster(Base):
    __tablename__ = "cluster"

    cluster_id: Mapped[int] = mapped_column(Integer, primary_key=True)
    sub_id:     Mapped[int] = mapped_column(Integer, primary_key=True)
