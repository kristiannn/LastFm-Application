package com.neno.lastfmapp.modules.tracks

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.neno.lastfmapp.Result
import com.neno.lastfmapp.repository.LastFmRepository
import com.neno.lastfmapp.repository.models.TrackWrapper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class TracksViewModel(
    private val username: String,
    private val period: String,
    private val lastFmRepository: LastFmRepository
) : ViewModel()
{
    private var page = 0

    private val _screenState = MutableLiveData<ScreenState>()
    private val _tracksListState = MutableLiveData<TracksListState>()

    val screenState: LiveData<ScreenState>
        get() = _screenState

    val tracksListState: LiveData<TracksListState>
        get() = _tracksListState

    init
    {
        _tracksListState.value = TracksListState()
        _screenState.value = ScreenState()

        loadInitialData()
        getTracks(true)
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

            val result = lastFmRepository.getTracks(
                username = username,
                period = period,
                page = page,
                loadFromDb = true
            )

            if (result is Result.Success)
            {
                delay(350) //Wait for animations to finish, because populating this is costly
                _tracksListState.postValue(TracksListState(result.data))
            }
        }
    }

    fun getTracks(isReload: Boolean)
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

            val result = lastFmRepository.getTracks(
                username = username,
                period = period,
                page = page,
                loadFromDb = false
            )

            if (result is Result.Success)
            {
                val newTracksList: List<TrackWrapper> = if (isReload)
                {
                    result.data
                } else
                {
                    _tracksListState.value!!.tracksList + result.data
                }

                _tracksListState.postValue(TracksListState(newTracksList))

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

    data class TracksListState(
        val tracksList: List<TrackWrapper> = listOf()
    )

    data class ScreenState(
        val isLoading: Boolean = false,
        val isListUpdating: Boolean = false,
        val errorMessage: String? = null
    )
}