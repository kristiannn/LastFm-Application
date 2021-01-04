package com.neno.lastfmapp.network

import com.neno.lastfmapp.Result
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
    private val lastFmResultConverter: HttpResultConverter
) : LastFmAuthOperations
{
    override suspend fun getSessionKey(method: String, username: String, password: String): Result<String>
    {
        try
        {
            //Order needs to be alphabetical
            val signatureString =
                API_KEY_PARAM + API_KEY + METHOD_PARAM + method + USER_PASSWORD + password + USER_NAME + username + SHARED_SECRET

            val apiSignature = md5(signatureString) ?: return Result.Error(RestError.MD5Null)

            val response = authService.getSessionKey(
                password = password,
                username = username,
                apiKey = API_KEY,
                apiSignature = apiSignature
            ).execute()

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
        private const val METHOD_PARAM = "method"
        private const val USER_PASSWORD = "password"
        private const val USER_NAME = "username"
    }
}