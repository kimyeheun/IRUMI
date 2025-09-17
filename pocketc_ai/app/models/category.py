from sqlalchemy import Column, Integer, String, Boolean, ForeignKey, text
from sqlalchemy.orm import relationship

from pocketc_ai.app.db.base import Base


class MajorCategory(Base):
    __tablename__ = "major_categories"

    major_id = Column(Integer, primary_key=True, autoincrement=True)
    major_name = Column(String(50), nullable=False)
    is_fixed = Column(Boolean, nullable=False, server_default=text('TRUE'))

    sub_categories = relationship("SubCategories", back_populates="major_category")


class SubCategories(Base):
    __tablename__ = "sub_categories"

    sub_id = Column(Integer, primary_key=True, autoincrement=True)
    major_id = Column(
        Integer,
        ForeignKey("major_categories.major_id", onupdate="CASCADE", ondelete="RESTRICT"),
        nullable=False
    )
    sub_name = Column(String(50), nullable=False)
    is_fixed = Column(Boolean, nullable=False, server_default=text('TRUE'))

    major_category = relationship("MajorCategory", back_populates="sub_categories")
