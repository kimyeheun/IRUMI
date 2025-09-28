package com.example.irumi.core.mapper

data class MajorCategory(val id: Int, val name: String, val isFixed: Boolean)
data class SubCategory(val id: Int, val name: String, val isFixed: Boolean)

object CategoryMapper {
    // Major 카테고리
    private val majorCategories = listOf(
        MajorCategory(1, "주거/생활", true),
        MajorCategory(2, "통신/인터넷", true),
        MajorCategory(3, "금융/저축", true),
        MajorCategory(4, "식비", false),
        MajorCategory(5, "교통/차량", false),
        MajorCategory(6, "건강/의료", false),
        MajorCategory(7, "교육/자기계발", false),
        MajorCategory(8, "쇼핑", false),
        MajorCategory(9, "문화/여가", false),
        MajorCategory(10, "기타", false)
    )

    val majorIdToName = majorCategories.associate { it.id to it.name }
    val majorNameToId = majorCategories.associate { it.name to it.id }
    val majorIdToFixed = majorCategories.associate { it.id to it.isFixed }

    // Sub 카테고리
    private val subCategories = listOf(
        SubCategory(1, "커피", false),
        SubCategory(2, "음료", false),
        SubCategory(3, "술", false),
        SubCategory(4, "간식", false),
        SubCategory(5, "배달음식", false),
        SubCategory(6, "외식", false),
        SubCategory(7, "식재료", false),
        SubCategory(8, "대중교통", false),
        SubCategory(9, "택시/대리", false),
        SubCategory(10, "유류비", false),
        SubCategory(11, "월세/관리비", true),
        SubCategory(12, "전기세", true),
        SubCategory(13, "수도세", true),
        SubCategory(14, "가스비", true),
        SubCategory(15, "생활용품", false),
        SubCategory(16, "휴대폰요금", true),
        SubCategory(17, "인터넷", true),
        SubCategory(18, "OTT·구독서비스", false),
        SubCategory(19, "병원", true),
        SubCategory(20, "약국", true),
        SubCategory(21, "헬스·PT", false),
        SubCategory(22, "학원·수강료", true),
        SubCategory(23, "도서·교재", false),
        SubCategory(24, "자격증", true),
        SubCategory(25, "의류·패션", false),
        SubCategory(26, "뷰티·미용", false),
        SubCategory(27, "온라인 쇼핑몰", false),
        SubCategory(28, "충동 구매", false),
        SubCategory(29, "영화·공연", false),
        SubCategory(30, "게임·콘텐츠", false),
        SubCategory(31, "여행", false),
        SubCategory(32, "취미·오락", false),
        SubCategory(33, "저축", true),
        SubCategory(34, "투자", true),
        SubCategory(35, "대출·이자", true),
        SubCategory(36, "세금·보험", true),
        SubCategory(37, "송금", true),
        SubCategory(38, "경조사비", true),
        SubCategory(39, "기타", false)
    )

    val subIdToName = subCategories.associate { it.id to it.name }
    val subNameToId = subCategories.associate { it.name to it.id }
    val subIdToFixed = subCategories.associate { it.id to it.isFixed }

    // majorId별 subId 리스트
    private val majorToSubs = mapOf(
        1 to listOf(1, 2, 3, 4, 5),
        2 to listOf(6, 7, 8),
        3 to listOf(9, 10, 11, 12),
        4 to listOf(13, 14, 15, 16, 17, 18, 19),
        5 to listOf(20, 21, 22),
        6 to listOf(23, 24, 25),
        7 to listOf(26, 27, 28),
        8 to listOf(29, 30, 31, 32),
        9 to listOf(33, 34, 35, 36),
        10 to listOf(37, 38, 39)
    )

    // 변환 함수
    fun getMajorName(id: Int): String? = majorIdToName[id]
    fun getMajorId(name: String): Int? = majorNameToId[name]
    fun isMajorFixed(id: Int): Boolean = majorIdToFixed[id] ?: false
    fun getSubName(id: Int): String? = subIdToName[id]
    fun getSubId(name: String): Int? = subNameToId[name]
    fun isSubFixed(id: Int): Boolean = subIdToFixed[id] ?: false

    fun getSubListByMajorId(majorId: Int): List<String> =
        majorToSubs[majorId]?.mapNotNull { subIdToName[it] } ?: emptyList()
}
