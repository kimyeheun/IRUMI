from __future__ import annotations

from fastapi import Depends
from requests.sessions import Session

from pocketc_ai.app.db.session import get_db
from pocketc_ai.app.services.categorize.category import CategoryService


def get_category_service(db: Session = Depends(get_db)) -> CategoryService:
    return CategoryService(db, fallback="기타")
