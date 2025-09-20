import StandardScaler
import pandas as pd
from celery import shared_task
from sklearn.cluster import KMeans
from sklearn.preprocessing import StandardScaler

from pocketc_ai.app.db.session import run_sql, get_db
from pocketc_ai.app.repository.categoryRepository import SubCategoryRepository
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

def get_cluster_data_from_model(repo: TransactionRepository, sub: SubCategoryRepository) -> list[dict]:
    tx = repo.get_all_transactions_as_df()
    user_feat = build_user_features(tx)

    scaler = StandardScaler()
    X = scaler.fit_transform(user_feat.values)
    best_k = find_optimal_k_with_elbow(X)

    km = KMeans(n_clusters=best_k, random_state=42, n_init='auto')
    labels = km.fit_predict(X)

    assign = pd.DataFrame({"user_id": user_feat.index, "cluster": labels})
    prof = cluster_category_profile(tx, assign)

    scored = score_categories(
        prof,
        w_share=0.5,
        w_trend=0.3,
        w_ticket=0.2,
        min_cnt=3,
        min_share=0.015
    )

    # 1. is_fixed 정보를 DB에서 가져옵니다.
    sub_cat_df = sub.get_all_sub_as_df()
    # 2. 점수 데이터에 is_fixed 정보를 병합합니다.
    scored_with_fixed_info = scored.merge(sub_cat_df[['sub_id', 'is_fixed']], on="sub_id", how="left")
    # 3. 고정비(is_fixed=1) 카테고리를 제외하고, 변동비만 남깁니다.
    variable_expenses_scored = scored_with_fixed_info[scored_with_fixed_info["is_fixed"] == 0]

    top3_by_cluster = variable_expenses_scored.groupby("cluster").head(3)

    data = []
    for _, row in top3_by_cluster.iterrows():
        data.append({
            "cluster_id": row["cluster"],
            "sub_id": row["sub_id"]
        })
    return data

@shared_task(name="app.tasks.cluster_model", autoretry_for=(Exception,), retry_backoff=True, max_retries=5)
def cluster_model_task():
    db = next(get_db())
    try:
        run_sql(TRUNCATE_SQL)

        repo = TransactionRepository(db)
        sub = SubCategoryRepository(db)
        cluster_data = get_cluster_data_from_model(repo, sub)
        if cluster_data:
            run_sql(INSERT_SQL, cluster_data)
        return {"ok": True, "inserted_rows": len(cluster_data)}
    finally:
        db.close()
