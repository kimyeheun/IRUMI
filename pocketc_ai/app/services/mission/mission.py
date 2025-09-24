from __future__ import annotations

import random
from datetime import datetime
from pathlib import Path

from sqlalchemy.orm.session import Session

from app.repository.categoryRepository import SubCategoryRepository
from app.repository.clusterRepository import ClusterRepository
from app.repository.missionRepository import MissionRepository
from app.repository.transactionRepository import TransactionRepository
from app.repository.userMetricsRepository import UserMetricsRepository
from app.schemas.mission import Mission
from app.services.mission.clustering import cluster_for_user
from app.services.mission.template.template import pick_template
from app.services.mission.templates_to_mission import build_mission_details


class MissionService:
    def __init__(self, db: Session):
        self.db = db
        # TODO: 환경 변수로 바꾸기
        self.cluster_path = Path("app/services/mission/cluster")
        self.repo = UserMetricsRepository(db)
        self.sub = SubCategoryRepository(db)
        self.trans = TransactionRepository(db)
        self.cluster = ClusterRepository(db)
        self.template = MissionRepository(db)

    def create_daily_mission(self, user_id: int, now: datetime) -> list[Mission]:
        # 1. 클러스터 예측
        cluster_id = cluster_for_user(self.cluster_path, self.trans, user_id, now)
        # 2.1. 클러스터 → 소분류 top-3
        sub_ids = self.cluster.get_sub_by_id(cluster_id)
        # 2.2. id → 이름 변환
        subs = self.sub.get_names_by_ids(sub_ids)

        missions = []
        mission_counts = [2, 2, 1]

        for i, (sub_id, sub_name) in enumerate(zip(sub_ids, subs)):
            if len(missions) >= 5: break

            stats = self.repo.get_daily_stats_for_category(user_id, sub_id, now)
            generated_templates = []
            for _ in range(mission_counts[i]):
                try:
                    # 3. 템플릿 선택
                    tmpl_name = pick_template(sub_name, user_stats=stats, epsilon=0.1, exclude=generated_templates)
                    generated_templates.append(tmpl_name)
                    # 4. 미션 생성
                    mission_text, dsl, (valid_from, valid_to) = build_mission_details(
                        tmpl_name, sub_id, sub_name, stats, now, self.template
                    )
                    missions.append(
                        Mission(
                            mission=mission_text,
                            subId=sub_id,
                            dsl=str(dsl),
                            type=0,
                            validFrom=valid_from,
                            validTo=valid_to
                        )
                    )
                except ValueError:
                    break
        return missions


    def create_weekly_mission(self, user_id: int, now: datetime) -> list[Mission]:
        top_cats = self.repo.get_top_frequent_categories(user_id, now, days=30, top_n=1)
        if not top_cats:
            return []
        print(top_cats)
        missions = []
        for top_cat in top_cats:
            print(top_cat)
            sub_id = top_cat["sub_id"]
            sub_name = self.sub.get_name_by_id(sub_id)
            # 4주간의 데이터를 바탕으로 주간 평균 소비 계산
            weekly_sum = top_cat["total_tx_sum"] / 4.0
            weekly_count = top_cat["total_tx_count"] / 4.0
            stats = {"weekly_sum": weekly_sum, "weekly_count": weekly_count}

            # 위클리 템플릿 중 하나를 무작위로 선택
            weekly_templates = ["SPEND_CAP_WEEKLY", "COUNT_CAP_WEEKLY", "DAY_BAN_WEEKLY"]
            # TODO: DAY_BAN_WEEKLY 요일 분석 추가
            tmpl_name = random.choice(weekly_templates)
            # 미션 생성
            mission_text, dsl, (valid_from, valid_to) = build_mission_details(
                tmpl_name, sub_id, sub_name, stats, now, self.template
            )
            missions.append(
                Mission(
                    mission=mission_text,
                    subId=sub_id,
                    dsl=str(dsl),
                    type=1,
                    validFrom=valid_from,
                    validTo=valid_to
                )
            )
        return missions


    def create_monthly_mission(self, user_id: int, now: datetime) -> list[Mission]:
        top_cats = self.repo.get_top_frequent_categories(user_id, now, days=90, top_n=1)
        if not top_cats:
            return []

        missions = []
        for top_cat in top_cats:
            sub_id = top_cat["sub_id"]
            sub_name = self.sub.get_name_by_id(sub_id)
            # 월간 평균 소비 계산
            monthly_sum = top_cat["total_tx_sum"] / 3.0
            monthly_count = top_cat["total_tx_count"] / 3.0
            stats = {"monthly_sum": monthly_sum, "monthly_count": monthly_count,}

            # 먼슬리 템플릿 선택
            tmpl_name = random.choice(["SPEND_CAP_MONTHLY", "COUNT_CAP_MONTHLY"])
            # 미션 생성
            mission_text, dsl, (valid_from, valid_to) = build_mission_details(
                tmpl_name, sub_id, sub_name, stats, now, self.template
            )
            missions.append(
                Mission(
                    mission=mission_text,
                    subId=sub_id,
                    dsl=str(dsl),
                    type=2,
                    validFrom=valid_from,
                    validTo=valid_to
                )
            )
        return missions
