package com.neno.lastfmapp.network

import com.neno.lastfmapp.Result
import com.neno.lastfmapp.network.dto.mapToRepository
import com.neno.lastfmapp.network.utils.HttpResultConverter
import com.neno.lastfmapp.network.utils.Keys.API_KEY
import com.neno.lastfmapp.network.utils.RestError
import com.neno.lastfmapp.repository.models.*
import com.neno.lastfmapp.toQuery
import retrofit2.Response
import java.io.IOException

@Suppress("BlockingMethodInNonBlockingContext")
class LastFmDataFetcher(
    private val service: LastFmService,
    private val lastFmResultConverter: HttpResultConverter
) : LastFmServiceOperations
{
    override suspend fun getArtists(
        username: String,
        period: String,
        limit: Int,
        page: Int
    ): Result<List<ArtistWrapper>> = executeRequest(
        { service.getArtists(username, period, limit, page, API_KEY).execute() },
        { artists -> artists.artistScope.artistsList.map { it.mapToRepository() } }
    )

    override suspend fun getAlbums(
        username: String,
        period: String,
        limit: Int,
        page: Int
    ): Result<List<AlbumWrapper>> = executeRequest(
        { service.getAlbums(username, period, limit, page, API_KEY).execute() },
        { albums -> albums.albumScope.albumsList.map { it.mapToRepository() } }
    )

    override suspend fun getTracks(
        username: String,
        period: String,
        limit: Int,
        page: Int
    ): Result<List<TrackWrapper>> = executeRequest(
        { service.getTracks(username, period, limit, page, API_KEY).execute() },
        { tracks -> tracks.trackScope.tracksList.map { it.mapToRepository() } }
    )

    override suspend fun getRecentTracks(username: String, limit: Int, page: Int): Result<List<RecentTrackWrapper>> =
        executeRequest(
            { service.getRecentTracks(username, page, limit, API_KEY).execute() },
            { recentTracks -> recentTracks.mapToRepository() }
        )

    override suspend fun getProfile(username: String): Result<ProfileWrapper> = executeRequest(
        { service.getProfile(username, API_KEY).execute() },
        { it.profile.mapToRepository() }
    )

    override suspend fun getFriends(username: String): Result<List<ProfileWrapper>> = executeRequest(
        { service.getFriends(username, API_KEY).execute() },
        { friends -> friends.friends.friendsList.map { it.mapToRepository() } }
    )

    override suspend fun getTrackDetails(artist: String, track: String): Result<TrackDetailsWrapper> = executeRequest(
        { service.getTrackDetails(artist.toQuery(), track.toQuery(), API_KEY).execute() },
        { it.trackDetailsDto.mapToRepository() }
    )

    override suspend fun getAlbumDetails(artist: String, album: String): Result<AlbumDetailsWrapper> = executeRequest(
        { service.getAlbumDetails(artist.toQuery(), album.toQuery(), API_KEY).execute() },
        { it.albumDetailsDto.mapToRepository() }
    )

    override suspend fun getArtistDetails(artist: String): Result<ArtistDetailsWrapper> = executeRequest(
        { service.getArtistDetails(artist.toQuery(), API_KEY).execute() },
        { it.artistDetailsDto.mapToRepository() }
    )

    private suspend inline fun <Dto, Wrapper> executeRequest(
        crossinline serviceRequest: suspend () -> Response<Dto>,
        crossinline mapToRepository: (Dto) -> Wrapper
    ): Result<Wrapper>
    {
        try
        {
            val response = serviceRequest()
            if (!response.isSuccessful)
            {
                return Result.Error(lastFmResultConverter.httpError(response.errorBody()))
            }

            return response.body()?.let { Result.Success(mapToRepository(it)) }
                ?: Result.Error(RestError.NullResult)

        } catch (e: IOException)
        {
            return Result.Error(RestError.NetworkError)
        }
    }
}