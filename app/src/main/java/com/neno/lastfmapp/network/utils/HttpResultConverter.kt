package com.neno.lastfmapp.network.utils

import okhttp3.ResponseBody

interface HttpResultConverter
{
    fun httpError(responseBody: ResponseBody?): RestError.HttpError
}