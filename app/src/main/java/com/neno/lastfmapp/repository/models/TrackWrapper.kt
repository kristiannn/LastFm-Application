package com.neno.lastfmapp.repository.models

import android.os.Parcelable
import com.neno.lastfmapp.database.entities.TrackEntity
import kotlinx.parcelize.Parcelize

@Parcelize
data class TrackWrapper(
    val track: String,
    val artist: String,
    val playCount: Int,
    val image: String
) : Parcelable

fun TrackWrapper.mapToDb(username: String, period: String): TrackEntity
{
    return TrackEntity(
        user = username,
        period = period,
        track = track,
        artist = artist,
        playCount = playCount,
        image = image
    )
}