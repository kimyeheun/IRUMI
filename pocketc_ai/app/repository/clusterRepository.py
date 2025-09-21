from sqlalchemy.orm.session import Session

from app.models.cluster import Cluster


class ClusterRepository:
    def __init__(self, db: Session):
        self.db = db

    def get_all_cluster(self) -> list[type[Cluster]]:
        return self.db.query(Cluster).all()

    def get_sub_by_id(self, cluster_id: int) -> list[int]:
        rows = (self.db.query(Cluster.sub_id)
                .filter(Cluster.cluster_id == cluster_id)
                .all())
        return [sub_id for (sub_id,) in rows]
