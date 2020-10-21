package com.neno.lastfmapp.network.dto

import com.google.gson.annotations.SerializedName

data class ErrorDto(
    @SerializedName("error")
    val code: Int,
    @SerializedName("message")
    val message: String
)