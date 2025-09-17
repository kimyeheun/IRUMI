from fastapi import FastAPI

from pocketc_ai.app.api.v1.routers import api_router
from pocketc_ai.app.services.batch_task import upsert_user_daily_metrics

app = FastAPI(
    title = "PocketC",
    description = "PocketC 프로젝트의 AI",
    version = "1.0.0",
)

@app.get("/health")
def health():
    return {"status": "ok"}

app.include_router(api_router, prefix="/ai")

# NOTE: 수동 트리거용(옵션): 운영 중 점검/재집계
@app.post("/admin/rebuild-daily-metrics")
def rebuild(lookback_days: int = 3):
    r = upsert_user_daily_metrics.delay(lookback_days)
    return {"task_id": r.id}