package com.neno.lastfmapp.network

import com.neno.lastfmapp.Result
import com.neno.lastfmapp.repository.models.*

interface LastFmServiceOperations
{
    suspend fun getArtists(username: String, period: String, limit: Int, page: Int): Result<List<ArtistWrapper>>

    suspend fun getAlbums(username: String, period: String, limit: Int, page: Int): Result<List<AlbumWrapper>>

    suspend fun getTracks(username: String, period: String, limit: Int, page: Int): Result<List<TrackWrapper>>

    suspend fun getRecentTracks(username: String, limit: Int, page: Int): Result<List<RecentTrackWrapper>>

    suspend fun getProfile(username: String): Result<ProfileWrapper>

    suspend fun getFriends(username: String): Result<List<ProfileWrapper>>

    suspend fun getTrackDetails(artist: String, track: String): Result<TrackDetailsWrapper>

    suspend fun getAlbumDetails(artist: String, album: String): Result<AlbumDetailsWrapper>

    suspend fun getArtistDetails(artist: String): Result<ArtistDetailsWrapper>
}