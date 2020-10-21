package com.neno.lastfmapp.database

import com.neno.lastfmapp.Result
import com.neno.lastfmapp.repository.models.AlbumWrapper
import com.neno.lastfmapp.repository.models.ArtistWrapper
import com.neno.lastfmapp.repository.models.ProfileWrapper
import com.neno.lastfmapp.repository.models.TrackWrapper

interface LastFmDatabaseOperations
{
    suspend fun getProfile(username: String): Result<ProfileWrapper>
    suspend fun getArtists(username: String, period: String, offset: Int): Result<List<ArtistWrapper>>
    suspend fun getAlbums(username: String, period: String, offset: Int): Result<List<AlbumWrapper>>
    suspend fun getTracks(username: String, period: String, offset: Int): Result<List<TrackWrapper>>

    suspend fun saveProfile(username: String, profileWrapper: ProfileWrapper)
    suspend fun saveArtists(username: String, period: String, artistsList: List<ArtistWrapper>)
    suspend fun saveAlbums(username: String, period: String, albumsList: List<AlbumWrapper>)
    suspend fun saveTracks(username: String, period: String, tracksList: List<TrackWrapper>)

    suspend fun getUpdateTimeArtists(username: String): Result<Int>
    suspend fun getUpdateTimeAlbums(username: String): Result<Int>
    suspend fun getUpdateTimeTracks(username: String): Result<Int>

    suspend fun setUpdateTimeArtists(username: String)
    suspend fun setUpdateTimeAlbums(username: String)
    suspend fun setUpdateTimeTracks(username: String)
}
