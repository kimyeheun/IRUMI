from datetime import datetime, timedelta
from pathlib import Path

from sqlalchemy.orm.session import Session

from pocketc_ai.app.repository.categoryRepository import SubCategoryRepository
from pocketc_ai.app.repository.clusterRepository import ClusterRepository
from pocketc_ai.app.repository.missionRepository import MissionRepository
from pocketc_ai.app.repository.transactionRepository import TransactionRepository
from pocketc_ai.app.repository.userMetricsRepository import UserMetricsRepository
from pocketc_ai.app.schemas.mission import Missions, Mission
from pocketc_ai.app.services.mission.clustering import cluster_for_user_from_metrics
from pocketc_ai.app.services.mission.dsl.dsl_runtime import compile_plan
from pocketc_ai.app.services.mission.dsl.dsl_templates import build_dsl_for_template
from pocketc_ai.app.services.mission.templates_to_mission import pick_template_for_category


class MissionService:
    def __init__(self, db: Session):
        self.db = db
        # TODO: 환경 변수로 바꾸기
        self.cluster_path = Path("pocketc_ai/app/services/mission/cluster")
        self.repo = UserMetricsRepository(db)
        self.sub = SubCategoryRepository(db)
        self.trans = TransactionRepository(db)
        self.cluster = ClusterRepository(db)
        self.template = MissionRepository(db)

    def create_daily_mission(self, user_id: int, now: datetime) -> list[Mission]:
        print("create daily mission")
        # 1) 클러스터 예측 (user_metrics 기반)
        cluster_id = cluster_for_user_from_metrics(self.cluster_path, self.repo, user_id, now)
        print("create daily mission")

        # 2) 클러스터 → 소분류 top-3
        sub_ids = self.cluster.get_sub_by_id(cluster_id)  # repo에 구현 가정
        print("create daily mission")

        # id → 이름 변환(또는 repo가 이름도 리턴)
        subs = self.sub.get_names_by_ids(sub_ids)  # ["커피","간식","택시/대리"] 등
        print("create daily mission")

        missions = []
        for sub_id, sub_name in zip(sub_ids, subs):
            print("create daily mission--")

            # 3) user_stats 준비 (템플릿 가중치 보정용)
            stats = self.repo.get_daily_stats_for_category(user_id, sub_id, now)  # mean_daily_count, night_ratio 등 dict
            print("create daily mission--")

            # 4) 템플릿 선택
            try:
                tmpl_name = pick_template_for_category(sub_name, user_stats=stats, epsilon=0.1)
            except ValueError:
                # EXCLUDED 카테고리
                continue
            print(f"create daily mission--{tmpl_name}")

            # 5) DSL 생성
            dsl = build_dsl_for_template(tmpl_name, user_id=user_id, sub_id=sub_id, now=now, stats=stats)
            print("create daily mission--")

            # 6) 실행계획(compiled_plan) 생성
            plan = compile_plan(dsl)
            print("create daily mission--")

            # 7) 저장(또는 반환)
            missions.append(
                Mission(
                    missionId=1,
                    mission="", # TODO: mission 만들기
                    subId=sub_id,
                    missionDsl=dsl,
                    missionType=0,
                    validFrom=datetime.now(),
                    validTo=datetime.now() + timedelta(days=7)
                )
            )
        return missions


    def create_weekly_mission(self, user_id: int) -> Missions:
        return None


    def create_monthly_mission(self, user_id: int) -> Missions:
        return None