package com.neno.lastfmapp.repository

import com.neno.lastfmapp.Result
import com.neno.lastfmapp.database.LastFmDatabaseOperations
import com.neno.lastfmapp.network.DeezerServiceOperations
import com.neno.lastfmapp.network.utils.RestError
import com.neno.lastfmapp.repository.models.ArtistWrapper
import com.neno.lastfmapp.repository.models.TrackWrapper
import com.neno.lastfmapp.repository.models.deezer.mapToRepository
import com.neno.lastfmapp.toQuery

class DeezerDataSource(
    private val deezerOperations: DeezerServiceOperations,
    private val lastFmDatabase: LastFmDatabaseOperations
) : DeezerRepository
{
    override suspend fun getArtistPicture(
        username: String,
        period: String,
        artistWrapper: ArtistWrapper
    ): Result<String>
    {
        val result = deezerOperations.search(artistWrapper.artist.toQuery())
        return if (result is Result.Success)
        {
            if (result.data.resultsList.isNullOrEmpty()) return Result.Error(RestError.NullResult)
            val artistPicture = result.data.resultsList.first()?.artist?.mapToRepository()?.picture
            artistPicture ?: return Result.Error(RestError.NullResult)
            val newArtistWrapper = ArtistWrapper(artistWrapper.artist, artistWrapper.playCount, artistPicture)
            lastFmDatabase.saveArtist(username, period, newArtistWrapper)
            Result.Success(artistPicture)
        } else
        {
            result as Result.Error
            Result.Error(result.error)
        }
    }

    override suspend fun getTrackPicture(
        username: String,
        period: String,
        trackWrapper: TrackWrapper
    ): Result<String>
    {
        val result = deezerOperations.search(trackWrapper.track.toQuery())
        return if (result is Result.Success)
        {
            if (result.data.resultsList.isNullOrEmpty()) return Result.Error(RestError.NullResult)
            val trackPicture =
                result.data.resultsList.find { it?.artist?.name == trackWrapper.artist }?.album?.mapToRepository()?.picture
                    ?: result.data.resultsList.first()?.album?.mapToRepository()?.picture
            trackPicture ?: return Result.Error(RestError.NullResult)
            val newTrackWrapper =
                TrackWrapper(trackWrapper.track, trackWrapper.artist, trackWrapper.playCount, trackPicture)

            lastFmDatabase.saveTrack(username, period, newTrackWrapper)
            Result.Success(trackPicture)
        } else
        {
            result as Result.Error
            Result.Error(result.error)
        }
    }
}