package com.neno.lastfmapp.network.utils

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.neno.lastfmapp.network.dto.ErrorDto
import okhttp3.ResponseBody

class LastFmResultConverter : HttpResultConverter
{
    private val gson = Gson()
    private val errorDtoType = object : TypeToken<ErrorDto>()
    {}.type

    override fun httpError(responseBody: ResponseBody?): RestError.HttpError
    {
        if (responseBody == null) return RestError.HttpError(
            0,
            "Unknown error." //Don't want to pass context, hence hard-coded
        )

        val errorResponse: ErrorDto = gson.fromJson(responseBody.charStream(), errorDtoType)

        return RestError.HttpError(errorResponse.code, errorResponse.message)
    }
}