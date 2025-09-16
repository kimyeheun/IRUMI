# 1) 컨테이너 기동
docker docker-compose up --build -d

# 2) 시드 데이터 적재
python tests/seed_data.py

# 3) 수동 트리거 + 검증
python tests/trigger_and_check.py

# 4) Flower UI 확인
# http://localhost:5555  (admin / admin123)