package com.neno.lastfmapp.repository

import android.database.sqlite.SQLiteConstraintException
import com.neno.lastfmapp.Result
import com.neno.lastfmapp.database.LastFmDatabaseOperations
import com.neno.lastfmapp.network.LastFmAuthOperations
import com.neno.lastfmapp.network.LastFmServiceOperations
import com.neno.lastfmapp.network.utils.LastFmMethods
import com.neno.lastfmapp.repository.models.*
import com.neno.lastfmapp.repository.utils.LastFmQueryParams

class LastFmDataSource(
    private val lastFmServiceOperations: LastFmServiceOperations,
    private val lastFmAuthOperations: LastFmAuthOperations,
    private val lastFmDatabase: LastFmDatabaseOperations
) : LastFmRepository
{
    override suspend fun getArtists(
        username: String,
        period: String,
        page: Int,
        loadFromDb: Boolean
    ): Result<List<ArtistWrapper>> =
        requestLists(
            username = username,
            loadFromDb = loadFromDb,
            dbRequest = { lastFmDatabase.getArtists(username, period, (page - 1) * RESULTS_LIMIT) },
            restRequest = { lastFmServiceOperations.getArtists(username, period, RESULTS_LIMIT, page) },
            isUpToDateRequest = { isUpToDate(username) { lastFmDatabase.getUpdateTimeArtists(username, period, page) } },
            updateDbRequest = {
                it as Result.Success
                lastFmDatabase.saveArtists(username, period, it.data)
                lastFmDatabase.setUpdateTimeArtists(username, period, page)
            }
        )

    override suspend fun getAlbums(
        username: String,
        period: String,
        page: Int,
        loadFromDb: Boolean
    ): Result<List<AlbumWrapper>> =
        requestLists(
            username = username,
            loadFromDb = loadFromDb,
            dbRequest = { lastFmDatabase.getAlbums(username, period, (page - 1) * RESULTS_LIMIT) },
            restRequest = { lastFmServiceOperations.getAlbums(username, period, RESULTS_LIMIT, page) },
            isUpToDateRequest = { isUpToDate(username) { lastFmDatabase.getUpdateTimeAlbums(username, period, page) } },
            updateDbRequest = {
                it as Result.Success
                lastFmDatabase.saveAlbums(username, period, it.data)
                lastFmDatabase.setUpdateTimeAlbums(username, period, page)
            }
        )

    override suspend fun getTracks(
        username: String,
        period: String,
        page: Int,
        loadFromDb: Boolean
    ): Result<List<TrackWrapper>> = requestLists(
        username = username,
        loadFromDb = loadFromDb,
        dbRequest = { lastFmDatabase.getTracks(username, period, (page - 1) * RESULTS_LIMIT) },
        restRequest = { lastFmServiceOperations.getTracks(username, period, RESULTS_LIMIT, page) },
        isUpToDateRequest = { isUpToDate(username) { lastFmDatabase.getUpdateTimeTracks(username, period, page) } },
        updateDbRequest = {
            it as Result.Success
            lastFmDatabase.saveTracks(username, period, it.data)
            lastFmDatabase.setUpdateTimeTracks(username, period, page)
        }
    )

    override suspend fun getRecentTracks(username: String, page: Int): Result<List<RecentTrackWrapper>>
    {
        return lastFmServiceOperations.getRecentTracks(
            username = username,
            limit = RESULTS_LIMIT,
            page = page
        )
    }

    override suspend fun getProfile(username: String): Result<ProfileWrapper>
    {
        val dbProfile = lastFmDatabase.getProfile(username)
        if (dbProfile is Result.Success)
        {
            return dbProfile
        }

        val restProfile = lastFmServiceOperations.getProfile(username)
        if (restProfile is Result.Success)
        {
            lastFmDatabase.saveProfile(username, restProfile.data)
        }

        return restProfile
    }

    override suspend fun getFriends(username: String): Result<List<ProfileWrapper>>
    {
        return lastFmServiceOperations.getFriends(username)
    }

    override suspend fun getTrackDetails(artist: String, track: String): Result<TrackDetailsWrapper>
    {
        return lastFmServiceOperations.getTrackDetails(artist = artist, track = track)
    }

    override suspend fun getAlbumDetails(artist: String, album: String): Result<AlbumDetailsWrapper>
    {
        return lastFmServiceOperations.getAlbumDetails(artist, album)
    }

    override suspend fun getArtistDetails(artist: String): Result<ArtistDetailsWrapper>
    {
        return lastFmServiceOperations.getArtistDetails(artist)
    }

    override suspend fun getUserSession(username: String, password: String): Result<String>
    {
        val params = mapOf<String, String>(
            LastFmQueryParams.username to username,
            LastFmQueryParams.password to password,
            LastFmQueryParams.method to LastFmMethods.GET_MOBILE_SESSION
        )

        return lastFmAuthOperations.getSessionKey(params)
    }

    override suspend fun updateNowPlaying(artist: String, track: String, album: String?)
    {
        val params = mutableMapOf<String, String>(
            LastFmQueryParams.artist to artist,
            LastFmQueryParams.track to track,
            LastFmQueryParams.method to LastFmMethods.NOW_PLAYING_SONG
        )

        if (!album.isNullOrEmpty()) params[LastFmQueryParams.album] = album

        return lastFmAuthOperations.updateNowPlaying(params)
    }

    override suspend fun scrobbleTrack(artist: String, track: String, timestamp: String, album: String?): Result<Unit>
    {
        val params = mutableMapOf<String, String>(
            LastFmQueryParams.artist to artist,
            LastFmQueryParams.track to track,
            LastFmQueryParams.timestamp to timestamp,
            LastFmQueryParams.method to LastFmMethods.SCROBBLE_TRACK
        )

        if (!album.isNullOrEmpty()) params[LastFmQueryParams.album] = album

        return lastFmAuthOperations.scrobbleTrack(params)
    }

    private suspend inline fun <Model> requestLists(
        username: String,
        loadFromDb: Boolean,
        crossinline dbRequest: suspend () -> Result<Model>,
        crossinline restRequest: suspend () -> Result<Model>,
        crossinline isUpToDateRequest: suspend () -> Result<Boolean>,
        updateDbRequest: SaveItemsToDb<Model>
    ): Result<Model>
    {
        if (loadFromDb)
        {
            val dbResult = dbRequest()
            if (dbResult is Result.Success) return dbResult
        }

        val isDbUpToDate = isUpToDateRequest()
        if (isDbUpToDate is Result.Success && isDbUpToDate.data)
        {
            val dbResult = dbRequest()
            if (dbResult is Result.Success) return dbResult
        }

        val restResult = restRequest()
        if (restResult is Result.Success)
        {
            try
            {
                updateDbRequest.invoke(restResult)
            } catch (e: SQLiteConstraintException)
            {
                getProfile(username)
                updateDbRequest.invoke(restResult)
            }

        }

        return restResult
    }

    private suspend inline fun <Model> isUpToDate(
        username: String,
        crossinline dbRequest: suspend () -> Result<Model>
    ): Result<Boolean>
    {
        var lastUpdateDb = 0

        val lastPlayed = lastFmServiceOperations.getRecentTracks(username, 1, 1)
        if (lastPlayed is Result.Success)
        {
            val lastUpdateFetched =
                if (lastPlayed.data.first().date != null) lastPlayed.data.first().date else lastPlayed.data.last().date

            val dbResult = dbRequest()
            if (dbResult is Result.Success) lastUpdateDb = dbResult.data as Int

            return if (lastUpdateDb < lastUpdateFetched!!)
            {
                Result.Success(false)
            } else
            {
                Result.Success(true)
            }
        }

        return Result.Success(false)
    }

    companion object
    {
        private const val RESULTS_LIMIT = 50
    }
}

typealias SaveItemsToDb<Model> = (items: Result<Model>) -> Unit