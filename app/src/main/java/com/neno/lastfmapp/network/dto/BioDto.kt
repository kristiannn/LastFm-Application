package com.neno.lastfmapp.network.dto

import com.google.gson.annotations.SerializedName

data class BioDto(
    @SerializedName("published")
    val published: String,
    @SerializedName("content")
    val content: String
)