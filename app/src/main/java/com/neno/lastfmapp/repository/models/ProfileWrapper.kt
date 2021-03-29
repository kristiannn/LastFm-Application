package com.neno.lastfmapp.repository.models

import com.neno.lastfmapp.database.entities.ProfileEntity

data class ProfileWrapper(
    val username: String,
    val profilePicture: String,
    val totalScrobbles: Int,
    val realName: String
)

fun ProfileWrapper.mapToDb(): ProfileEntity
{
    return ProfileEntity(
        username = username,
        profilePicture = profilePicture,
        totalScrobbles = totalScrobbles
    )
}

fun ProfileWrapper.mapToFriendWrapper(): FriendWrapper
{
    return FriendWrapper(
        username = username,
        realName = realName,
        profilePicture = profilePicture,
        totalScrobbles = totalScrobbles,
        lastScrobble = "",
        lastScrobbleTime = ""
    )
}