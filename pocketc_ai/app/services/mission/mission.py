from __future__ import annotations

from datetime import datetime
from pathlib import Path

from sqlalchemy.orm.session import Session

from app.repository.categoryRepository import SubCategoryRepository
from app.repository.clusterRepository import ClusterRepository
from app.repository.missionRepository import MissionRepository
from app.repository.transactionRepository import TransactionRepository
from app.repository.userMetricsRepository import UserMetricsRepository
from app.schemas.mission import Missions, Mission
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
                        tmpl_name, user_id, sub_id, sub_name, stats, now, self.template
                    )
                    missions.append(
                        Mission(
                            missionId=1,  # 임시 ID
                            mission=mission_text,
                            subId=sub_id,
                            missionDsl=str(dsl),
                            missionType=0,
                            validFrom=valid_from,
                            validTo=valid_to
                        )
                    )
                except ValueError:
                    break
        return missions


    def create_weekly_mission(self, user_id: int) -> Missions:
        return None


    def create_monthly_mission(self, user_id: int) -> Missions:
        return None