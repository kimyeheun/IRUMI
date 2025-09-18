import StandardScaler
import pandas as pd
from celery import shared_task
from sklearn.cluster import KMeans
from sklearn.preprocessing import StandardScaler
from sqlalchemy.orm.session import Session

from pocketc_ai.app.db.session import run_sql
from pocketc_ai.app.repository.transactionRepository import TransactionRepository
from pocketc_ai.app.services.batch_program.clustering.features import build_user_features
from pocketc_ai.app.services.batch_program.clustering.get_k import find_optimal_k_with_elbow
from pocketc_ai.app.services.batch_program.clustering.scoring import cluster_category_profile, score_categories

TRUNCATE_SQL = """
TRUNCATE TABLE cluster
"""

INSERT_SQL = """
INSERT INTO cluster (cluster_id, sub_id) VALUES (:cluster_id, :sub_id)
"""


def get_cluster_data_from_model(repo: TransactionRepository) -> list[dict]:
    # TODO: 클러스터 매핑하기
    tx = repo.get_all_transactions_as_df()
    user_feat = build_user_features(tx)

    scaler = StandardScaler()
    X = scaler.fit_transform(user_feat.values)
    best_k = find_optimal_k_with_elbow(X)

    # 2) KMeans 라벨링
    km = KMeans(n_clusters=best_k, random_state=42, n_init='auto')
    labels = km.fit_predict(X)

    # 3-a) user_id ↔ cluster 매핑 만들기/저장
    assign = pd.DataFrame({"user_id": user_feat.index, "cluster": labels})
    prof = cluster_category_profile(tx, assign)

    # 프로파일을 기반으로 카테고리별 점수를 매깁니다.
    scored = score_categories(
        prof,
        w_share=0.5,
        w_trend=0.3,
        w_ticket=0.2,
        min_cnt=3,
        min_share=0.015
    )

    # 군집별 Top-3
    top3_by_cluster = scored.groupby("cluster").head(3)

    data = []
    for _, row in top3_by_cluster.iterrows():
        data.append({
            "cluster_id": row["cluster"],
            "sub_id": row["sub_id"]
        })
    return data

@shared_task(name="app.tasks.cluster_model", autoretry_for=(Exception,), retry_backoff=True, max_retries=5)
def upsert_user_daily_metrics(db: Session):
    run_sql(TRUNCATE_SQL)

    repo = TransactionRepository(db)
    cluster_data = get_cluster_data_from_model(repo)
    run_sql(INSERT_SQL, cluster_data)
    return {"ok": True, "inserted_rows": len(cluster_data)}
