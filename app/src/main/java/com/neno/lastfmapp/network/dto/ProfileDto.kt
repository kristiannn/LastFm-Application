package com.neno.lastfmapp.network.dto

import com.google.gson.annotations.SerializedName
import com.neno.lastfmapp.repository.models.ProfileWrapper

data class ProfileBaseScope(
    @SerializedName("user")
    val profile: ProfileDto
)

data class ProfileDto(
    @SerializedName("name")
    val username: String,
    @SerializedName("image")
    val profilePicture: List<ImageDto>,
    @SerializedName("playcount")
    val totalScrobbles: Int,
    @SerializedName("realname")
    val realName: String
)

fun ProfileDto.mapToRepository(): ProfileWrapper
{
    return ProfileWrapper(
        username = username,
        realName = realName,
        profilePicture = profilePicture[1].url,
        totalScrobbles = totalScrobbles
    )
}