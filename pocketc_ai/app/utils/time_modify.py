from datetime import datetime, timedelta, timezone


def kst_to_utc(dt_kst: datetime) -> datetime:
    KST = timezone(timedelta(hours=9))
    if dt_kst.tzinfo is None:
        dt_kst = dt_kst.replace(tzinfo=KST)
    return dt_kst.astimezone(timezone.utc)