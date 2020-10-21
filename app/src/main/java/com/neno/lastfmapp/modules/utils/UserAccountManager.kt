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

    override fun getUser(): String = sharedPreferences.getString(PROFILE_NAME, "")!!.toString()

    override fun getProfilePicture(): String = sharedPreferences.getString(PROFILE_PICTURE, "")!!.toString()

    override fun setPeriodPreference(period: String)
    {
        sharedPreferences.edit().putString(PROFILE_PERIOD, period).apply()
    }

    override fun getPeriodPreference(): String
    {
        return sharedPreferences.getString(PROFILE_PERIOD, LastFmPeriodParams.Overall.tag)!!
    }

    override fun isUserLogged(): Boolean = sharedPreferences.contains(PROFILE_NAME)

    override fun logoutUser() = sharedPreferences.edit().clear().apply()

    companion object
    {
        private const val PROFILE_NAME = "username"
        private const val PROFILE_PICTURE = "picture"
        private const val PROFILE_PERIOD = "period"
    }
}
