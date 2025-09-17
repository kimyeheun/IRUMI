from __future__ import annotations

from sqlalchemy.orm.session import Session

from pocketc_ai.app.models.category import SubCategories


class SubCategoryRepository:
    def __init__(self, db: Session):
        self.db = db

    def get_all_sub(self) -> list[type[SubCategories]]:
        return self.db.query(SubCategories).all()

    def get_category(self, sub_name: str) -> SubCategories | None:
        return self.db.query(SubCategories).filter(SubCategories.sub_name == sub_name).first()

    def get_sub_category_by_id(self, sub_id: int) -> SubCategories | None:
        return self.db.query(SubCategories).filter(SubCategories.sub_id == sub_id).first()
