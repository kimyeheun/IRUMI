from fastapi import APIRouter

router = APIRouter()

@router.get("/categories")
def get_transactions() :
    return "categories"
