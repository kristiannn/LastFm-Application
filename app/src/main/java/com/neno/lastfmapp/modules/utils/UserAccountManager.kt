package com.neno.lastfmapp.modules.utils

import android.app.Application
import android.content.Context
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

    override fun saveSessionKeyAndPassword(password: String, sessionKey: String)
    {
        sharedPreferences.edit().apply {
            putString(PROFILE_PASSWORD, password)
            putString(PROFILE_SESSION_KEY, sessionKey).apply()
        }
    }

    override fun getUser(): String = sharedPreferences.getString(PROFILE_NAME, "")!!.toString()

    override fun getProfilePicture(): String = sharedPreferences.getString(PROFILE_PICTURE, "")!!.toString()

    override fun setPeriodPreference(period: String)
    {
        sharedPreferences.edit().putString(PROFILE_PERIOD, period).apply()
    }

    override fun getPeriodPreference(): String
    {
        return sharedPreferences.getString(PROFILE_PERIOD, LastFmPeriodParams.Overall)!!
    }

    override fun isUserLogged(): Boolean = sharedPreferences.contains(PROFILE_NAME) &&
            sharedPreferences.contains(PROFILE_PASSWORD) && sharedPreferences.contains(PROFILE_SESSION_KEY)

    override fun logoutUser() = sharedPreferences.edit().clear().apply()

    companion object
    {
        private const val PROFILE_NAME = "username"
        private const val PROFILE_PASSWORD = "password"
        private const val PROFILE_SESSION_KEY = "session"
        private const val PROFILE_PICTURE = "picture"
        private const val PROFILE_PERIOD = "period"
    }
}
