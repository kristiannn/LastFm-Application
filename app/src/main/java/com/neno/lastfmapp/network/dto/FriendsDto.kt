package com.neno.lastfmapp.network.dto

import com.google.gson.annotations.SerializedName

data class FriendsBaseScope(
    @SerializedName("friends")
    val friends: FriendsDto
)

data class FriendsDto(
    @SerializedName("user")
    val friendsList: List<ProfileDto>
)