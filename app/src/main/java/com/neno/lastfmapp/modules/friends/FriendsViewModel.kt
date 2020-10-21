package com.neno.lastfmapp.modules.friends

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.neno.lastfmapp.Result
import com.neno.lastfmapp.repository.LastFmRepository
import com.neno.lastfmapp.repository.models.ProfileWrapper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class FriendsViewModel(
    private val username: String,
    private val lastFmRepository: LastFmRepository,
) : ViewModel()
{
    private val _screenState = MutableLiveData<ScreenState>()
    private val _friendsListState = MutableLiveData<FriendsListState>()

    val screenState: LiveData<ScreenState>
        get() = _screenState

    val friendsListState: LiveData<FriendsListState>
        get() = _friendsListState

    init
    {
        _friendsListState.value = FriendsListState()
        _screenState.value = ScreenState()

        getFriends()
    }

    fun getFriends()
    {
        viewModelScope.launch(Dispatchers.IO) {
            _screenState.postValue(ScreenState(isLoading = true, errorMessage = null))

            val result = lastFmRepository.getFriends(username = username)
            if (result is Result.Success)
            {
                _friendsListState.postValue(FriendsListState(result.data))

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

    data class FriendsListState(
        val friendsList: List<ProfileWrapper> = listOf()
    )

    data class ScreenState(
        val isLoading: Boolean = false,
        val errorMessage: String? = null
    )
}