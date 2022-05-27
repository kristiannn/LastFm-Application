package com.neno.lastfmapp.network

import com.neno.lastfmapp.Result
import com.neno.lastfmapp.network.dto.deezer.DeezerSearchDto

interface DeezerServiceOperations
{
    suspend fun search(query: String): Result<DeezerSearchDto>
}