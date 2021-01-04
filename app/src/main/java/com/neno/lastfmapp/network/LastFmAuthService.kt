package com.neno.lastfmapp.network

import com.neno.lastfmapp.network.dto.auth.SessionDto
import retrofit2.Call
import retrofit2.http.POST
import retrofit2.http.QueryMap

interface LastFmAuthService
{
    @POST("?format=json")
    fun getSessionKey(@QueryMap queryMap: Map<String, String>): Call<SessionDto>

    @POST("?format=json")
    fun updateTrackPlaying(@QueryMap queryMap: Map<String, String>): Call<Any>

    @POST("?format=json")
    fun scrobbleTrack(@QueryMap queryMap: Map<String, String>): Call<Any>
}