from __future__ import annotations

from datetime import datetime, timedelta
from pathlib import Path

from sqlalchemy.orm.session import Session

from app.repository.categoryRepository import SubCategoryRepository
from app.repository.clusterRepository import ClusterRepository
from app.repository.missionRepository import MissionRepository
from app.repository.transactionRepository import TransactionRepository
from app.repository.userMetricsRepository import UserMetricsRepository
from app.schemas.mission import Missions, Mission
from app.services.mission.clustering import cluster_for_user
from app.services.mission.dsl.dsl_templates import build_dsl_for_template
from app.services.mission.templates_to_mission import pick_template_for_category, build_mission_sentence


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
        # 1) 클러스터 예측
        # cluster_id = cluster_for_user_from_metrics(self.cluster_path, self.repo, user_id, now)
        cluster_id = cluster_for_user(self.cluster_path, self.trans, user_id, now)
        # 2-1) 클러스터 → 소분류 top-3
        sub_ids = self.cluster.get_sub_by_id(cluster_id)
        # 2-2) id → 이름 변환
        subs = self.sub.get_names_by_ids(sub_ids)

        missions = []
        mission_counts = [2, 2, 1]
        for i, (sub_id, sub_name) in enumerate(zip(sub_ids, subs)):
            if len(missions) >= 5: break

            stats = self.repo.get_daily_stats_for_category(user_id, sub_id, now)
            generated_templates = []
            for _ in range(mission_counts[i]):
                try:
                    # 4) 템플릿 선택 (이미 생성된 템플릿 제외)
                    tmpl_name = pick_template_for_category(sub_name, user_stats=stats, epsilon=0.1, exclude=generated_templates)
                    generated_templates.append(tmpl_name)
                    # 5) DSL 생성
                    dsl = build_dsl_for_template(tmpl_name, user_id=user_id, sub_id=sub_id, now=now, stats=stats)
                    # 6) 미션 문장 생성하기
                    mission_text, params = build_mission_sentence(tmpl_name, sub_id, sub_name, stats, self.template)
                    # 7) 저장(또는 반환)
                    missions.append(
                        Mission(
                            missionId=1,  # 임시 ID
                            mission=mission_text,
                            subId=sub_id,
                            missionDsl=str(dsl),
                            missionType=0,
                            validFrom=datetime.now(),
                            validTo=datetime.now() + timedelta(days=7)
                        )
                    )
                except ValueError:
                    break
        return missions


    def create_weekly_mission(self, user_id: int) -> Missions:
        return None


    def create_monthly_mission(self, user_id: int) -> Missions:
        return None