# PocketC AI Backend

![Python](https://img.shields.io/badge/Python-3.10-blue.svg)
![FastAPI](https://img.shields.io/badge/FastAPI-0.95+-teal.svg)
![MySQL](https://img.shields.io/badge/Database-MySQL-orange.svg)
![Redis](https://img.shields.io/badge/Cache-Redis-red.svg)
![Celery](https://img.shields.io/badge/TaskQueue-Celery-green.svg)
![Docker](https://img.shields.io/badge/Deploy-Docker-blue.svg)

---

## 프로젝트 설명

PocketC AI는 사용자의 금융 거래 데이터를 분석하여 **개인화된 서비스**를 제공하는 프로젝트입니다.

> 사용자의 **소비 내역을 자동 분류**하고, AI 기반의 클러스터링을 통해 유사한 소비 패턴을 가진 사용자 그룹을 식별합니다.
> 이를 바탕으로 각 사용자에게 최적화된 일간, 주간, 월간 절약 미션을 제공하여 **건강한 금융 습관 형성**을 돕는 것을 목표로 합니다.

---

## 주요 기능

### 거래 내역 자동 분류

* 정규식 및 휴리스틱 규칙 기반으로 사용자의 지출 내역을 다양한 카테고리(`외식`, `교통`, `쇼핑` 등)로 자동 분류합니다.

### AI 기반 사용자 클러스터링

* K-Means 알고리즘을 사용하여 전체 사용자의 소비 데이터를 분석합니다.
* 유사한 소비 패턴을 가진 그룹(`배달 음식형`, `온라인 쇼핑형` 등)으로 군집화합니다.

### 맞춤형 절약 미션 추천

* 클러스터링된 그룹 특성과 개인의 최근 소비 통계를 종합하여 사용자에게 실천 가능한 절약 미션을 생성하고 제안합니다.

### 주기적인 데이터 처리 및 모델 학습

* Celery를 활용하여 매일 새벽 소비 데이터를 통계적으로 집계합니다.
* 주기적으로 AI 클러스터링 모델을 최신 데이터로 재학습하여 추천 정확도를 유지합니다.

---

## 기술 스택

* **Language**: Python 3.10
* **Web Framework**: FastAPI
* **Database**: MySQL, SQLAlchemy (ORM)
* **Asynchronous Tasks**: Celery, Redis
* **ML/Data**: Scikit-learn, Pandas, Numpy

---

## 설치 및 사용 방법

### 사전 요구사항

* [Docker](https://www.docker.com/get-started/)
* [Docker Compose](https://docs.docker.com/compose/install/)

### 설치 단계

1. **프로젝트 클론**

   ```bash
   git clone https://lab.ssafy.com/s13-fintech-finance-sub1/S13P21A407.git
   cd S13P21A407/pocketc_ai
   ```

2. **환경 변수 설정**
   프로젝트 루트 디렉터리에 `.env` 파일을 생성하고 데이터베이스 및 Redis 연결 정보를 입력합니다.

   ```env
   # Database
   DB_HOST=localhost
   DB_PORT=3306
   DB_USER=root
   DB_PASSWORD=your_database_password
   DB_NAME=pocketc

   # Redis (for Celery)
   CELERY_BROKER_URL=redis://redis:6379/0
   CELERY_RESULT_BACKEND=redis://redis:6379/1
   ```

---

### 실행 방법

1. **Docker 컨테이너 빌드 및 실행**

   ```bash
   docker-compose up --build -d
   ```

2. **서버 상태 확인**

   * API 서버: [http://localhost:8000/docs](http://localhost:8000/docs)
   * Celery 모니터링: [http://localhost:5555](http://localhost:5555)

---

## API 사용 예시

### 거래 내역 분류

```http
POST /ai/categories
```

### 사용자 맞춤 미션 생성

```http
GET /ai/missions/{userId}/daily
GET /ai/missions/{userId}/weekly
GET /ai/missions/{userId}/monthly
```

---

## 백그라운드 작업 수동 실행 (선택)

### 일별 소비 통계 재집계

```bash
curl -X POST "http://localhost:8000/admin/rebuild-daily-metrics?lookback_days=3"
```

### AI 클러스터 모델 재학습

```bash
curl -X POST "http://localhost:8000/admin/rebuild-clusters"
```
