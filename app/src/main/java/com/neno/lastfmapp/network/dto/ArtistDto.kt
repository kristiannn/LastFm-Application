package com.neno.lastfmapp.network.dto

import com.google.gson.annotations.SerializedName
import com.neno.lastfmapp.repository.models.ArtistWrapper

data class TopArtistsBaseScope(
    @SerializedName("topartists")
    val artistScope: ArtistScope
)

data class ArtistScope(
    @SerializedName("artist")
    val artistsList: List<ArtistDto>
)

data class ArtistDto(
    @SerializedName("name")
    val artist: String,
    @SerializedName("playcount")
    val playCount: Int,
    @SerializedName("image")
    val images: List<ImageDto>
)

fun ArtistDto.mapToRepository(): ArtistWrapper
{
    return ArtistWrapper(
        artist = artist,
        playCount = playCount,
        image = images[2].url
    )
}