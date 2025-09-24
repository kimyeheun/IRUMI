from celery import shared_task
from app.db.session import run_sql


UPSERT_SQL = """
-- transactions 테이블의 모든 데이터를 KST 날짜 기준으로 집계하여 user_metrics에 삽입/업데이트
INSERT INTO user_metrics (user_id, sub_id, d, day_count, day_sum, night_count, morning_count, afternoon_count, max_per_txn)
SELECT
  t.user_id,
  t.sub_id,
  -- transacted_at(UTC)을 KST 날짜로 변환하여 그룹화 및 삽입의 기준으로 삼습니다.
  DATE(CONVERT_TZ(t.transacted_at,'UTC','Asia/Seoul')) AS d,
  COUNT(*) AS day_count,
  SUM(t.amount) AS day_sum,
  -- 야간/오전/오후 집계 로직은 그대로 유지합니다.
  SUM(CASE WHEN TIME(CONVERT_TZ(t.transacted_at,'UTC','Asia/Seoul'))>='22:00:00'
        OR TIME(CONVERT_TZ(t.transacted_at,'UTC','Asia/Seoul'))<'06:00:00' THEN 1 ELSE 0 END) AS night_count,
  SUM(CASE WHEN TIME(CONVERT_TZ(t.transacted_at,'UTC','Asia/Seoul')) >= '07:00:00' 
         AND TIME(CONVERT_TZ(t.transacted_at,'UTC','Asia/Seoul')) < '11:00:00' THEN 1 ELSE 0 END) AS morning_count,
  SUM(CASE WHEN TIME(CONVERT_TZ(t.transacted_at,'UTC','Asia/Seoul')) >= '12:00:00' 
         AND TIME(CONVERT_TZ(t.transacted_at,'UTC','Asia/Seoul')) < '19:00:00' THEN 1 ELSE 0 END) AS afternoon_count,
  MAX(t.amount) AS max_per_txn
FROM transactions as t
-- 날짜 제한 없이 모든 데이터를 그룹화합니다.
GROUP BY t.user_id, t.sub_id, d
-- PK(user_id, sub_id, d)가 중복될 경우 UPDATE를 수행합니다.
ON DUPLICATE KEY UPDATE
  day_count = VALUES(day_count),
  day_sum = VALUES(day_sum),
  night_count = VALUES(night_count),
  morning_count = VALUES(morning_count),
  afternoon_count = VALUES(afternoon_count),
  max_per_txn = VALUES(max_per_txn);
"""

@shared_task(name="app.tasks.user_metrics", autoretry_for=(Exception,), retry_backoff=True, max_retries=5)
def upsert_user_metrics(lookback_days: int = 3):
    run_sql(UPSERT_SQL, [{"lookback": lookback_days}])
    return {"ok": True, "lookback": lookback_days}
