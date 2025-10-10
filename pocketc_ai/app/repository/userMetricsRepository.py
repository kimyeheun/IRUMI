import statistics as stats
from datetime import date
from datetime import datetime, timedelta
from typing import Dict

import pandas as pd
from pandas import DataFrame
from sqlalchemy import func
from sqlalchemy import text
from sqlalchemy.orm import Session

from app.models.user_metrics import UserMetrics


class UserMetricsRepository:
    def __init__(self, db: Session):
        self.db = db

    def get_all_metrics_as_df(self) -> DataFrame:
        query = self.db.query(UserMetrics)
        return pd.read_sql(query.statement, self.db.bind)

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

    def get_daily_stats_for_category(
            self,
            user_id: int,
            sub_id: int,
            now: datetime,
            days: int = 30,
    ) -> Dict[str, float]:
        start_date = (now.date() - timedelta(days=days))
        end_date = now.date() + timedelta(days=1)  # end-exclusive

        sql = text("""
                   SELECT AVG(day_count)                                           AS mean_daily_count,
                          AVG(max_per_txn)                                         AS max_per_txn_mean,
                          STDDEV_SAMP(day_sum)                                     AS daily_sum_volatility,
                          STDDEV_SAMP(COALESCE(day_sum / NULLIF(day_count, 0), 0)) AS per_txn_std
                   FROM user_metrics
                   WHERE user_id = :uid
                     AND sub_id = :sid
                     AND d >= :start_d
                     AND d < :end_d
                   """)
        row = self.db.execute(sql, {
            "uid": user_id,
            "sid": sub_id,
            "start_d": start_date,
            "end_d": end_date
        }).first()

        if row is not None and any(v is not None for v in row):
            mean_daily_count = float(row.mean_daily_count or 0.0)
            per_txn_std = float(row.per_txn_std or 0.0)
            max_per_txn_mean = float(row.max_per_txn_mean or 0.0)
            daily_sum_volatility = float(row.daily_sum_volatility or 0.0)
            return {
                "mean_daily_count": mean_daily_count,
                "per_txn_std": per_txn_std,
                "max_per_txn_mean": max_per_txn_mean,
                "daily_sum_volatility": daily_sum_volatility,
            }

        rows = self.db.execute(text("""
                                    SELECT day_count, day_sum, max_per_txn
                                    FROM user_metrics
                                    WHERE user_id = :uid
                                      AND sub_id = :sid
                                      AND d >= :start_d
                                      AND d < :end_d
                                    """), {
                                   "uid": user_id,
                                   "sid": sub_id,
                                   "start_d": start_date,
                                   "end_d": end_date
                               }).all()
        if not rows:
            return {
                "mean_daily_count": 0.0,
                "per_txn_std": 0.0,
                "max_per_txn_mean": 0.0,
                "daily_sum_volatility": 0.0,
            }

        day_counts = [float(r.day_count or 0) for r in rows]
        day_sums = [float(r.day_sum or 0) for r in rows]
        max_per_tx = [float(r.max_per_txn or 0) for r in rows]
        per_txn_avgs = [
            (s / dc) if dc and dc > 0 else 0.0
            for s, dc in zip(day_sums, day_counts)
        ]

        def _stdev(values: list[float]) -> float:
            vals = [v for v in values if v is not None]
            return float(stats.stdev(vals)) if len(vals) >= 2 else 0.0  # sample stdev

        mean_daily_count = float(sum(day_counts) / len(day_counts)) if day_counts else 0.0
        max_per_txn_mean = float(sum(max_per_tx) / len(max_per_tx)) if max_per_tx else 0.0
        daily_sum_volatility = _stdev(day_sums)
        per_txn_std = _stdev(per_txn_avgs)

        return {
            "mean_daily_count": mean_daily_count,
            "per_txn_std": per_txn_std,
            "max_per_txn_mean": max_per_txn_mean,
            "daily_sum_volatility": daily_sum_volatility,
        }

# NOTE: 가장 자주 소비한(거래일 기준) 상위 N개 카테고리를 반환합니다.
    def get_top_frequent_categories(
            self,
            user_id: int,
            now: datetime,
            days: int = 30,
            top_n: int = 3
    ) -> list[dict]:

        start_date = now.date() - timedelta(days=days)
        end_date = now.date()

        rows = (
            self.db.query(
                UserMetrics.sub_id,
                func.count(UserMetrics.d).label("active_days"),  # 소비한 날짜 수
                func.sum(UserMetrics.day_count).label("total_tx_count"),  # 총 거래 건수
                func.sum(UserMetrics.day_sum).label("total_tx_sum")  # 총 거래 금액
            )
            .filter(UserMetrics.user_id == user_id)
            .filter(UserMetrics.d.between(start_date, end_date))
            .group_by(UserMetrics.sub_id)
            .order_by(func.count(UserMetrics.d).desc())  # 소비한 날짜 수 기준으로 정렬
            .limit(top_n)
            .all()
        )

        return [
            {"sub_id": row.sub_id, "active_days": row.active_days,
             "total_tx_count": float(row.total_tx_count or 0),
             "total_tx_sum": float(row.total_tx_sum or 0),} for row in rows
        ]
