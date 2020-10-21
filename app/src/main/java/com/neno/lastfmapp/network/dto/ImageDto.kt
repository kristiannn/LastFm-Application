package com.neno.lastfmapp.network.dto

import com.google.gson.annotations.SerializedName

data class ImageDto(
    @SerializedName("size")
    val size: String,
    @SerializedName("#text")
    val url: String
)