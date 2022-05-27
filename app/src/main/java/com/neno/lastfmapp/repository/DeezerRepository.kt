package com.neno.lastfmapp.repository

import com.neno.lastfmapp.Result
import com.neno.lastfmapp.repository.models.ArtistWrapper
import com.neno.lastfmapp.repository.models.TrackWrapper

interface DeezerRepository
{
    suspend fun getArtistPicture(username: String, period: String, artistWrapper: ArtistWrapper): Result<String>

    suspend fun getTrackPicture(username: String, period: String, trackWrapper: TrackWrapper): Result<String>
}