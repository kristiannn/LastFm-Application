package com.neno.lastfmapp.network.dto

import com.google.gson.annotations.SerializedName
import com.neno.lastfmapp.msToTime
import com.neno.lastfmapp.repository.models.TrackDetailsWrapper
import java.text.DecimalFormat

data class TrackDetailsBaseScope(
    @SerializedName("track")
    val trackDetailsDto: TrackDetailsDto
)

data class TrackDetailsAlbum(
    @SerializedName("artist")
    val artist: String,
    @SerializedName("title")
    val album: String,
    @SerializedName("image")
    val images: List<ImageDto>
)

data class TrackDetailsDto(
    @SerializedName("name")
    val track: String,
    @SerializedName("duration")
    val duration: Long,
    @SerializedName("listeners")
    val listeners: Int,
    @SerializedName("playcount")
    val playCount: Int,
    @SerializedName("album")
    val albumDetails: TrackDetailsAlbum?,
    @SerializedName("wiki")
    val bio: BioDto?,
    @SerializedName("toptags")
    val topTags: TopTags?
)

fun TrackDetailsDto.mapToRepository(): TrackDetailsWrapper
{
    return TrackDetailsWrapper(
        track = track,
        album = albumDetails?.album,
        artist = albumDetails?.artist,
        image = albumDetails?.images?.get(3)?.url,
        duration = duration.msToTime,
        listeners = DecimalFormat.getInstance().format(listeners),
        playCount = DecimalFormat.getInstance().format(playCount),
        published = bio?.published,
        bio = bio?.content?.substringBefore("<a")?.trim(),
        topTags = topTags?.tags?.map { it.name }
    )
}