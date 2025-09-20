import json
import os
from datetime import datetime, timedelta
from pathlib import Path

import joblib
import numpy as np
import pandas as pd
from kneed import KneeLocator
from sklearn.cluster import KMeans
from sklearn.preprocessing import StandardScaler

# 중요: 이 스크립트를 프로젝트 루트에서 실행하려면 경로 설정이 필요할 수 있습니다.
# 예: sys.path.append(os.getcwd())
from pocketc_ai.app.db.session import get_db
from pocketc_ai.app.repository.categoryRepository import SubCategoryRepository
from pocketc_ai.app.repository.userMetricsRepository import UserMetricsRepository


def get_data(metrics, sub):
    print("데이터 로딩 중...")

    metrics_df = metrics.get_all_metrics_as_df()
    subs = sub.get_all_sub()
    sub_cat_df = pd.DataFrame([(s.sub_id, s.sub_name) for s in subs], columns=["sub_id", "sub_name"])

    print(f"총 {len(metrics_df)}개의 일일 지표 데이터를 로드했습니다.")
    return metrics_df, sub_cat_df


def build_features_from_metrics(df: pd.DataFrame, sub_cat_df: pd.DataFrame) -> pd.DataFrame:
    print("사용자 특징(Features) 생성 중...")

    user_summary = df.groupby('user_id').agg(
        total_spend_30d=('day_sum', 'sum'),
        total_count_30d=('day_count', 'sum'),
        spend_volatility_30d=('day_sum', 'std'),
        night_count_30d=('night_count', 'sum'),
        morning_count_30d=('morning_count', 'sum'),
        afternoon_count_30d=('afternoon_count', 'sum'),
    ).fillna(0)
    user_summary['avg_ticket_size_30d'] = user_summary['total_spend_30d'] / user_summary['total_count_30d']
    user_summary['night_ratio_30d'] = user_summary['night_count_30d'] / user_summary['total_count_30d']
    user_summary['morning_ratio_30d'] = user_summary['morning_count_30d'] / user_summary['total_count_30d']
    user_summary['afternoon_ratio_30d'] = user_summary['afternoon_count_30d'] / user_summary['total_count_30d']

    category_summary = df.groupby(['user_id', 'sub_id'])['day_sum'].sum().reset_index()
    spend_pivot = category_summary.pivot(index='user_id', columns='sub_id', values='day_sum').fillna(0)

    # 카테고리 지출 비중(Share) 계산
    spend_total = spend_pivot.sum(axis=1)
    spend_share = spend_pivot.div(spend_total, axis=0).fillna(0)

    # 소비 집중도 (Herfindahl-Hirschman Index) 계산
    user_summary['spend_hhi'] = (spend_share ** 2).sum(axis=1)

    sub_map = sub_cat_df.set_index('sub_id')['sub_name'].to_dict()
    spend_share.columns = [f'spend_share::{sub_map.get(c, "unknown")}' for c in spend_share.columns]

    final_features = user_summary.join(spend_share).fillna(0)
    final_features.drop(columns=['night_count_30d', 'morning_count_30d', 'afternoon_count_30d'], inplace=True)

    final_features.replace([np.inf, -np.inf], 0, inplace=True)

    print(f"총 {len(final_features)}명의 사용자에 대한 특징 생성을 완료했습니다.")
    return final_features.sort_index(axis=1)


def find_optimal_k(X: np.ndarray, max_k: int = 10) -> int:
    n_samples = X.shape[0]

    if n_samples < 2:
        print(f"샘플 수가 {n_samples}개로, 클러스터링을 수행할 수 없습니다. k=1을 반환합니다.")
        return 1
    effective_max_k = min(max_k, n_samples - 1)
    if effective_max_k < 2:
        print(f"샘플 수가 {n_samples}개로 너무 적어 최적 k를 찾을 수 없습니다. k={n_samples}을 반환합니다.")
        return n_samples

    print(f"최적의 클러스터 개수(k)를 찾는 중... (샘플 수: {n_samples}, k 범위: 2-{effective_max_k})")
    inertias = []
    k_range = range(2, effective_max_k + 1)
    for k in k_range:
        km = KMeans(n_clusters=k, random_state=42, n_init='auto')
        km.fit(X)
        inertias.append(km.inertia_)

    if len(inertias) < 2:
        print(f"k 탐색 범위가 너무 좁습니다. k={k_range[-1]}로 결정되었습니다.")
        return k_range[-1]

    knee = KneeLocator(x=k_range, y=inertias, curve="convex", direction="decreasing")
    best_k = knee.elbow if knee.elbow else effective_max_k
    print(f"최적의 k는 {best_k}로 결정되었습니다.")
    return best_k


def main():
    db = next(get_db())
    try:
        metrics, sub = UserMetricsRepository(db), SubCategoryRepository(db)
        metrics_df, sub_cat_df = get_data(metrics, sub)

        if metrics_df.empty:
            print("처리할 데이터가 없습니다.")
            return

        user_features = build_features_from_metrics(metrics_df, sub_cat_df)

        # 2. 특징 스케일링
        scaler = StandardScaler()
        X_scaled = scaler.fit_transform(user_features.values)

        # 3. 최적 k 탐색 및 KMeans 모델 학습
        best_k = find_optimal_k(X_scaled)
        kmeans = KMeans(n_clusters=best_k, random_state=42, n_init='auto')
        kmeans.fit(X_scaled)

        # 4. 결과물(모델, 스케일러, 특징 컬럼) 저장
        output_dir = Path("../../mission/cluster")
        output_dir.mkdir(parents=True, exist_ok=True)

        joblib.dump(kmeans, output_dir / "kmeans.joblib")
        joblib.dump(scaler, output_dir / "scaler.joblib")
        with open(output_dir / "feature_cols.json", "w", encoding="utf-8") as f:
            json.dump(user_features.columns.tolist(), f, ensure_ascii=False, indent=2)

        print("=" * 50)
        print(f"클러스터링 모델 및 설정 파일 저장 완료!")
        print(f"경로: {output_dir.absolute()}")
        print(f"  - kmeans.joblib (클러스터 모델)")
        print(f"  - scaler.joblib (데이터 스케일러)")
        print(f"  - feature_cols.json (모델이 사용한 특징 목록)")
        print("=" * 50)

    finally:
        db.close()


if __name__ == "__main__":
    main()