package com.neno.lastfmapp.network.dto.deezer

import com.google.gson.annotations.SerializedName

data class DeezerSearchDto(
    @SerializedName("data")
    val resultsList: List<DeezerSearchDtoDetails?>
)
