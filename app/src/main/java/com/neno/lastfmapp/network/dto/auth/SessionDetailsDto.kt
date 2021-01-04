package com.neno.lastfmapp.network.dto.auth

import com.google.gson.annotations.SerializedName

//TODO - if we end up only needing the key - don't download useless info
data class SessionDetailsDto(
    @SerializedName("subscriber")
    val subscriber: Int,
    @SerializedName("username")
    val username: String,
    @SerializedName("key")
    val sessionKey: String
)
