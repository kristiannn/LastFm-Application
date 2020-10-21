package com.neno.lastfmapp.network.dto

import com.google.gson.annotations.SerializedName

data class DateDto(
    @SerializedName("uts")
    val unixTime: Int
)