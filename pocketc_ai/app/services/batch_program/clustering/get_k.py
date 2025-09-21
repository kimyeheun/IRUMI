import pandas as pd
from kneed import KneeLocator
from sklearn.cluster import KMeans


def find_optimal_k_with_elbow(X: pd.DataFrame, max_k: int = 10) -> int:
    k_range = range(2, max_k + 1)
    inertias = []

    for k in k_range:
        km = KMeans(n_clusters=k, random_state=42, n_init='auto')
        km.fit(X)
        inertias.append(km.inertia_)

    # kneed를 사용해 elbow 지점을 자동으로 찾기
    knee = KneeLocator(
        x=k_range,
        y=inertias,
        curve="convex",  # 곡선 모양 (감소하는 볼록 곡선)
        direction="decreasing"  # 곡선 방향 (감소)
    )
    return knee.elbow
