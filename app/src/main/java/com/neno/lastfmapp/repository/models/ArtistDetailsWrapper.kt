package com.neno.lastfmapp.repository.models

data class ArtistDetailsWrapper(
    val artist: String,
    val image: String,
    val listeners: String,
    val playCount: String,
    val published: String?,
    val bio: String?,
    val topTags: List<String>?,
    val similarArtists: List<ArtistWrapper>
)