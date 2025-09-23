package com.example.irumi.core.pref

import android.content.Context
import android.content.SharedPreferences
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SharedPrefsTokenStore @Inject constructor(
    private val prefs: SharedPreferences
) : TokenStore {

    override val accessToken: String?
        get() = prefs.getString(KEY_ACCESS, null)

    override val refreshToken: String?
        get() = prefs.getString(KEY_REFRESH, null)

    override var email: String?
        get() = prefs.getString(KEY_EMAIL, null)
        set(value) { prefs.edit().putString(KEY_EMAIL, value).apply() }

    override var autoLogin: Boolean
        get() = prefs.getBoolean(KEY_AUTO_LOGIN, false)
        set(value) { prefs.edit().putBoolean(KEY_AUTO_LOGIN, value).apply() }

    override fun save(access: String?, refresh: String?) {
        prefs.edit().apply {
            if (access == null) remove(KEY_ACCESS) else putString(KEY_ACCESS, access)
            if (refresh == null) remove(KEY_REFRESH) else putString(KEY_REFRESH, refresh)
        }.apply()
    }

    override fun clear() {
        prefs.edit()
            .remove(KEY_ACCESS)
            .remove(KEY_REFRESH)
            .remove(KEY_EMAIL)
            .remove(KEY_AUTO_LOGIN)
            .apply()
    }

    private companion object {
        const val PREFS_NAME = "auth_prefs"
        const val KEY_ACCESS = "access_token"
        const val KEY_REFRESH = "refresh_token"
        const val KEY_EMAIL = "login_email"
        const val KEY_AUTO_LOGIN = "auto_login"
    }
}
