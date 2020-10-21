package com.neno.lastfmapp.network.dto

import com.google.gson.annotations.SerializedName
import com.neno.lastfmapp.repository.models.TrackWrapper

data class TopTracksBaseScope(
    @SerializedName("toptracks")
    val trackScope: TrackScope
)

data class TrackScope(
    @SerializedName("track")
    val tracksList: List<TrackDto>
)

data class TrackDto(
    @SerializedName("artist")
    val artist: ArtistDto,
    @SerializedName("name")
    val track: String,
    @SerializedName("playcount")
    val playCount: Int,
    @SerializedName("image")
    val images: List<ImageDto>
)

fun TrackDto.mapToRepository(): TrackWrapper
{
    return TrackWrapper(
        track = track,
        artist = artist.artist,
        playCount = playCount,
        image = images[2].url
    )
}