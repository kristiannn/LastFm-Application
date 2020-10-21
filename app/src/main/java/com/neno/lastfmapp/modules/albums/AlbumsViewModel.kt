package com.neno.lastfmapp.modules.albums

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.neno.lastfmapp.Result
import com.neno.lastfmapp.repository.LastFmRepository
import com.neno.lastfmapp.repository.models.AlbumWrapper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class AlbumsViewModel(
    private val username: String,
    private val period: String,
    private val lastFmRepository: LastFmRepository
) : ViewModel()
{
    private var page = 0

    private val _screenState = MutableLiveData<ScreenState>()
    private val _albumsListState = MutableLiveData<AlbumsListState>()

    val screenState: LiveData<ScreenState>
        get() = _screenState

    val albumsListState: LiveData<AlbumsListState>
        get() = _albumsListState

    init
    {
        _albumsListState.value = AlbumsListState()
        _screenState.value = ScreenState()

        loadInitialData()
        getAlbums(true)
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

            val result = lastFmRepository.getAlbums(
                username = username,
                period = period,
                page = page,
                loadFromDb = true
            )

            if (result is Result.Success)
            {
                delay(350) //Wait for animations to finish, because populating this is costly
                _albumsListState.postValue(AlbumsListState(result.data))
            }
        }
    }

    fun getAlbums(isReload: Boolean)
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

            val result = lastFmRepository.getAlbums(
                username = username,
                period = period,
                page = page,
                loadFromDb = false
            )

            if (result is Result.Success)
            {
                val newAlbumsList: List<AlbumWrapper> = if (isReload)
                {
                    result.data
                } else
                {
                    albumsListState.value!!.albumsList + result.data
                }

                _albumsListState.postValue(AlbumsListState(newAlbumsList))

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

    data class AlbumsListState(
        val albumsList: List<AlbumWrapper> = listOf()
    )

    data class ScreenState(
        val isLoading: Boolean = false,
        val isListUpdating: Boolean = false,
        val errorMessage: String? = null
    )
}