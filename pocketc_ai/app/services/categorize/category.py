from datetime import datetime

from requests.sessions import Session

from app.repository.categoryRepository import SubCategoryRepository
from app.schemas.transaction import CategoryRequest, CategoryResponse
from app.services.categorize.category_lib import REGEX_RULES
from app.services.categorize.rules.chain import Categorizer
from app.services.categorize.rules.heuristics import CafeRule, ConvenienceRule, DiningTimeRule
from app.services.categorize.rules.regex_rule import RegexRule


class Categorization:
    def __init__(self, fallback: str = "기타 지출"):
        self._categorizer = Categorizer(
            rules=[
                RegexRule(REGEX_RULES),
                CafeRule(),
                ConvenienceRule(),
                DiningTimeRule(),
            ],
            fallback=fallback,
        )

    def classify(self, merchant: str, amount: int, trans_time: datetime) -> str:
        return self._categorizer.classify(merchant, amount, trans_time.hour)


class CategoryService:
    def __init__(self, db: Session, fallback: str = "기타 지출"):
        self.db = db
        self.repo = SubCategoryRepository(db)
        self.categorizer = Categorization(fallback=fallback)

    def create_category(self, req: CategoryRequest) -> CategoryResponse:
        transactedAt = datetime.fromisoformat(req.transactedAt)
        pred = self.categorizer.classify(req.merchantName, req.amount, transactedAt)
        category = self.repo.get_category(pred)

        return CategoryResponse(
            amount=req.amount,
            merchantName=req.merchantName,
            transactedAt=transactedAt,
            majorId=category.major_id,
            subId=category.sub_id,
            isFixed=category.is_fixed,
        )
