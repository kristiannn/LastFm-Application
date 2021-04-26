package com.neno.lastfmapp.modules.details

import com.neno.lastfmapp.repository.models.AlbumTrackWrapper
import com.neno.lastfmapp.repository.models.ArtistWrapper

sealed class DetailsItem
{
    class Tags(val tagsList: List<String>) : DetailsItem()

    class SimilarArtists(val artistsList: List<ArtistWrapper>) : DetailsItem()

    class AlbumTracks(val tracksList: List<AlbumTrackWrapper>) : DetailsItem()

    class LabelValueHorizontal(val labelValue: Pair<Int, String>) : DetailsItem()

    class LabelValueVertical(val labelValue: Pair<Int, String>) : DetailsItem()

    class CoverImage(val url: String) : DetailsItem()

    object LoadingItem : DetailsItem()
}