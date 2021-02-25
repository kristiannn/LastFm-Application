package com.neno.lastfmapp.di

import com.neno.lastfmapp.modules.charts.albums.AlbumsViewModel
import com.neno.lastfmapp.modules.charts.artists.ArtistsViewModel
import com.neno.lastfmapp.modules.details.DetailsViewModel
import com.neno.lastfmapp.modules.friends.FriendsViewModel
import com.neno.lastfmapp.modules.login.LoginViewModel
import com.neno.lastfmapp.modules.recents.RecentsViewModel
import com.neno.lastfmapp.modules.charts.tracks.TracksViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val viewModelsModule = module {
    viewModel { (username: String, period: String) -> ArtistsViewModel(username, period, get()) }

    viewModel { (username: String, period: String) -> AlbumsViewModel(username, period, get()) }

    viewModel { (username: String, period: String) -> TracksViewModel(username, period, get()) }

    viewModel { (artist: String, album: String, track: String) -> DetailsViewModel(artist, album, track, get()) }

    viewModel { (username: String) -> FriendsViewModel(username, get()) }

    viewModel { LoginViewModel(get(), get()) }

    viewModel { (username: String) -> RecentsViewModel(username, get()) }
}