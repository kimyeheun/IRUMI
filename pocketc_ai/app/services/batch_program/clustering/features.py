from __future__ import annotations

import numpy as np
import pandas as pd


def _to_dt(df: pd.DataFrame) -> pd.DataFrame:
    df = df.copy()
    df["transacted_at"] = pd.to_datetime(df["transacted_at"])
    return df

def _time_flags(df: pd.DataFrame) -> pd.DataFrame:
    df = df.copy()
    dt = pd.to_datetime(df["transacted_at"])
    df["dow"] = dt.dt.weekday              # 0=Mon ... 6=Sun
    df["hour"] = dt.dt.hour
    df["is_weekend"] = df["dow"] >= 5
    bins  = [0, 6, 11, 14, 18, 22, 24]
    labels = ["h00_06","h06_11","h11_14","h14_18","h18_22","h22_24"]
    # (옵션) 카테고리로 고정해 피벗 시 모든 구간 보장
    df["hour_bin"] = pd.Categorical(pd.cut(df["hour"], bins=bins, right=False, labels=labels, include_lowest=True),
                                    categories=labels, ordered=True)
    return df

def _safe_div(a, b):
    return (a / b) if b != 0 else 0.0

def _hhi(shares: np.ndarray) -> float:
    # Herfindahl–Hirschman Index: sum(p_i^2)
    return float(np.sum(np.square(shares)))

def _entropy(shares: np.ndarray, eps: float = 1e-12) -> float:
    p = shares[shares > 0]
    return float(-np.sum(p * np.log(p + eps)))

def _interpurchase_hours_series(s: pd.Series) -> float:
    # FIX: Series 전용 구현 (경고/형 변환 이슈 제거)
    dt = pd.to_datetime(s).sort_values()
    if len(dt) < 2:
        return np.nan
    diff_h = (dt.diff().dropna().dt.total_seconds() / 3600.0).values
    return float(np.mean(diff_h))

def build_user_features(
    tx: pd.DataFrame,
    sub_id_universe: list[str] | None = None,
    include_amount_stats: bool = True,
    include_recency_trend: bool = True,
    include_time_distributions: bool = True,
    include_diversity_concentration: bool = True,
) -> pd.DataFrame:
    assert {"user_id","sub_id","transacted_at","amount"}.issubset(tx.columns), \
        "tx must have columns: user_id, sub_id, transacted_at, amount"

    tx = _to_dt(tx)
    tx = _time_flags(tx)

    base = tx.groupby("user_id").agg(
        total_spend_30d=("amount","sum"),
        total_cnt_30d=("amount","size"),
        last_dt=("transacted_at","max"),
    )

    # 금액 통계 (전체)
    if include_amount_stats:
        stats_all = tx.groupby("user_id").agg(
            stat_avg=("amount", "mean"),
            stat_med=("amount", "median"),
            stat_std=("amount", "std"),
            stat_max=("amount", "max"),
            stat_q75=("amount", lambda s: s.quantile(0.75)),
        )
        stats_all["stat_cv"] = stats_all["stat_std"] / (stats_all["stat_avg"] + 1e-9)
        base = base.join(stats_all)

        # 고가 거래 비율(개인 75분위 초과 비중)
        hi = tx.merge(
            stats_all[["stat_q75"]], left_on="user_id", right_index=True, how="left"
        )
        hi["is_high"] = hi["amount"] > hi["stat_q75"]
        high_ratio = hi.groupby("user_id")["is_high"].mean().rename("stat_high_ratio")
        base = base.join(high_ratio)

    # 금액 share
    spend_by_sub = tx.groupby(["user_id","sub_id"])["amount"].sum().rename("spend")
    spend_pvt = spend_by_sub.reset_index().pivot_table(
        index="user_id", columns="sub_id", values="spend", fill_value=0.0, observed=False  # FIX: observed 명시
    )
    # 빈도 share
    freq_by_sub = tx.groupby(["user_id","sub_id"])["amount"].size().rename("freq")
    freq_pvt = freq_by_sub.reset_index().pivot_table(
        index="user_id", columns="sub_id", values="freq", fill_value=0.0, observed=False  # FIX
    )

    # 유니버스 고정(없으면 발견된 sub_id 사용)
    if sub_id_universe is None:
        sub_cols = spend_pvt.columns.tolist()
    else:
        sub_cols = list(sub_id_universe)
        for col in sub_cols:
            if col not in spend_pvt.columns:
                spend_pvt[col] = 0.0
            if col not in freq_pvt.columns:
                freq_pvt[col] = 0.0
        spend_pvt = spend_pvt[sub_cols]
        freq_pvt  = freq_pvt[sub_cols]

    spend_share = spend_pvt.div(spend_pvt.sum(axis=1).replace(0, np.nan), axis=0).fillna(0.0)
    freq_share  = freq_pvt.div(freq_pvt.sum(axis=1).replace(0, np.nan), axis=0).fillna(0.0)

    spend_share.columns = [f"sub_spend_share::{c}" for c in spend_share.columns]
    freq_share.columns  = [f"sub_freq_share::{c}"  for c in freq_share.columns]

    # Top-N 집중도
    top1 = spend_share.apply(lambda r: float(np.sort(r.values)[::-1][:1].sum()), axis=1).rename("sub_top1_spend")
    top3 = spend_share.apply(lambda r: float(np.sort(r.values)[::-1][:3].sum()), axis=1).rename("sub_top3_spend")

    # HHI/Entropy
    if include_diversity_concentration:
        hhi = spend_share.apply(lambda r: _hhi(r.values), axis=1).rename("div_hhi_spend")
        ent = spend_share.apply(lambda r: _entropy(r.values), axis=1).rename("div_entropy_spend")

    if include_time_distributions:
        # 시간대(금액 기준 비중)
        spend_hour = tx.pivot_table(index="user_id", columns="hour_bin", values="amount",
                                    aggfunc="sum", fill_value=0.0, observed=False)  # FIX
        spend_hour = spend_hour.div(spend_hour.sum(axis=1).replace(0, np.nan), axis=0).fillna(0.0)
        spend_hour.columns = [f"time_spend_share::{c}" for c in spend_hour.columns]

        # 요일(금액 기준 비중)
        spend_dow = tx.pivot_table(index="user_id", columns="dow", values="amount",
                                   aggfunc="sum", fill_value=0.0, observed=False)  # FIX
        spend_dow = spend_dow.div(spend_dow.sum(axis=1).replace(0, np.nan), axis=0).fillna(0.0)
        spend_dow.columns = [f"time_spend_share::dow{c}" for c in spend_dow.columns]

        weekend_share = tx.groupby("user_id")["is_weekend"].mean().rename("time_weekend_ratio")

    if include_recency_trend:
        tmax = tx["transacted_at"].max()
        cut7  = tmax - pd.Timedelta(days=7)
        cut14 = tmax - pd.Timedelta(days=14)
        cut28 = tmax - pd.Timedelta(days=28)

        spend7  = tx[tx["transacted_at"] >= cut7].groupby("user_id")["amount"].sum().rename("rec_spend_7d")
        spend14 = tx[(tx["transacted_at"] >= cut14) & (tx["transacted_at"] < cut7)].groupby("user_id")["amount"].sum().rename("rec_spend_prev7")
        spend_prev14 = tx[(tx["transacted_at"] < cut14) & (tx["transacted_at"] >= cut28)].groupby("user_id")["amount"].sum().rename("rec_spend_prev14")

        rec = pd.concat([spend7, spend14, spend_prev14], axis=1).fillna(0.0)
        # 7일/30일 비중, 모멘텀(최근7 vs 그 전 7)
        rec["rec_7_over_30"] = rec["rec_spend_7d"] / (base["total_spend_30d"] + 1e-9)
        rec["trend_7_vs_prev7"] = np.where(
            rec["rec_spend_prev7"] > 0,
            (rec["rec_spend_7d"] - rec["rec_spend_prev7"]) / rec["rec_spend_prev7"],
            np.nan
        )
        days_since_last = (tmax - base["last_dt"]).dt.days.rename("rec_days_since_last")

        # FIX: 그룹화는 Series로, 이름은 agg에서 바로 부여(경고/에러 제거)
        mean_gap_h = tx.groupby("user_id")["transacted_at"].agg(
            rec_mean_gap_hours=_interpurchase_hours_series
        )

    parts = [base.drop(columns=["last_dt"], errors="ignore"), spend_share, freq_share, top1, top3]
    if include_diversity_concentration:
        parts += [hhi, ent]
    if include_time_distributions:
        parts += [spend_hour, spend_dow, weekend_share]
    if include_recency_trend:
        parts += [rec[["rec_7_over_30", "trend_7_vs_prev7"]], days_since_last, mean_gap_h]

    feats = pd.concat(parts, axis=1)
    feats = feats.replace([np.inf, -np.inf], np.nan).fillna(0.0)
    feats = feats.sort_index(axis=1)
    return feats
