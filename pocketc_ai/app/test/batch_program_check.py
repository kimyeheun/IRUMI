import os
import time
import requests
from urllib.parse import quote_plus
from dotenv import load_dotenv
from sqlalchemy import create_engine, text

load_dotenv()

API_BASE = os.getenv("API_BASE", "http://localhost:8000")

def engine():
    host = os.getenv("DB_HOST", "127.0.0.1")
    port = int(os.getenv("DB_PORT", "3306"))
    user = os.getenv("DB_USER", "root")
    password = quote_plus(os.getenv("DB_PASSWORD", "0000"))
    name = os.getenv("DB_NAME", "pocketc")
    url = f"mysql+pymysql://{user}:{password}@{host}:{port}/{name}?charset=utf8mb4"
    return create_engine(url, pool_pre_ping=True, future=True)

def trigger_rebuild(lookback_days=3):
    r = requests.post(f"{API_BASE}/admin/rebuild-daily-batch_program", params={"lookback_days": lookback_days}, timeout=10)
    r.raise_for_status()
    return r.json()["task_id"]

def fetch_metrics(user_id=1, sub_id=2001, days=3):
    eng = engine()
    with eng.connect() as conn:
        rows = conn.execute(text("""
        SELECT d, day_count, day_sum, night_count, morning_count, afternoon_count, max_per_txn
        FROM user_daily_metrics
        WHERE user_id=:u AND sub_id=:s
        ORDER BY d DESC
        LIMIT :lim
        """), {"u": user_id, "s": sub_id, "lim": days}).mappings().all()
        return rows

if __name__ == "__main__":
    task_id = trigger_rebuild(lookback_days=3)
    print(f"[trigger] task_id={task_id}")


    for i in range(10):
        time.sleep(1.0)
        rows_coffee = fetch_metrics(1, 2001, 5)
        rows_delivery = fetch_metrics(1, 2002, 5)
        if rows_coffee and rows_delivery:
            print("[batch_program/coffee]")
            for r in rows_coffee:
                print(dict(r))
            print("[batch_program/delivery]")
            for r in rows_delivery:
                print(dict(r))
            break
    else:
        print("No batch_program yet. Check worker/beat logs and Celery/Flower.")
