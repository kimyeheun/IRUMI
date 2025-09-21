from celery import shared_task
from app.db.session import run_sql

UPSERT_SQL = """
INSERT INTO user_metrics (user_id, sub_id, d, day_count, day_sum, night_count, morning_count, afternoon_count, max_per_txn)
WITH kst AS (
  SELECT DATE(CONVERT_TZ(UTC_TIMESTAMP(),'UTC','Asia/Seoul')) AS today_kst
),
bounds AS (
  SELECT
    DATE_SUB(today_kst, INTERVAL 1 DAY) AS d_kst,
    CONVERT_TZ(CONCAT(DATE_SUB(today_kst, INTERVAL 1 DAY),' 00:00:00'),'Asia/Seoul','UTC') AS start_utc,
    CONVERT_TZ(CONCAT(DATE_SUB(today_kst, INTERVAL 0 DAY),' 00:00:00'),'Asia/Seoul','UTC') AS end_utc
  FROM kst
)
SELECT
  t.user_id,
  t.sub_id,
  DATE(CONVERT_TZ(t.transacted_at,'UTC','Asia/Seoul')) AS d_kst,
  COUNT(*) AS day_count,
  SUM(t.amount) AS day_sum,
  SUM(CASE WHEN TIME(CONVERT_TZ(t.transacted_at,'UTC','Asia/Seoul'))>='22:00:00'
           OR TIME(CONVERT_TZ(t.transacted_at,'UTC','Asia/Seoul'))<'06:00:00' THEN 1 ELSE 0 END) AS night_count,
  SUM(CASE WHEN TIME(CONVERT_TZ(t.transacted_at,'UTC','Asia/Seoul')) >= '07:00:00' 
            AND TIME(CONVERT_TZ(t.transacted_at,'UTC','Asia/Seoul')) < '11:00:00' THEN 1 ELSE 0 END) AS morning_count,
  SUM(CASE WHEN TIME(CONVERT_TZ(t.transacted_at,'UTC','Asia/Seoul')) >= '12:00:00' 
            AND TIME(CONVERT_TZ(t.transacted_at,'UTC','Asia/Seoul')) < '19:00:00' THEN 1 ELSE 0 END) AS afternoon_count,
  MAX(t.amount) AS max_per_txn
FROM transactions t
JOIN bounds b
  ON t.transacted_at >= b.start_utc AND t.transacted_at < b.end_utc
GROUP BY t.user_id, t.sub_id, d_kst
ON DUPLICATE KEY UPDATE
  day_count = VALUES(day_count),
  day_sum   = VALUES(day_sum),
  night_count = VALUES(night_count),
  max_per_txn = VALUES(max_per_txn);
"""

@shared_task(name="app.tasks.user_metrics", autoretry_for=(Exception,), retry_backoff=True, max_retries=5)
def upsert_user_metrics(lookback_days: int = 3):
    run_sql(UPSERT_SQL, [{"lookback": lookback_days}])
    return {"ok": True, "lookback": lookback_days}
