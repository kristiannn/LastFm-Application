package com.neno.lastfmapp.modules.friends

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.neno.lastfmapp.Result
import com.neno.lastfmapp.modules.utils.TimeCalculator
import com.neno.lastfmapp.repository.LastFmRepository
import com.neno.lastfmapp.repository.models.FriendWrapper
import com.neno.lastfmapp.repository.models.mapToFriendWrapper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.core.context.GlobalContext.get

class FriendsViewModel(
    private val username: String,
    private val lastFmRepository: LastFmRepository,
) : ViewModel()
{
    private val timeCalculator: TimeCalculator by get().inject()

    private val _screenState = MutableLiveData<ScreenState>()
    private val _friendsListState = MutableLiveData<List<FriendWrapper>>()

    val screenState: LiveData<ScreenState>
        get() = _screenState

    val friendsListState: LiveData<List<FriendWrapper>>
        get() = _friendsListState

    init
    {
        _friendsListState.value = listOf()
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
                val friendsList = result.data.map { it.mapToFriendWrapper() }

                _friendsListState.postValue(friendsList)

                _screenState.postValue(
                    ScreenState(
                        isLoading = false,
                        errorMessage = null
                    )
                )

                updateAllListening(friendsList)
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

    private suspend fun updateAllListening(friendsList: List<FriendWrapper>?)
    {
        friendsList ?: return
        val mutableList = friendsList.toMutableList()

        friendsList.forEachIndexed { index, friend ->
            val recentTracks = lastFmRepository.getRecentTracks(
                username = friend.username,
                page = 1,
                limit = 1
            )

            if (recentTracks is Result.Success)
            {
                val fr = FriendWrapper(
                    username = friend.username,
                    realName = friend.realName,
                    profilePicture = friend.profilePicture,
                    totalScrobbles = recentTracks.data.first().totalScrobbles,
                    lastScrobble = recentTracks.data.first().artist + ": " + recentTracks.data.first().track,
                    lastScrobbleTime = recentTracks.data.first().date?.let { timeCalculator.convertToTime(it) }
                        ?: ""
                )

                mutableList[index] = fr
                _friendsListState.postValue(mutableList.toList())
            }
        }
    }

    data class ScreenState(
        val isLoading: Boolean = false,
        val errorMessage: String? = null
    )
}