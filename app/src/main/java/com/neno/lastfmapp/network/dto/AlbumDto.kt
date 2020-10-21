package com.neno.lastfmapp.network.dto

import com.google.gson.annotations.SerializedName
import com.neno.lastfmapp.repository.models.AlbumWrapper

data class TopAlbumsBaseScope(
    @SerializedName("topalbums")
    val albumScope: AlbumScope
)

data class AlbumScope(
    @SerializedName("album")
    val albumsList: List<AlbumDto>
)

data class AlbumArtist(
    @SerializedName("name")
    val artist: String
)

data class AlbumDto(
    @SerializedName("name")
    val album: String,
    @SerializedName("artist")
    val albumArtist: AlbumArtist,
    @SerializedName("playcount")
    val playCount: Int,
    @SerializedName("image")
    val images: List<ImageDto>
)

fun AlbumDto.mapToRepository(): AlbumWrapper
{
    return AlbumWrapper(
        album = album,
        artist = albumArtist.artist,
        playCount = playCount,
        image = images[2].url
    )
}