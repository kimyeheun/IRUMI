from fastapi import APIRouter

router = APIRouter()

@router.get("/")
def get_mission() :
    return "mission"

@router.post("/daily")
def create_daily_mission(userId: int):
    try:
        print("hi")
        # 최근 일주일의 user 결제 내역 가져오기
        # 클러스터링 진행
        # 클러스터 id get -> 매핑 된 소분류 id 가져오기
        # 소분류와 어떤 미션 템플릿을 매핑할지 판단
        #
    except Exception as e:
        print(e)
    return