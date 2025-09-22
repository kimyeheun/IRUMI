import os

from dotenv import load_dotenv
from sqlalchemy import create_engine, Engine, text
from sqlalchemy.orm.session import sessionmaker

load_dotenv()

def get_db_engine() -> Engine:
    host = os.getenv("DB_HOST", "127.0.0.1")
    port = int(os.getenv("DB_PORT", "3306"))
    user = os.getenv("MYSQL_USER", "root")
    password = os.getenv("MYSQL_PASSWORD", "")
    name = os.getenv("MYSQL_DB", "pocketcdb")
    url = f"mysql+pymysql://{user}:{password}@{host}:{port}/{name}?charset=utf8mb4"
    engine = create_engine(url, pool_pre_ping=True)

    with engine.connect() as conn:
        conn.execute(text("SELECT 1"))
    return engine

ENGINE = get_db_engine()
SessionLocal = sessionmaker(bind=ENGINE, autoflush=False, expire_on_commit=False)

def get_db():
    db = SessionLocal()
    try:
        yield db
    finally:
        db.close()

# NOTE: 일별 집계를 위한 배치 프로그램
def run_sql(sql: str, params: list[dict] | None = None):
    with ENGINE.begin() as conn:
        return conn.execute(text(sql), params or {})
