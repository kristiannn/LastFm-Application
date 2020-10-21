package com.neno.lastfmapp.repository.models

data class TrackDetailsWrapper(
    val track: String,
    val album: String?,
    val artist: String?,
    val image: String?,
    val duration: String,
    val listeners: String,
    val playCount: String,
    val published: String?,
    val bio: String?
)