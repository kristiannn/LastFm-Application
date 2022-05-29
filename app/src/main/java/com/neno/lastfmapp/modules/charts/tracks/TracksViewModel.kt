package com.neno.lastfmapp.modules.charts.tracks

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.neno.lastfmapp.Result
import com.neno.lastfmapp.isLastFmImage
import com.neno.lastfmapp.repository.DeezerRepository
import com.neno.lastfmapp.repository.LastFmRepository
import com.neno.lastfmapp.repository.models.TrackWrapper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class TracksViewModel(
    private val username: String,
    private val period: String,
    private val lastFmRepository: LastFmRepository,
    private val deezerRepository: DeezerRepository
) : ViewModel()
{
    private var page = 0

    private val _screenState = MutableLiveData<ScreenState>()
    private val _tracksListState = MutableLiveData<List<TrackWrapper>>()

    val screenState: LiveData<ScreenState>
        get() = _screenState

    val tracksListState: LiveData<List<TrackWrapper>>
        get() = _tracksListState

    init
    {
        _tracksListState.value = listOf()
        _screenState.value = ScreenState()

        loadInitialData()
        getTracks(isReload = true)
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
                // Since we're loading the 3 tabs at the same time, this makes the animations a lot smoother
                delay(300)
                _tracksListState.postValue(result.data)
            }
        }
    }

    private suspend fun replaceLastFmImages(tracksList: List<TrackWrapper>)
    {
        val newTracksList = tracksList.toMutableList()

        tracksList.forEachIndexed { index, trackWrapper ->
            if (!trackWrapper.image.isLastFmImage())
            {
                return@forEachIndexed
            }

            val result = deezerRepository.getTrackPicture(username, period, trackWrapper)
            if (result is Result.Success)
            {
                val newTrackWrapper =
                    TrackWrapper(trackWrapper.track, trackWrapper.artist, trackWrapper.playCount, result.data)
                newTracksList.removeAt(index)
                newTracksList.add(index, newTrackWrapper)
            }
        }
        _tracksListState.postValue(newTracksList)
    }

    /**
     * Creates a new list which includes the already loaded images from the last one.
     * It's needed since the request to Last FM returns us a list with placeholder images,
     * so we want to replace the ones we've already loaded from Deezer.
     * */
    private fun createNewTracksList(
        oldList: List<TrackWrapper>?,
        listFromResult: List<TrackWrapper>
    ): List<TrackWrapper>
    {
        if (oldList?.isNullOrEmpty() == false)
        {
            val finalList = mutableListOf<TrackWrapper>()

            listFromResult.forEach { newListTrack ->
                val matchingTrack = oldList.find { oldListTrack ->
                    newListTrack.track == oldListTrack.track && newListTrack.artist == oldListTrack.artist
                }
                if (matchingTrack != null)
                {
                    finalList.add(
                        TrackWrapper(
                            newListTrack.track,
                            newListTrack.artist,
                            newListTrack.playCount,
                            matchingTrack.image
                        )
                    )
                } else
                {
                    finalList.add(newListTrack)
                }
            }

            return finalList
        } else
        {
            return listFromResult
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
                val listFromResult: List<TrackWrapper> =
                    if (isReload) result.data
                    else _tracksListState.value!! + result.data
                val oldTracksList = _tracksListState.value

                val newTracksList = createNewTracksList(oldTracksList, listFromResult)
                _tracksListState.postValue(newTracksList)

                _screenState.postValue(
                    ScreenState(
                        isLoading = false,
                        isListUpdating = false,
                        errorMessage = null
                    )
                )

                replaceLastFmImages(newTracksList)
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