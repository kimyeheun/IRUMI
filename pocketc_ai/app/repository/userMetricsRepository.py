from datetime import timedelta, date

import pandas as pd
from pandas import DataFrame
from sqlalchemy.orm.session import Session

from pocketc_ai.app.models.user_metrics import UserMetrics


class UserMetricsRepository:
    def __init__(self, db: Session):
        self.db = db

    def get_user_metrics(self, user_id: int) ->  list[type[UserMetrics]] :
        return self.db.query(UserMetrics).filter(UserMetrics.user_id == user_id).all()

    def get_user_week_metrics(self, user_id: int, now: date) -> list[type[UserMetrics]] :
        start = now - timedelta(days=7)
        return (
            self.db.query(UserMetrics)
            .filter(UserMetrics.d.between(start, now))
            .filter(UserMetrics.user_id == user_id)
            .all()
        )

    def get_user_term_metrics(self, user_id: int, now: date, days: int) -> list[type[UserMetrics]] :
        start = now - timedelta(days=days)
        return (
            self.db.query(UserMetrics)
            .filter(UserMetrics.d.between(start, now))
            .filter(UserMetrics.user_id == user_id)
            .all()
        )

    def get_user_term_metrics_as_df(self, user_id: int, now: date, days: int) -> DataFrame :
        start = now - timedelta(days=days)
        query = ((self.db.query(UserMetrics)
                 .filter(UserMetrics.d.between(start, now))
                 .filter(UserMetrics.user_id == user_id)))
        return pd.read_sql(query.statement, self.db.bind)

    def get_daily_stats_for_category(self, user_id, sub_id, now):
        pass
