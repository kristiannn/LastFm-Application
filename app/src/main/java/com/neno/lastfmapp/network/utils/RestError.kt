package com.neno.lastfmapp.network.utils

sealed class RestError(message: String) : Throwable(message)
{
    class HttpError(code: Int, body: String) : RestError("Error $code: $body.")

    object NetworkError : RestError("Unable to gather results due to connectivity issues.")

    object NullResult : RestError("The request returned a null result.")
}