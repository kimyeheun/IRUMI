import os
from celery import Celery
from celery.schedules import crontab

BROKER_URL = os.getenv("CELERY_BROKER_URL", "redis://localhost:6379/0")
BACKEND_URL = os.getenv("CELERY_RESULT_BACKEND", "redis://localhost:6379/1")

celery = Celery("pocketc_ai", broker=BROKER_URL, backend=BACKEND_URL)

celery.conf.update(
    timezone="Asia/Seoul",
    enable_utc=True,
    task_always_eager=False,
    task_serializer="json",
    accept_content=["json"],
    result_serializer="json",
    broker_connection_retry_on_startup=True,
)

celery.conf.update(
    include=[
        "app.task.cluster_service",
        "app.task.metrics_service",
    ],
)

celery.conf.beat_schedule = {
    "upsert-user-metrics-daily-batch_program": {
        "task": "app.tasks.user_metrics",
        "schedule": crontab(),
        "args": [3],
    },
}
