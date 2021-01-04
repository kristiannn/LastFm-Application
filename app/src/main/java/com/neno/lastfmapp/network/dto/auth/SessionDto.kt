package com.neno.lastfmapp.network.dto.auth

import com.google.gson.annotations.SerializedName

data class SessionDto(
    @SerializedName("session")
    val sessionDetailsDto: SessionDetailsDto
)
