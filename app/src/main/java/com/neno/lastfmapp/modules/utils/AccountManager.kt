package com.neno.lastfmapp.modules.utils

interface AccountManager
{
    fun saveUser(username: String, pictureUrl: String)

    fun getUser(): String

    fun getProfilePicture(): String

    fun setPeriodPreference(period: String)

    fun getPeriodPreference(): String

    fun saveSessionKey(password: String, sessionKey: String)

    fun getSessionKey(): String

    fun setCurrentTheme(theme: Int)

    fun getCurrentTheme(): Int

    fun isUserLogged(): Boolean

    fun logoutUser()
}