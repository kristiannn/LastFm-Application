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
    private val _recentsListState = MutableLiveData<RecentsListState>()

    val screenState: LiveData<ScreenState>
        get() = _screenState

    val recentsListState: LiveData<RecentsListState>
        get() = _recentsListState

    init
    {
        _recentsListState.value = RecentsListState()
        _screenState.value = ScreenState()

        getRecentTracks(true)
    }

    fun getRecentTracks(isReload: Boolean)
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

            val result = lastFmRepository.getRecentTracks(
                username = username,
                page = page
            )

            if (result is Result.Success)
            {
                val newTracksList: List<RecentTrackWrapper> = if (isReload)
                {
                    result.data
                } else
                {
                    _recentsListState.value!!.tracksList + result.data.filter { it.date != null }
                }

                _recentsListState.postValue(RecentsListState(newTracksList))

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

    data class RecentsListState(
        val tracksList: List<RecentTrackWrapper> = listOf()
    )

    data class ScreenState(
        val isLoading: Boolean = false,
        val isListUpdating: Boolean = false,
        val errorMessage: String? = null
    )
}