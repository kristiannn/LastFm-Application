package com.neno.lastfmapp.network.dto.deezer

import com.google.gson.annotations.SerializedName

data class DeezerArtistDto(
    @SerializedName("name")
    val name: String,
    @SerializedName("picture_small")
    val pictureSmall: String,
    @SerializedName("picture_medium")
    val pictureMedium: String,
    @SerializedName("picture_big")
    val pictureBig: String,
    @SerializedName("picture_xl")
    val pictureXl: String
)