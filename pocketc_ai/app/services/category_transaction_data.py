'''
-- 최근 30일치 지표를 한 번에
SELECT
  SUM(day_count)/30.0 AS mean_daily_count,
  SUM(night_count)/NULLIF(SUM(day_count),0) AS night_ratio,
  STDDEV_SAMP(day_sum) AS daily_sum_volatility,
  -- per_txn_std는 원시 트랜잭션에서 뽑거나, 집계 시 저장(선택)
  MAX(max_per_txn) AS max_per_txn_last30
FROM user_sub_daily_metrics
WHERE user_id = :user_id
  AND sub_id  = :sub_id
  AND d >= CURDATE() - INTERVAL 29 DAY;
'''