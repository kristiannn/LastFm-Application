package com.neno.lastfmapp.network

import com.neno.lastfmapp.network.dto.auth.SessionDto
import retrofit2.Call
import retrofit2.http.POST
import retrofit2.http.Query

interface LastFmAuthService
{
    @POST("?method=auth.getMobileSession&format=json")
    fun getSessionKey(
        @Query("password") password: String,
        @Query("username") username: String,
        @Query("api_key") apiKey: String,
        @Query("api_sig") apiSignature: String
    ): Call<SessionDto>
}