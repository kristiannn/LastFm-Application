package com.neno.lastfmapp.network

import com.neno.lastfmapp.Result

interface LastFmAuthOperations
{
    suspend fun getSessionKey(params: Map<String, String>): Result<String>

    suspend fun updateNowPlaying(params: Map<String, String>)

    suspend fun scrobbleTrack(params: Map<String, String>): Result<Unit>
}