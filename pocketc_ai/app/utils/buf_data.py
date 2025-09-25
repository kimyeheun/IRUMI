BUF_DAILY_MISSION = [
    {
        "mission": "<간식> 1건 8000원 이하",
        "subId": 4,
        "dsl": "{'template': 'PER_TXN_DAILY', 'sub_id': 4, 'type': 'per_txn_max', 'comparator': '<=', 'value': 8000}",
        "type": 0,
    },
    {
        "mission": "오늘 <간식> 1회 이하",
        "subId": 4,
        "dsl": "{'template': 'COUNT_CAP_DAILY', 'sub_id': 4, 'type': 'count', 'comparator': '<=', 'value': 1}",
        "type": 0,
    },
    {
        "mission": "<외식> 1건 8000원 이하",
        "subId": 6,
        "dsl": "{'template': 'PER_TXN_DAILY', 'sub_id': 6, 'type': 'per_txn_max', 'comparator': '<=', 'value': 8000}",
        "type": 0,
    },
    {
        "mission": "오늘 <외식> 20000원 이하",
        "subId": 6,
        "dsl": "{'template': 'SPEND_CAP_DAILY', 'sub_id': 6, 'type': 'total_spend', 'comparator': '<=', 'value': 20000}",
        "type": 0,
    },
    {
        "mission": "오늘 <기타 지출> 1회 이하",
        "subId": 39,
        "dsl": "{'template': 'COUNT_CAP_DAILY', 'sub_id': 39, 'type': 'count', 'comparator': '<=', 'value': 1}",
        "type": 0,
    }
]

BUF_WEEKLY_MISSION = [
    {
        "mission": "이번 주 <커피> 결제 1회 이하로 줄이기",
        "subId": 1,
        "dsl": "{'template': 'COUNT_CAP_WEEKLY', 'sub_id': 1, 'type': 'count', 'comparator': '<=', 'value': 1}",
        "type": 1,
    }
]

BUF_MONTHLY_MISSION = [
    {
        "mission": "이번 달 <커피> 50,000원 이하로 사용하기",
        "subId": 1,
        "dsl": "{'template': 'SPEND_CAP_MONTHLY', 'sub_id': 1, 'type': 'total_spend', 'comparator': '<=', 'value': '50000'}",
        "type": 2,
    }
]