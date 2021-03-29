package com.neno.lastfmapp.modules.recents

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.neno.lastfmapp.Result
import com.neno.lastfmapp.repository.LastFmRepository
import com.neno.lastfmapp.repository.models.RecentTrackWrapper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class RecentsViewModel(
    private val username: String,
    private val lastFmRepository: LastFmRepository
) : ViewModel()
{
    private var page = 0

    private val _screenState = MutableLiveData<ScreenState>()
    private val _recentsListState = MutableLiveData<List<RecentTrackWrapper>>()

    val screenState: LiveData<ScreenState>
        get() = _screenState

    val recentsListState: LiveData<List<RecentTrackWrapper>>
        get() = _recentsListState

    init
    {
        _recentsListState.value = listOf()
        _screenState.value = ScreenState()

        getRecentTracks(true)
    }

    fun scrobbleTracks(selectedTracks: List<RecentTrackWrapper>)
    {
        viewModelScope.launch(Dispatchers.IO) {
            _screenState.postValue(
                ScreenState(
                    isLoading = true,
                    isListUpdating = false,
                    errorMessage = null,
                    scrobbles = null
                )
            )

            var scrobbledTracks = 0
            var failedScrobbles = 0

            selectedTracks.forEach {
                if (it.date != null)
                {
                    val response = lastFmRepository.scrobbleTrack(it.artist, it.track, it.date.toString(), it.album)

                    if (response is Result.Success)
                        scrobbledTracks++
                    else if (response is Result.Error)
                        failedScrobbles++
                }
            }

            _screenState.postValue(
                ScreenState(
                    isLoading = false,
                    isListUpdating = false,
                    errorMessage = null,
                    scrobbles = setOf(scrobbledTracks, failedScrobbles)
                )
            )
        }
    }

    fun getRecentTracks(isReload: Boolean)
    {
        viewModelScope.launch(Dispatchers.IO) {
            _screenState.postValue(
                ScreenState(
                    isLoading = isReload,
                    isListUpdating = !isReload,
                    errorMessage = null,
                    scrobbles = null
                )
            )

            if (isReload) page = 1 else page++

            val result = lastFmRepository.getRecentTracks(
                username = username,
                page = page
            )

            if (result is Result.Success)
            {
                val newTracksList: List<RecentTrackWrapper> =
                    if (isReload) result.data
                    else _recentsListState.value!! + result.data.filter { it.date != null }

                _recentsListState.postValue(newTracksList)

                _screenState.postValue(
                    ScreenState(
                        isLoading = false,
                        isListUpdating = false,
                        errorMessage = null,
                        scrobbles = null
                    )
                )
            } else if (result is Result.Error)
            {
                _screenState.postValue(
                    ScreenState(
                        isLoading = false,
                        isListUpdating = false,
                        errorMessage = result.error.message,
                        scrobbles = null
                    )
                )
            }
        }
    }

    data class ScreenState(
        val isLoading: Boolean = false,
        val isListUpdating: Boolean = false,
        val errorMessage: String? = null,
        val scrobbles: Set<Int>? = null
    )
}