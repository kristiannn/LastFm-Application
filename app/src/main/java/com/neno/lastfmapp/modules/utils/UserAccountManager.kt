package com.neno.lastfmapp.modules.utils

import android.app.Application
import android.content.Context
import com.neno.lastfmapp.R
import com.neno.lastfmapp.network.utils.LastFmPeriodParams

class UserAccountManager(
    application: Application
) : AccountManager
{

    private val sharedPreferences = application.getSharedPreferences("account", Context.MODE_PRIVATE)

    override fun saveUser(username: String, pictureUrl: String)
    {
        sharedPreferences.edit().apply {
            putString(PROFILE_NAME, username)
            putString(PROFILE_PICTURE, pictureUrl)
            apply()
        }
    }

    override fun saveSessionKey(password: String, sessionKey: String)
    {
        sharedPreferences.edit().putString(PROFILE_SESSION_KEY, sessionKey).apply()
    }

    override fun getSessionKey(): String
    {
        return sharedPreferences.getString(PROFILE_SESSION_KEY, "")!!
    }

    override fun setCurrentTheme(theme: Int)
    {
        sharedPreferences.edit().putInt(CURRENT_THEME, theme).apply()
    }

    override fun getCurrentTheme(): Int
    {
        return sharedPreferences.getInt(CURRENT_THEME, R.style.LightTheme)
    }

    override fun getUser(): String = sharedPreferences.getString(PROFILE_NAME, "")!!

    override fun getProfilePicture(): String = sharedPreferences.getString(PROFILE_PICTURE, "")!!

    override fun setPeriodPreference(period: String)
    {
        sharedPreferences.edit().putString(PROFILE_PERIOD, period).apply()
    }

    override fun getPeriodPreference(): String
    {
        return sharedPreferences.getString(PROFILE_PERIOD, LastFmPeriodParams.Overall)!!
    }

    override fun isUserLogged(): Boolean = sharedPreferences.contains(PROFILE_NAME) &&
            sharedPreferences.contains(PROFILE_SESSION_KEY)

    override fun logoutUser() = sharedPreferences.edit().clear().apply()

    companion object
    {
        private const val PROFILE_NAME = "username"
        private const val PROFILE_SESSION_KEY = "session"
        private const val PROFILE_PICTURE = "picture"
        private const val PROFILE_PERIOD = "period"
        private const val CURRENT_THEME = "theme"
    }
}
