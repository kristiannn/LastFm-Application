package com.neno.lastfmapp.database

import com.neno.lastfmapp.Result
import com.neno.lastfmapp.database.entities.AlbumUpdateEntity
import com.neno.lastfmapp.database.entities.ArtistUpdateEntity
import com.neno.lastfmapp.database.entities.TrackUpdateEntity
import com.neno.lastfmapp.database.entities.mapToRepository
import com.neno.lastfmapp.repository.models.*

class DatabaseDataSource(private val lastFmDatabase: LastFmDatabase) : LastFmDatabaseOperations
{
    override suspend fun getProfile(username: String): Result<ProfileWrapper>
    {
        lastFmDatabase.profileDao().getProfile(username)?.let {
            return Result.Success(it.mapToRepository())
        } ?: return Result.Error(DatabaseError.NullResult)
    }

    override suspend fun getArtists(username: String, period: String, offset: Int): Result<List<ArtistWrapper>>
    {
        lastFmDatabase.artistDao().getArtists(username, period, offset)?.let { artists ->
            if (artists.isEmpty()) return Result.Error(DatabaseError.NullResult)

            val result = artists.map { it.mapToRepository() }
            return Result.Success(result)
        } ?: return Result.Error(DatabaseError.NullResult)
    }

    override suspend fun getAlbums(username: String, period: String, offset: Int): Result<List<AlbumWrapper>>
    {
        lastFmDatabase.albumDao().getAlbums(username, period, offset)?.let { albums ->
            if (albums.isEmpty()) return Result.Error(DatabaseError.NullResult)

            val result = albums.map { it.mapToRepository() }
            return Result.Success(result)
        } ?: return Result.Error(DatabaseError.NullResult)
    }

    override suspend fun getTracks(username: String, period: String, offset: Int): Result<List<TrackWrapper>>
    {
        lastFmDatabase.trackDao().getTracks(username, period, offset)?.let { tracks ->
            if (tracks.isEmpty()) return Result.Error(DatabaseError.NullResult)

            val result = tracks.map { it.mapToRepository() }
            return Result.Success(result)
        } ?: return Result.Error(DatabaseError.NullResult)
    }

    override suspend fun saveProfile(username: String, profileWrapper: ProfileWrapper)
    {
        lastFmDatabase.profileDao().insertProfile(profileWrapper.mapToDb())
    }

    override suspend fun saveArtists(username: String, period: String, artistsList: List<ArtistWrapper>)
    {
        artistsList.forEach { artistWrapper ->
            lastFmDatabase.artistDao().insertArtist(artistWrapper.mapToDb(username, period))
        }
    }

    override suspend fun saveAlbums(username: String, period: String, albumsList: List<AlbumWrapper>)
    {
        albumsList.forEach { albumWrapper ->
            lastFmDatabase.albumDao().insertAlbum(albumWrapper.mapToDb(username, period))
        }
    }

    override suspend fun saveTracks(username: String, period: String, tracksList: List<TrackWrapper>)
    {
        tracksList.forEach { trackWrapper ->
            lastFmDatabase.trackDao().insertTrack(trackWrapper.mapToDb(username, period))
        }
    }

    override suspend fun getUpdateTimeArtists(username: String, period: String, page: Int): Result<Int>
    {
        lastFmDatabase.artistUpdateDao().getArtistsUpdateTime(username, period, page).let {
            return Result.Success(it)
        }
    }

    override suspend fun getUpdateTimeAlbums(username: String, period: String, page: Int): Result<Int>
    {
        lastFmDatabase.albumUpdateDao().getAlbumsUpdateTime(username, period, page).let {
            return Result.Success(it)
        }
    }

    override suspend fun getUpdateTimeTracks(username: String, period: String, page: Int): Result<Int>
    {
        lastFmDatabase.trackUpdateDao().getTracksUpdateTime(username, period, page).let {
            return Result.Success(it)
        }
    }

    override suspend fun setUpdateTimeArtists(username: String, period: String, page: Int)
    {
        lastFmDatabase.artistUpdateDao().insertUpdateTimes(
            ArtistUpdateEntity(
                user = username,
                page = page,
                period = period,
                time = (System.currentTimeMillis() / 1000).toInt()
            )
        )
    }

    override suspend fun setUpdateTimeAlbums(username: String, period: String, page: Int)
    {
        lastFmDatabase.albumUpdateDao().insertUpdateTimes(
            AlbumUpdateEntity(
                user = username,
                page = page,
                period = period,
                time = (System.currentTimeMillis() / 1000).toInt()
            )
        )
    }

    override suspend fun setUpdateTimeTracks(username: String, period: String, page: Int)
    {
        lastFmDatabase.trackUpdateDao().insertUpdateTimes(
            TrackUpdateEntity(
                user = username,
                page = page,
                period = period,
                time = (System.currentTimeMillis() / 1000).toInt()
            )
        )
    }
}