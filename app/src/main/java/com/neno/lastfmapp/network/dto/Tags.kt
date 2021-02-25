package com.neno.lastfmapp.network.dto

import com.google.gson.annotations.SerializedName

data class TopTags(
    @SerializedName("tag")
    val tags: List<Tag>
)

data class Tag(
    @SerializedName("name")
    val name: String
)