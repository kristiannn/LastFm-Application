package com.neno.lastfmapp.network.dto.deezer

import com.google.gson.annotations.SerializedName

data class DeezerSearchDtoDetails(
    @SerializedName("artist")
    val artist: DeezerArtistDto,
    @SerializedName("album")
    val album: DeezerAlbumDto
)
