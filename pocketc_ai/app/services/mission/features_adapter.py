from __future__ import annotations

from typing import Dict, List, Iterable

import numpy as np
import pandas as pd

# 기대되는 카테고리 표기(학습 시 사용했던 정확한 한글 라벨과 일치해야 함)
EXPECTED_SUB_NAMES: List[str] = [
    "OTT/구독서비스","간식","기타","대중교통","도서/교재","배달음식","병원","뷰티/미용",
    "생활용품","식재료","약국","영화/공연","온라인 쇼핑몰","외식","의류/패션","취미/오락",
    "커피","택시/대리","휴대폰 요금"
]

# 모델이 기대하는 time bins (user_metrics에 없으면 0으로 둠)
DOW_KEYS = [f"time_spend_share::dow{i}" for i in range(7)]
HOUR_KEYS = [
    "time_spend_share::h00_06","time_spend_share::h06_11",
    "time_spend_share::h11_14","time_spend_share::h14_18",
    "time_spend_share::h18_22","time_spend_share::h22_24",
]

def _safe_div(a: float, b: float) -> float:
    return float(a) / float(b) if b and float(b) != 0 else 0.0

def _entropy(p: Iterable[float]) -> float:
    arr = np.asarray([x for x in p if x > 0], dtype=float)
    if arr.size == 0:
        return 0.0
    probs = arr / arr.sum()
    return float(-(probs * np.log(probs)).sum())

def _hhi(p: Iterable[float]) -> float:
    arr = np.asarray([x for x in p if x > 0], dtype=float)
    if arr.size == 0:
        return 0.0
    probs = arr / arr.sum()
    return float((probs**2).sum())

def build_features_for_model_from_metrics(
    df: pd.DataFrame,
    sub_id_to_name: Dict[int, str],
    feature_cols: List[str],
) -> pd.DataFrame:
    """
    df: user_metrics rows in window [d, sub_id, day_count, day_sum, ...]
    sub_id_to_name: {sub_id -> "커피"} 같은 매핑 (필수)
    feature_cols: 모델 학습 시 저장한 정확한 컬럼 리스트
    """
    if df is None or df.empty:
        # 전부 0 한 행
        return pd.DataFrame([ {c: 0.0 for c in feature_cols} ])

    # sub_name 부착
    local = df.copy()
    local["sub_name"] = local["sub_id"].map(sub_id_to_name).fillna("기타")

    # 기본 합계
    total_cnt = float(local["day_count"].sum())
    total_sum = float(local["day_sum"].sum())

    # 카테고리별 집계
    grp = local.groupby("sub_name", as_index=False).agg(
        cnt=("day_count","sum"),
        spend=("day_sum","sum"),
    )

    # 점유율 벡터
    freq_share = {name: _safe_div(r.cnt, total_cnt) for _, r in grp.iterrows()}
    spend_share = {name: _safe_div(r.spend, total_sum) for _, r in grp.iterrows()}

    # 상위 지출
    top_spend = grp.sort_values("spend", ascending=False)["spend"].tolist()
    sub_top1 = float(top_spend[0]) if top_spend else 0.0
    sub_top3 = float(sum(top_spend[:3])) if top_spend else 0.0

    # 분산계수 등 통계 (일 단위 지출 합 기준)
    day_sums = local.groupby("d")["day_sum"].sum().values.astype(float)
    stat_avg = float(day_sums.mean()) if day_sums.size else 0.0
    stat_std = float(day_sums.std(ddof=1)) if day_sums.size >= 2 else 0.0
    stat_cv  = _safe_div(stat_std, stat_avg)  # coefficient of variation
    stat_max = float(day_sums.max()) if day_sums.size else 0.0
    stat_med = float(np.median(day_sums)) if day_sums.size else 0.0
    stat_q75 = float(np.percentile(day_sums, 75)) if day_sums.size else 0.0
    stat_high_ratio = _safe_div( (day_sums > stat_avg).sum(), day_sums.size ) if day_sums.size else 0.0

    # 다양성 지표
    div_entropy_spend = _entropy([r.spend for _, r in grp.iterrows()])
    div_hhi_spend     = _hhi([r.spend for _, r in grp.iterrows()])

    # 최근성/간격 (user_metrics는 일 단위이므로 '일' 단위로 근사)
    nonzero_days = sorted(local.loc[local["day_sum"] > 0, "d"].unique())
    if nonzero_days:
        last_day = pd.to_datetime(nonzero_days[-1]).date()
        rec_days_since_last = float((local["d"].max() - last_day).days)
        # 최근 30일 중 거래 있는 일수 비율(대략 rec_7_over_30 대체; 정확 히스토리는 tx에서 계산 추천)
        rec_7_over_30 = _safe_div( sum((local["d"].max() - pd.to_datetime(d).date()).days < 7 for d in nonzero_days),
                                   sum((local["d"].max() - pd.to_datetime(d).date()).days < 30 for d in nonzero_days) )
        # 평균 간격(일) → 시간
        gaps = np.diff(sorted(pd.to_datetime(nonzero_days))).astype("timedelta64[h]").astype(int)
        rec_mean_gap_hours = float(gaps.mean()) if gaps.size else 24.0
    else:
        rec_days_since_last = 30.0
        rec_7_over_30 = 0.0
        rec_mean_gap_hours = 24.0

    # 추세: 최근 7일 vs 그 전 7일 (일 합 기준)
    local_by_day = local.groupby("d")["day_sum"].sum().sort_index()
    last_day_all = local_by_day.index.max() if not local_by_day.empty else None
    if last_day_all is not None:
        last_day_all = pd.to_datetime(last_day_all).date()
        w2_end = last_day_all
        w2_start = w2_end - pd.Timedelta(days=6)
        w1_end = w2_start - pd.Timedelta(days=1)
        w1_start = w1_end - pd.Timedelta(days=6)
        s2 = float(local_by_day.loc[(local_by_day.index >= w2_start) & (local_by_day.index <= w2_end)].sum())
        s1 = float(local_by_day.loc[(local_by_day.index >= w1_start) & (local_by_day.index <= w1_end)].sum())
        trend_7_vs_prev7 = _safe_div((s2 - s1), max(s1, 1.0))
    else:
        trend_7_vs_prev7 = 0.0

    # 시간대/요일 점유율: user_metrics에 직접 분해가 없으면 0으로 둠
    time_weekend_ratio = 0.0
    time_bins = {k: 0.0 for k in DOW_KEYS + HOUR_KEYS}

    # 최종 딕셔너리
    feat = {
        # 다양성/최근성
        "div_entropy_spend": div_entropy_spend,
        "div_hhi_spend": div_hhi_spend,
        "rec_7_over_30": rec_7_over_30,
        "rec_days_since_last": rec_days_since_last,
        "rec_mean_gap_hours": rec_mean_gap_hours,
        # 통계
        "stat_avg": stat_avg, "stat_std": stat_std, "stat_cv": stat_cv,
        "stat_max": stat_max, "stat_med": stat_med, "stat_q75": stat_q75,
        "stat_high_ratio": stat_high_ratio,
        # 누적
        "total_cnt_30d": total_cnt,
        "total_spend_30d": total_sum,
        "sub_top1_spend": sub_top1,
        "sub_top3_spend": sub_top3,
        # 시간 관련
        "time_weekend_ratio": time_weekend_ratio,
        **time_bins,
    }

    # 카테고리별 점유율 key 채우기(없으면 0)
    for name in EXPECTED_SUB_NAMES:
        feat[f"sub_freq_share::{name}"]  = float(freq_share.get(name, 0.0))
        feat[f"sub_spend_share::{name}"] = float(spend_share.get(name, 0.0))

    # DataFrame 한 행으로, 모델 스키마에 맞춰 reindex
    X = pd.DataFrame([feat]).reindex(columns=feature_cols, fill_value=0.0).astype(float)
    return X
