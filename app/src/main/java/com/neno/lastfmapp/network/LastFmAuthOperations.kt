package com.neno.lastfmapp.network

import com.neno.lastfmapp.Result

interface LastFmAuthOperations
{
    suspend fun getSessionKey(method: String, username: String, password: String): Result<String>
}