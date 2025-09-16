import os

from dotenv import load_dotenv
from sqlalchemy import create_engine, Engine, text

load_dotenv()

def get_db_engine() -> Engine:
    host = os.getenv("DB_HOST", "127.0.0.1")
    port = int(os.getenv("DB_PORT", "3306"))
    user = os.getenv("DB_USER", "root")
    password = os.getenv("DB_PASSWORD", "")
    name = os.getenv("DB_NAME", "pocketc")
    url = f"mysql+pymysql://{user}:{password}@{host}:{port}/{name}?charset=utf8mb4"
    engine = create_engine(url, pool_pre_ping=True, future=True)

    with engine.connect() as conn:
        conn.exec_driver_sql("SELECT 1")
    return engine

ENGINE = get_db_engine()


# NOTE: 일별 집계를 위한 배치 프로그램
def run_sql(sql: str, params: dict | None = None):
    with ENGINE.begin() as conn:
        return conn.execute(text(sql), params or {})
