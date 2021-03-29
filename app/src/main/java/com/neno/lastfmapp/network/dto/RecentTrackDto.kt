package com.neno.lastfmapp.network.dto

import com.google.gson.annotations.SerializedName
import com.neno.lastfmapp.repository.models.RecentTrackWrapper

data class RecentTracksBaseScope(
    @SerializedName("recenttracks")
    val recentTracksScope: RecentTracksScope
)

data class RecentTracksScope(
    @SerializedName("@attr")
    val extraInfo: RecentExtraInfo,
    @SerializedName("track")
    val tracksList: List<RecentTrackDto>
)

data class RecentExtraInfo(
    @SerializedName("total")
    val totalScrobbles: Int
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

fun RecentTracksBaseScope.mapToRepository(): List<RecentTrackWrapper>
{
    return recentTracksScope.tracksList.map {
        RecentTrackWrapper(
            artist = it.artist.artist,
            album = it.album?.album,
            track = it.track,
            date = it.date?.unixTime,
            image = it.image[2].url,
            totalScrobbles = recentTracksScope.extraInfo.totalScrobbles
        )
    }
}