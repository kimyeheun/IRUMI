from fastapi import FastAPI
from starlette.middleware.cors import CORSMiddleware

from app.api.v1.routers import api_router
from app.task.cluster_service import cluster_model_task
from app.task.metrics_service import upsert_user_metrics

app = FastAPI(
    title = "PocketC",
    description = "PocketC 프로젝트의 AI",
    version = "1.0.0",
    docs_url="/docs"
)

# NOTE: swagger-url CORS 오류 처리
origins = [
    "https://www.irumi.my", # 실제 서비스 도메인
    "http://localhost",
]

app.add_middleware(
    CORSMiddleware,
    allow_origins=origins,
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)

@app.get("/health")
def health():
    return {"status": "ok"}

app.include_router(api_router, prefix="/ai")

# NOTE: 수동 트리거용(옵션): 운영 중 점검/재집계
@app.post("/admin/rebuild-daily-metrics")
def rebuild_metrics(lookback_days: int = 3):
    r = upsert_user_metrics.delay(lookback_days)
    return {"task_id": r.id}

@app.post("/admin/rebuild-clusters")
def rebuild_clusters():
    r = cluster_model_task.apply_async()
    return {"task_id": r.id}