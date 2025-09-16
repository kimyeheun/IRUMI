import os
from celery import Celery
from celery.schedules import crontab

BROKER_URL = os.getenv("CELERY_BROKER_URL", "redis://redis:6379/0")
BACKEND_URL = os.getenv("CELERY_RESULT_BACKEND", "redis://redis:6379/1")

celery = Celery("pocketc", broker=BROKER_URL, backend=BACKEND_URL)

celery.conf.update(
    timezone="Asia/Seoul",     # 주기 작업은 이 타임존 기준으로 동작
    enable_utc=True,           # 권장: 내부는 UTC, 스케줄 표시만 tz
    task_always_eager=False,   # 테스트 때만 True
    task_serializer="json",
    accept_content=["json"],
    result_serializer="json",
)

# 시간당 1회: 최근 3일 재집계(지연/정정 대비, 멱등 UPSERT)
celery.conf.beat_schedule = {
    "upsert-user-sub-daily-metrics-hourly": {
        "task": "app.tasks.upsert_user_sub_daily_metrics",
        "schedule": crontab(minute=0),  # 매 정각
        "args": [3],                    # lookback_days
    },
}
