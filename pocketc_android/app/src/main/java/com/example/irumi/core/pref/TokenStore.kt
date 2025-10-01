package com.example.irumi.core.pref

interface TokenStore {
    val accessToken: String?
    val refreshToken: String?
    var email: String?
    var autoLogin: Boolean

    /** null 전달 시 해당 값 삭제 */
    fun save(access: String?, refresh: String?)
    fun clear()
}
