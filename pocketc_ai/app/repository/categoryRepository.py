from __future__ import annotations

import pandas as pd
from pandas import DataFrame
from sqlalchemy.orm.session import Session

from app.models.category import SubCategory


class SubCategoryRepository:
    def __init__(self, db: Session):
        self.db = db

    def get_all_sub(self) -> list[type[SubCategory]]:
        return self.db.query(SubCategory).all()

    def get_category(self, sub_name: str) -> SubCategory | None:
        return self.db.query(SubCategory).filter(SubCategory.sub_name == sub_name).first()

    def get_sub_category_by_id(self, sub_id: int) -> SubCategory | None:
        return self.db.query(SubCategory).filter(SubCategory.sub_id == sub_id).first()

    def get_names_by_ids(self, sub_ids: list[int]) -> list[str]:
        if not sub_ids: return []

        rows = (self.db.query(SubCategory.sub_name)
                .filter(SubCategory.sub_id.in_(sub_ids))
                .all())
        return [name for (name,) in rows]

    def get_all_sub_as_df(self) -> DataFrame:
        query = self.db.query(SubCategory.sub_id, SubCategory.sub_name, SubCategory.is_fixed)
        return pd.read_sql(query.statement, self.db.bind)