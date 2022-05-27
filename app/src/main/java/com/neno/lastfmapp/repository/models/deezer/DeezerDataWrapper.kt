package com.neno.lastfmapp.repository.models.deezer

import com.neno.lastfmapp.network.dto.deezer.DeezerAlbumDto
import com.neno.lastfmapp.network.dto.deezer.DeezerArtistDto

data class DeezerDataWrapper(
    val name: String,
    val picture: String?
)

fun DeezerArtistDto.mapToRepository(): DeezerDataWrapper
{
    return DeezerDataWrapper(
        name = name,
        picture = pictureXl ?: pictureBig ?: pictureMedium ?: pictureSmall
    )
}

fun DeezerAlbumDto.mapToRepository(): DeezerDataWrapper
{
    return DeezerDataWrapper(
        name = name,
        picture = pictureXl ?: pictureBig ?: pictureMedium ?: pictureSmall
    )
}