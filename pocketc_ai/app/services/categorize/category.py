from datetime import datetime

from requests.sessions import Session

from pocketc_ai.app.repository.categoryRepository import SubCategoryRepository
from pocketc_ai.app.schemas.transaction import CategoryRequest, Transaction
from pocketc_ai.app.services.categorize.category_lib import REGEX_RULES
from pocketc_ai.app.services.categorize.rules.chain import Categorizer
from pocketc_ai.app.services.categorize.rules.heuristics import CafeRule, ConvenienceRule, DiningTimeRule
from pocketc_ai.app.services.categorize.rules.regex_rule import RegexRule


class Categorization:
    def __init__(self, fallback: str = "기타"):
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
    def __init__(self, db: Session, fallback: str = "기타"):
        self.db = db
        self.repo = SubCategoryRepository(db)
        self.categorizer = Categorization(fallback=fallback)

    def create_category(self, req: CategoryRequest) -> Transaction:
        pred = self.categorizer.classify(req.merchantName, req.amount, req.transactedAt)
        category = self.repo.get_category(pred)

        return Transaction(
            transactionId=req.transactionId,
            amount=req.amount,
            merchantName=req.merchantName,
            transactedAt=req.transactedAt,
            majorCategory=category.major_id,
            subCategory=category.sub_id,
            isFixed=category.is_fixed,
        )
