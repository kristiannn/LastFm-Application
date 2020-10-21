package com.neno.lastfmapp.modules.details

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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
    private val _detailsState = MutableLiveData<DetailsState>()
    private var categoryType: DetailsCategoryType

    val screenState: LiveData<ScreenState>
        get() = _screenState

    val detailsState: LiveData<DetailsState>
        get() = _detailsState

    init
    {
        _detailsState.value = DetailsState()
        _screenState.value = ScreenState()

        categoryType = when
        {
            track != null -> DetailsCategoryType.TRACK
            album != null -> DetailsCategoryType.ALBUM
            else -> DetailsCategoryType.ARTIST
        }

        getDetails()
    }

    private fun getDetails()
    {
        viewModelScope.launch(Dispatchers.IO) {

            when (categoryType)
            {
                DetailsCategoryType.ARTIST -> requestDetails {
                    lastFmRepository.getArtistDetails(
                        artist = artist!!
                    )
                }

                DetailsCategoryType.ALBUM -> requestDetails {
                    lastFmRepository.getAlbumDetails(
                        artist = artist!!,
                        album = album!!
                    )
                }

                DetailsCategoryType.TRACK -> requestDetails {
                    lastFmRepository.getTrackDetails(
                        artist = artist!!,
                        track = track!!
                    )
                }
            }
        }
    }

    private suspend inline fun <Model> requestDetails(crossinline request: suspend () -> Result<Model>)
    {
        _screenState.postValue(
            ScreenState(
                isLoading = true,
                errorMessage = null
            )
        )

        val result = request()

        if (result is Result.Success)
        {
            when (result.data)
            {
                is ArtistDetailsWrapper -> _detailsState.postValue(DetailsState(artistDetails = result.data))
                is AlbumDetailsWrapper -> _detailsState.postValue(DetailsState(albumDetails = result.data))
                is TrackDetailsWrapper -> _detailsState.postValue(DetailsState(trackDetails = result.data))
            }

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

    data class DetailsState(
        val artistDetails: ArtistDetailsWrapper? = null,
        val albumDetails: AlbumDetailsWrapper? = null,
        val trackDetails: TrackDetailsWrapper? = null
    )

    data class ScreenState(
        val isLoading: Boolean = false,
        val errorMessage: String? = null
    )
}