# Dockerfile

# 1. 베이스 이미지: 파이썬 3.10 슬림 버전을 사용해 이미지 크기를 줄입니다.
FROM python:3.10-slim

# 2. 작성자 정보 (기존과 동일)
LABEL authors="SSAFY"

# 3. 환경 변수 설정
ENV PYTHONDONTWRITEBYTECODE 1 # .pyc 파일 생성 방지
ENV PYTHONUNBUFFERED 1     # 로그가 버퍼링 없이 바로 출력되도록 설정

# 4. 작업 디렉토리 설정 및 생성
WORKDIR /app

# 5. 필요한 라이브러리 설치
# 먼저 requirements.txt 파일만 복사하여 Docker 캐시를 효율적으로 사용합니다.
COPY requirements.txt .
RUN pip install --no-cache-dir --upgrade pip
RUN pip install --no-cache-dir -r requirements.txt

# 6. 프로젝트 전체 소스 코드를 작업 디렉토리로 복사
COPY . .

# 7. Entrypoint/CMD는 docker-compose.yml에서 지정하므로 여기서는 생략합니다.
#    이렇게 하면 이 Docker 이미지를 api, worker, beat 등 다용도로 사용할 수 있습니다.