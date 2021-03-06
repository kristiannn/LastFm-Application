package com.neno.lastfmapp.network.dto

import com.google.gson.annotations.SerializedName
import com.neno.lastfmapp.formatToTime
import com.neno.lastfmapp.repository.models.AlbumDetailsWrapper
import com.neno.lastfmapp.repository.models.AlbumTrackWrapper
import java.text.DecimalFormat
import java.util.concurrent.TimeUnit

data class AlbumDetailsBaseScope(
    @SerializedName("album")
    val albumDetailsDto: AlbumDetailsDto
)

data class AlbumDetailsDto(
    @SerializedName("name")
    val album: String,
    @SerializedName("artist")
    val artist: String,
    @SerializedName("image")
    val images: List<ImageDto>,
    @SerializedName("listeners")
    val listeners: Int,
    @SerializedName("playcount")
    val playCount: Int,
    @SerializedName("wiki")
    val bio: BioDto?,
    @SerializedName("tags")
    val topTags: TopTags?,
    @SerializedName("tracks")
    val albumTracks: AlbumTracks
)

fun AlbumDetailsDto.mapToRepository(): AlbumDetailsWrapper
{
    return AlbumDetailsWrapper(
        album = album,
        artist = artist,
        image = images[3].url,
        listeners = DecimalFormat.getInstance().format(listeners),
        playCount = DecimalFormat.getInstance().format(playCount),
        published = bio?.published,
        bio = bio?.content?.substringBefore("<a")?.trim(),
        topTags = topTags?.tags?.map { it.name },
        albumTracks = albumTracks.albumTracksList.map { AlbumTrackWrapper(it.name, it.duration.formatToTime(TimeUnit.SECONDS)) }
    )
}