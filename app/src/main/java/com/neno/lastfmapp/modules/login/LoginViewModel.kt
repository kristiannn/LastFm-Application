package com.neno.lastfmapp.modules.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.neno.lastfmapp.Result
import com.neno.lastfmapp.modules.utils.AccountManager
import com.neno.lastfmapp.repository.LastFmRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class LoginViewModel(
    private val lastFmRepository: LastFmRepository,
    private val accountManager: AccountManager
) : ViewModel()
{
    private val _screenState = MutableLiveData<ScreenState>()

    val screenState: LiveData<ScreenState>
        get() = _screenState

    init
    {
        _screenState.value = ScreenState()
    }

    fun getProfile(username: String?)
    {
        viewModelScope.launch(Dispatchers.IO) {
            _screenState.postValue(
                ScreenState(
                    isLoading = true,
                    errorMessage = null,
                    userLogged = null
                )
            )

            if (username.isNullOrEmpty())
            {
                _screenState.postValue(
                    ScreenState(
                        isLoading = false,
                        errorMessage = ValidationError.EmptyUsername.message,
                        userLogged = null
                    )
                )
                return@launch
            }

            val result = lastFmRepository.getProfile(username)

            if (result is Result.Success)
            {
                accountManager.saveUser(username = result.data.username, pictureUrl = result.data.profilePicture)

                _screenState.postValue(
                    ScreenState(
                        isLoading = false,
                        errorMessage = null,
                        userLogged = result.data.username
                    )
                )
            } else if (result is Result.Error)
            {
                _screenState.postValue(
                    ScreenState(
                        isLoading = false,
                        errorMessage = result.error.message,
                        userLogged = null
                    )
                )
            }
        }
    }

    data class ScreenState(
        val isLoading: Boolean = false,
        val errorMessage: String? = null,
        val userLogged: String? = null
    )
}

sealed class ValidationError(message: String? = null) : Throwable(message)
{
    object EmptyUsername : ValidationError("You need to type in a username!")
}