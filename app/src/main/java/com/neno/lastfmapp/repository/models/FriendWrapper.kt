package com.neno.lastfmapp.repository.models

data class FriendWrapper(
    val username: String,
    val realName: String,
    val profilePicture: String,
    val totalScrobbles: Int,
    val lastScrobble: String,
    val lastScrobbleTime: String
)
