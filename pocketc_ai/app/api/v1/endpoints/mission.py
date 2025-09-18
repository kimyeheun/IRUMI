from typing import Any
from typing import Dict

from fastapi import APIRouter, Response, Depends
from sqlalchemy.orm.session import Session

from pocketc_ai.app.db.session import get_db
from pocketc_ai.app.repository.category import SubCategoryRepository
from pocketc_ai.app.schemas.transaction import CategoryResponse, CategoryRequest, Transaction
from pocketc_ai.app.services.categorize.categorization import CategorizationService

router = APIRouter()

