import json
from datetime import datetime
from pathlib import Path
from typing import Dict, Any

import joblib
import pandas as pd

from app.repository.transactionRepository import TransactionRepository
from app.services.batch_program.clustering.features import build_user_features


def cluster_for_user(cluster_path:Path, repo:TransactionRepository, user_id: int, now:datetime, days: int = 30) -> int | None:
    scaler = joblib.load(Path(cluster_path) / "scaler.joblib")
    kmeans = joblib.load(Path(cluster_path) / "kmeans.joblib")
    feature_cols = json.loads((Path(cluster_path) / "feature_cols.json").read_text(encoding="utf-8"))

    tx = repo.get_user_term_transactions_as_df(user_id=user_id, now=now, days=days)
    user_feat = build_user_features(tx)

    X = user_feat.reindex(columns=feature_cols, fill_value=0.0).values
    Xs = scaler.transform(X)
    cluster = int(kmeans.predict(Xs)[0])
    return cluster

def cluster_for_user_from_metrics(metrics: pd.DataFrame) -> pd.DataFrame:
    if metrics.empty:
        return pd.DataFrame([{}])

    g = metrics.groupby("sub_id", as_index=False).agg(
        mean_day_count=("day_count", "mean"),
        day_sum_volatility=("day_sum", "std"),
        per_txn_std=("max_per_txn", "std"),
        night_count_sum=("night_count", "sum"),
        day_count_sum=("day_count", "sum"),
        max_per_txn_mean=("max_per_txn", "mean"),
    )
    g["night_ratio"] = (g["night_count_sum"] / g["day_count_sum"]).fillna(0.0)

    feat: Dict[str, Any] = {}
    for _, row in g.iterrows():
        sid = int(row["sub_id"])
        feat[f"mean_day_count__{sid}"]   = float(row["mean_day_count"])
        feat[f"daily_sum_volatility__{sid}"] = float(row["day_sum_volatility"] or 0.0)
        feat[f"per_txn_std__{sid}"]      = float(row["per_txn_std"] or 0.0)
        feat[f"night_ratio__{sid}"]      = float(row["night_ratio"])
        feat[f"max_per_txn_mean__{sid}"] = float(row["max_per_txn_mean"] or 0.0)
    return pd.DataFrame([feat])
