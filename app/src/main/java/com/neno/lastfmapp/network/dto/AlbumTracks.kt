package com.neno.lastfmapp.network.dto

import com.google.gson.annotations.SerializedName

data class AlbumTracks(
    @SerializedName("track")
    val albumTracksList: List<AlbumTrack>
)

data class AlbumTrack(
    @SerializedName("name")
    val name: String,
    @SerializedName("duration")
    val duration: Long
)