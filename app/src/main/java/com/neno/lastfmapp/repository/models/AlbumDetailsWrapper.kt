package com.neno.lastfmapp.repository.models

data class AlbumDetailsWrapper(
    val album: String,
    val artist: String,
    val image: String,
    val listeners: String,
    val playCount: String,
    val published: String?,
    val bio: String?,
    val topTags: List<String>?,
    val albumTracks: List<AlbumTrackWrapper>
)