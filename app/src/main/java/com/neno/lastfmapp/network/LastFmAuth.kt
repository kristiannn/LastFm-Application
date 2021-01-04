package com.neno.lastfmapp.network

import android.util.Log
import com.neno.lastfmapp.Result
import com.neno.lastfmapp.modules.utils.AccountManager
import com.neno.lastfmapp.network.utils.HttpResultConverter
import com.neno.lastfmapp.network.utils.Keys.API_KEY
import com.neno.lastfmapp.network.utils.Keys.SHARED_SECRET
import com.neno.lastfmapp.network.utils.RestError
import java.io.IOException
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException

@Suppress("BlockingMethodInNonBlockingContext")
class LastFmAuth(
    private val authService: LastFmAuthService,
    private val lastFmResultConverter: HttpResultConverter,
    private val accountManager: AccountManager //This will not be here once we put the secret keys at the proper place
) : LastFmAuthOperations
{
    override suspend fun getSessionKey(params: Map<String, String>): Result<String>
    {
        try
        {
            val queryMap = getSignature(params)

            val response = authService.getSessionKey(queryMap = queryMap).execute()

            if (!response.isSuccessful)
            {
                return Result.Error(lastFmResultConverter.httpError(response.errorBody()))
            }

            return response.body()?.let { Result.Success(it.sessionDetailsDto.sessionKey) }
                ?: Result.Error(RestError.NullResult)
        } catch (e: IOException)
        {
            return Result.Error(RestError.NetworkError)
        }
    }

    override suspend fun updateNowPlaying(params: Map<String, String>)
    {
        try
        {
            val queryMap = getSignature(params)

            val response = authService.updateTrackPlaying(queryMap = queryMap).execute()
        } catch (e: IOException)
        {
            println(e)
        }
    }

    override suspend fun scrobbleTrack(params: Map<String, String>): Result<Unit>
    {
        try
        {
            val queryMap = getSignature(params)

            val response = authService.scrobbleTrack(queryMap = queryMap).execute()

            if (!response.isSuccessful)
            {
                return Result.Error(lastFmResultConverter.httpError(response.errorBody()))
            }

            return Result.Success(Unit)
        } catch (e: IOException)
        {
            println(e)
            return Result.Error(RestError.NetworkError)
        }
    }

    private fun getSignature(params: Map<String, String>): Map<String, String>
    {
        val sortedMap = sortedMapOf<String, String>()
        val stringBuilder = StringBuilder()

        //We'll always need the API key, no point of putting it in the argument every single time..
        sortedMap[API_KEY_PARAM] = API_KEY
        if (accountManager.isUserLogged()) sortedMap[SK_PARAM] = accountManager.getSessionKey()
        sortedMap.putAll(params)

        sortedMap.forEach {
            stringBuilder.append(it.key)
            stringBuilder.append(it.value)
        }
        stringBuilder.append(SHARED_SECRET)

        val signatureString = stringBuilder.toString()
        val apiSignature = md5(signatureString)
        sortedMap[API_SIG_PARAM] = apiSignature


        return sortedMap
    }

    private fun md5(s: String): String?
    {
        val hexString = StringBuilder()
        try
        {
            // Create MD5 Hash
            val digest = MessageDigest.getInstance("MD5")
            digest.update(s.toByteArray(Charsets.UTF_8))
            val messageDigest = digest.digest()

            // Create Hex String
            for (aMessageDigest in messageDigest)
            {
                var h = Integer.toHexString(0xFF and aMessageDigest.toInt())
                while (h.length < 2) h = "0$h"
                hexString.append(h)
            }

            return hexString.toString()
        } catch (e: NoSuchAlgorithmException)
        {
            e.printStackTrace()
        }
        return null
    }

    companion object
    {
        private const val API_KEY_PARAM = "api_key"
        private const val API_SIG_PARAM = "api_sig"
        private const val SK_PARAM = "sk"
    }
}