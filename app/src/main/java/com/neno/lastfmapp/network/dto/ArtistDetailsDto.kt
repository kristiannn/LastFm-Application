package com.neno.lastfmapp.network.dto

import com.google.gson.annotations.SerializedName
import com.neno.lastfmapp.repository.models.ArtistDetailsWrapper
import com.neno.lastfmapp.repository.models.ArtistWrapper
import java.text.DecimalFormat

data class ArtistDetailsBaseScope(
    @SerializedName("artist")
    val artistDetailsDto: ArtistDetailsDto
)

data class ArtistStats(
    @SerializedName("listeners")
    val listeners: Int,
    @SerializedName("playcount")
    val playCount: Int
)

data class ArtistDetailsDto(
    @SerializedName("name")
    val artist: String,
    @SerializedName("image")
    val images: List<ImageDto>,
    @SerializedName("stats")
    val stats: ArtistStats,
    @SerializedName("bio")
    val bio: BioDto?,
    @SerializedName("tags")
    val topTags: TopTags?,
    @SerializedName("similar")
    val similarArtistsList: SimilarArtistsList
)

fun ArtistDetailsDto.mapToRepository(): ArtistDetailsWrapper
{
    return ArtistDetailsWrapper(
        artist = artist,
        image = images[3].url,
        listeners = DecimalFormat.getInstance().format(stats.listeners),
        playCount = DecimalFormat.getInstance().format(stats.playCount),
        published = bio?.published,
        bio = bio?.content?.substringBefore("<a")?.trim(),
        topTags = topTags?.tags?.map { it.name },
        similarArtists = similarArtistsList.artistsList.map { ArtistWrapper(it.name, 0, it.images.last().url) }
    )
}