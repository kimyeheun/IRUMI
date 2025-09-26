package com.example.irumi.core.state

/**
 * sealed 키워드: 이 인터페이스를 상속받는 클래스나 객체를 같은 파일 내로 제한
 * 이를 통해 컴파일러는 이 인터페이스의 모든 가능한 하위 타입(sub-types)을 알 수 있음
 *
 * sealed interface를 왜 사용할까?
 * when 식의 완전성(exhaustiveness)
 * sealed 키워드가 붙은 타입의 모든 하위 타입을 when 식에서 처리하면, 별도의 else 구문이 필요하지 않음
 */
sealed interface UiState<out T> {
    /**
     * 데이터가 비었을 때 (검색 결과가 없을 때)
     */
    data object Empty : UiState<Nothing>

    data object Loading : UiState<Nothing>

    data class Success<T>(
        val data: T
    ) : UiState<T>

    data class Failure(
        val msg: String
    ) : UiState<Nothing>
}