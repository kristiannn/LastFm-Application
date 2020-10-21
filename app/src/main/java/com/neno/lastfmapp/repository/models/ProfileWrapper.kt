package com.neno.lastfmapp.repository.models

import com.neno.lastfmapp.database.entities.ProfileEntity

data class ProfileWrapper(
    val username: String,
    val profilePicture: String,
    val totalScrobbles: Int
)

fun ProfileWrapper.mapToDb(): ProfileEntity
{
    return ProfileEntity(
        username = username,
        profilePicture = profilePicture,
        totalScrobbles = totalScrobbles
    )
}