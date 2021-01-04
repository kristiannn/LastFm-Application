package com.neno.lastfmapp.network.dto

import com.google.gson.annotations.SerializedName
import com.neno.lastfmapp.repository.models.RecentTrackWrapper

data class RecentTracksBaseScope(
    @SerializedName("recenttracks")
    val recentTracksScope: RecentTracksScope
)

data class RecentTracksScope(
    @SerializedName("track")
    val tracksList: List<RecentTrackDto>
)

data class RecentArtistDto(
    @SerializedName("#text")
    val artist: String
)

data class RecentAlbumDto(
    @SerializedName("#text")
    val album: String
)

data class RecentTrackDto(
    @SerializedName("artist")
    val artist: RecentArtistDto,
    @SerializedName("album")
    val album: RecentAlbumDto?,
    @SerializedName("name")
    val track: String,
    @SerializedName("date")
    val date: DateDto?,
    @SerializedName("image")
    val image: List<ImageDto>
)

fun RecentTrackDto.mapToRepository(): RecentTrackWrapper
{
    return RecentTrackWrapper(
        artist = artist.artist,
        album = album?.album,
        track = track,
        date = date?.unixTime,
        image = image[2].url
    )
}