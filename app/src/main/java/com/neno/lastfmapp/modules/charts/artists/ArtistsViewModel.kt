package com.neno.lastfmapp.modules.charts.artists

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.neno.lastfmapp.Result
import com.neno.lastfmapp.repository.LastFmRepository
import com.neno.lastfmapp.repository.models.ArtistWrapper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ArtistsViewModel(
    private val username: String,
    private val period: String,
    private val lastFmRepository: LastFmRepository
) : ViewModel()
{
    private var page = 0

    private val _screenState = MutableLiveData<ScreenState>()
    private val _artistsListState = MutableLiveData<List<ArtistWrapper>>()

    val screenState: LiveData<ScreenState>
        get() = _screenState

    val artistsListState: LiveData<List<ArtistWrapper>>
        get() = _artistsListState

    init
    {
        _artistsListState.value = listOf()
        _screenState.value = ScreenState()

        loadInitialData()
        getArtists(isReload = true)
    }

    private fun loadInitialData()
    {
        viewModelScope.launch(Dispatchers.IO) {

            _screenState.postValue(
                ScreenState(
                    isLoading = true,
                    isListUpdating = false,
                    errorMessage = null
                )
            )

            val result = lastFmRepository.getArtists(
                username = username,
                period = period,
                page = page,
                loadFromDb = true
            )

            if (result is Result.Success) _artistsListState.postValue(result.data)
        }
    }

    fun getArtists(isReload: Boolean)
    {
        viewModelScope.launch(Dispatchers.IO) {
            _screenState.postValue(
                ScreenState(
                    isLoading = isReload,
                    isListUpdating = !isReload,
                    errorMessage = null
                )
            )

            if (isReload) page = 1 else page++

            val result = lastFmRepository.getArtists(
                username = username,
                period = period,
                page = page,
                loadFromDb = false
            )

            if (result is Result.Success)
            {
                if (result.data.isEmpty())
                {
                    _screenState.postValue(
                        ScreenState(
                            isLoading = false,
                            isListUpdating = false,
                            errorMessage = null
                        )
                    )

                    return@launch
                }

                val newArtistsList: List<ArtistWrapper> =
                    if (isReload) result.data
                    else _artistsListState.value!! + result.data

                _artistsListState.postValue(newArtistsList)

                _screenState.postValue(
                    ScreenState(
                        isLoading = false,
                        isListUpdating = false,
                        errorMessage = null
                    )
                )
            } else if (result is Result.Error)
            {
                _screenState.postValue(
                    ScreenState(
                        isLoading = false,
                        isListUpdating = false,
                        errorMessage = result.error.message
                    )
                )
            }
        }
    }

    data class ScreenState(
        val isLoading: Boolean = false,
        val isListUpdating: Boolean = false,
        val errorMessage: String? = null
    )
}