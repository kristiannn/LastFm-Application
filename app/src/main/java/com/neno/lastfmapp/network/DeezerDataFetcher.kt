package com.neno.lastfmapp.network

import com.neno.lastfmapp.Result
import com.neno.lastfmapp.network.dto.deezer.DeezerSearchDto
import com.neno.lastfmapp.network.utils.RestError
import com.neno.lastfmapp.toQuery
import java.io.IOException

@Suppress("BlockingMethodInNonBlockingContext")
class DeezerDataFetcher(
    private val service: DeezerService
) : DeezerServiceOperations
{
    override suspend fun search(query: String): Result<DeezerSearchDto>
    {
        return try
        {
            val response = service.search(query.toQuery()).execute()
            if (!response.isSuccessful)
            {
                Result.Error<DeezerSearchDto>(RestError.NullResult)
            }

            response.body()?.let { Result.Success(it) }
                ?: Result.Error(RestError.NullResult)
        } catch (e: IOException)
        {
            Result.Error(RestError.NetworkError)
        }
    }
}