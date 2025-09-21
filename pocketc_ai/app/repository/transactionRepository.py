from datetime import datetime, timedelta

import pandas as pd
from pandas import DataFrame
from sqlalchemy.orm.session import Session

from app.models.transaction import Transaction


class TransactionRepository:
    def __init__(self, db: Session):
        self.db = db

    def get_all_transactions(self) ->  list[type[Transaction]] :
        return self.db.query(Transaction).all()

    def get_all_transactions_as_df(self) -> DataFrame:
        query = self.db.query(Transaction)
        return pd.read_sql(query.statement, self.db.bind)

    def get_user_week_transactions(self, user_id:int, now:datetime) -> list[type[Transaction]]:
        start_date = now - timedelta(days=7)
        return (
            self.db.query(Transaction)
            .filter(Transaction.transacted_at.between(start_date, now))
            .filter(Transaction.user_id == user_id)
            .all()
        )

    def get_user_term_transactions(self, user_id:int, now:datetime, days: int) -> list[type[Transaction]]:
        start_date = now - timedelta(days=days)
        return (
            self.db.query(Transaction)
            .filter(Transaction.transacted_at.between(start_date, now))
            .filter(Transaction.user_id == user_id)
            .all()
        )

    def get_user_term_transactions_as_df(self, user_id:int, now:datetime, days: int) -> DataFrame:
        start_date = now - timedelta(days=days)
        query = (self.db.query(Transaction)
            .filter(Transaction.transacted_at.between(start_date, now))
            .filter(Transaction.user_id == user_id)
        )
        return pd.read_sql(query.statement, self.db.bind)
