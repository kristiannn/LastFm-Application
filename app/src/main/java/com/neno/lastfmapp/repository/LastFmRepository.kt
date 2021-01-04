package com.neno.lastfmapp.repository

import com.neno.lastfmapp.Result
import com.neno.lastfmapp.repository.models.*

interface LastFmRepository
{
    suspend fun getArtists(username: String, period: String, page: Int, loadFromDb: Boolean): Result<List<ArtistWrapper>>

    suspend fun getAlbums(username: String, period: String, page: Int, loadFromDb: Boolean): Result<List<AlbumWrapper>>

    suspend fun getTracks(username: String, period: String, page: Int, loadFromDb: Boolean): Result<List<TrackWrapper>>

    suspend fun getRecentTracks(username: String, page: Int) : Result<List<RecentTrackWrapper>>

    suspend fun getProfile(username: String): Result<ProfileWrapper>

    suspend fun getFriends(username: String): Result<List<ProfileWrapper>>

    suspend fun getTrackDetails(artist: String, track: String): Result<TrackDetailsWrapper>

    suspend fun getAlbumDetails(artist: String, album: String): Result<AlbumDetailsWrapper>

    suspend fun getArtistDetails(artist: String): Result<ArtistDetailsWrapper>

    suspend fun getUserSession(username: String, password: String): Result<String>

    suspend fun updateNowPlaying(artist: String, track: String, album: String?)

    suspend fun scrobbleTrack(artist: String, track: String, timestamp: String, album: String?): Result<Unit>
}