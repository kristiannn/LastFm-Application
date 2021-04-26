package com.neno.lastfmapp.modules.details

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.neno.lastfmapp.R
import com.neno.lastfmapp.Result
import com.neno.lastfmapp.repository.LastFmRepository
import com.neno.lastfmapp.repository.models.AlbumDetailsWrapper
import com.neno.lastfmapp.repository.models.ArtistDetailsWrapper
import com.neno.lastfmapp.repository.models.TrackDetailsWrapper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class DetailsViewModel(
    private val artist: String?,
    private val album: String?,
    private val track: String?,
    private val lastFmRepository: LastFmRepository
) : ViewModel()
{
    private val _screenState = MutableLiveData<ScreenState>()
    private val _detailsState = MutableLiveData<List<DetailsItem>>()
    private var categoryType: DetailsCategoryType = when
    {
        track != null -> DetailsCategoryType.TRACK
        album != null -> DetailsCategoryType.ALBUM
        else -> DetailsCategoryType.ARTIST
    }

    val screenState: LiveData<ScreenState>
        get() = _screenState

    val detailsState: LiveData<List<DetailsItem>>
        get() = _detailsState

    init
    {
        getDetails()
    }

    private fun getDetails()
    {
        viewModelScope.launch(Dispatchers.IO) {

            _screenState.postValue(
                ScreenState(
                    isLoading = true,
                    errorMessage = null
                )
            )

            val result = when (categoryType)
            {
                DetailsCategoryType.ARTIST -> lastFmRepository.getArtistDetails(artist = artist!!)
                DetailsCategoryType.ALBUM -> lastFmRepository.getAlbumDetails(artist = artist!!, album = album!!)
                DetailsCategoryType.TRACK -> lastFmRepository.getTrackDetails(artist = artist!!, track = track!!)
            }

            if (result is Result.Success)
            {
                // Replace the whole list building process with buildList once it's stable
                val detailsList: List<DetailsItem> = when (categoryType)
                {
                    DetailsCategoryType.ARTIST ->
                    {
                        result.data as ArtistDetailsWrapper

                        listOf(
                            DetailsItem.CoverImage(result.data.image),
                            DetailsItem.Tags(result.data.topTags ?: emptyList()),
                            DetailsItem.LabelValueHorizontal(Pair(R.string.artist, result.data.artist)),
                            DetailsItem.LabelValueHorizontal(Pair(R.string.published, result.data.published ?: "-")),
                            DetailsItem.LabelValueHorizontal(Pair(R.string.listeners, result.data.listeners)),
                            DetailsItem.LabelValueHorizontal(Pair(R.string.playCount, result.data.playCount)),
                            DetailsItem.LabelValueVertical(Pair(R.string.bio, result.data.bio ?: "-")),
                            DetailsItem.SimilarArtists(result.data.similarArtists)
                        )
                    }
                    DetailsCategoryType.ALBUM ->
                    {
                        result.data as AlbumDetailsWrapper

                        listOf(
                            DetailsItem.CoverImage(result.data.image),
                            DetailsItem.Tags(result.data.topTags ?: emptyList()),
                            DetailsItem.LabelValueHorizontal(Pair(R.string.artist, result.data.artist)),
                            DetailsItem.LabelValueHorizontal(Pair(R.string.album, result.data.album)),
                            DetailsItem.LabelValueHorizontal(Pair(R.string.published, result.data.published ?: "-")),
                            DetailsItem.LabelValueHorizontal(Pair(R.string.listeners, result.data.listeners)),
                            DetailsItem.LabelValueHorizontal(Pair(R.string.playCount, result.data.playCount)),
                            DetailsItem.LabelValueVertical(Pair(R.string.bio, result.data.bio ?: "-")),
                            DetailsItem.AlbumTracks(result.data.albumTracks)
                        )
                    }
                    DetailsCategoryType.TRACK ->
                    {
                        result.data as TrackDetailsWrapper

                        listOf(
                            DetailsItem.CoverImage(result.data.image ?: ""),
                            DetailsItem.Tags(result.data.topTags ?: emptyList()),
                            DetailsItem.LabelValueHorizontal(Pair(R.string.artist, result.data.artist ?: "-")),
                            DetailsItem.LabelValueHorizontal(Pair(R.string.album, result.data.album ?: "-")),
                            DetailsItem.LabelValueHorizontal(Pair(R.string.track, result.data.track)),
                            DetailsItem.LabelValueHorizontal(Pair(R.string.duration, result.data.duration)),
                            DetailsItem.LabelValueHorizontal(Pair(R.string.published, result.data.published ?: "-")),
                            DetailsItem.LabelValueHorizontal(Pair(R.string.listeners, result.data.listeners)),
                            DetailsItem.LabelValueHorizontal(Pair(R.string.playCount, result.data.playCount)),
                            DetailsItem.LabelValueVertical(Pair(R.string.bio, result.data.bio ?: "-"))
                        )
                    }
                }

                _detailsState.postValue(detailsList)

                _screenState.postValue(
                    ScreenState(
                        isLoading = false,
                        errorMessage = null
                    )
                )
            } else if (result is Result.Error)
            {
                _screenState.postValue(
                    ScreenState(
                        isLoading = false,
                        errorMessage = result.error.message
                    )
                )
            }
        }
    }

    data class ScreenState(
        val isLoading: Boolean = false,
        val errorMessage: String? = null
    )
}