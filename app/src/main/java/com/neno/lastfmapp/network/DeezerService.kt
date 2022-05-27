package com.neno.lastfmapp.network

import com.neno.lastfmapp.network.dto.deezer.DeezerSearchDto
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface DeezerService
{
    @GET("search")
    fun search(
        @Query("q") query: String
    ): Call<DeezerSearchDto>
}