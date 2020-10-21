package com.neno.lastfmapp.repository.models

data class ArtistDetailsWrapper(
    val artist: String,
    val image: String,
    val listeners: String,
    val playCount: String,
    val published: String?,
    val bio: String?
)