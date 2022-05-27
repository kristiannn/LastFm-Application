package com.neno.lastfmapp.network.dto.deezer

import com.google.gson.annotations.SerializedName

data class DeezerAlbumDto(
    @SerializedName("title")
    val name: String,
    @SerializedName("cover_small")
    val pictureSmall: String?,
    @SerializedName("cover_medium")
    val pictureMedium: String?,
    @SerializedName("cover_big")
    val pictureBig: String?,
    @SerializedName("cover_xl")
    val pictureXl: String?
)