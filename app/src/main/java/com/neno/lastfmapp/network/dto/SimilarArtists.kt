package com.neno.lastfmapp.network.dto

import com.google.gson.annotations.SerializedName

data class SimilarArtistsList(
    @SerializedName("artist")
    val artistsList: List<SimilarArtists>
)

data class SimilarArtists(
    @SerializedName("name")
    val name: String,
    @SerializedName("image")
    val images: List<ImageDto>
)