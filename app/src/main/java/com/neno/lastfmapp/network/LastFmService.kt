package com.neno.lastfmapp.network

import com.neno.lastfmapp.network.dto.*
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface LastFmService
{
    @GET("?method=user.getTopArtists&format=json")
    fun getArtists(
        @Query("user") username: String,
        @Query("period") period: String,
        @Query("limit") limit: Int,
        @Query("page") page: Int,
        @Query("api_key") apiKey: String
    ): Call<TopArtistsBaseScope>

    @GET("?method=user.getTopAlbums&format=json")
    fun getAlbums(
        @Query("user") username: String,
        @Query("period") period: String,
        @Query("limit") limit: Int,
        @Query("page") page: Int,
        @Query("api_key") apiKey: String
    ): Call<TopAlbumsBaseScope>

    @GET("?method=user.getTopTracks&format=json")
    fun getTracks(
        @Query("user") username: String,
        @Query("period") period: String,
        @Query("limit") limit: Int,
        @Query("page") page: Int,
        @Query("api_key") apiKey: String
    ): Call<TopTracksBaseScope>

    @GET("?method=user.getRecentTracks&format=json")
    fun getRecentTracks(
        @Query("user") username: String,
        @Query("page") page: Int,
        @Query("limit") limit: Int,
        @Query("api_key") apiKey: String
    ): Call<RecentTracksBaseScope>

    @GET("?method=user.getInfo&format=json")
    fun getProfile(
        @Query("user") username: String,
        @Query("api_key") apiKey: String
    ): Call<ProfileBaseScope>

    @GET("?method=user.getFriends&format=json")
    fun getFriends(
        @Query("user") username: String,
        @Query("api_key") apiKey: String
    ): Call<FriendsBaseScope>

    @GET("?method=track.getInfo&format=json")
    fun getTrackDetails(
        @Query(value = "artist", encoded = true) artist: String,
        @Query(value = "track", encoded = true) track: String,
        @Query("api_key") apiKey: String
    ): Call<TrackDetailsBaseScope>

    @GET("?method=album.getInfo&format=json")
    fun getAlbumDetails(
        @Query(value = "artist", encoded = true) artist: String,
        @Query(value = "album", encoded = true) album: String,
        @Query("api_key") apiKey: String
    ): Call<AlbumDetailsBaseScope>

    @GET("?method=artist.getInfo&format=json")
    fun getArtistDetails(
        @Query(value = "artist", encoded = true) artist: String,
        @Query("api_key") apiKey: String
    ): Call<ArtistDetailsBaseScope>
}