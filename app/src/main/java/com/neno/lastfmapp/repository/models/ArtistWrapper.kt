package com.neno.lastfmapp.repository.models

import android.os.Parcelable
import com.neno.lastfmapp.database.entities.ArtistEntity
import kotlinx.android.parcel.Parcelize

@Parcelize
data class ArtistWrapper(
    val artist: String,
    val playCount: Int,
    val image: String
) : Parcelable

fun ArtistWrapper.mapToDb(username: String, period: String): ArtistEntity
{
    return ArtistEntity(
        user = username,
        period = period,
        artist = artist,
        playCount = playCount,
        image = image
    )
}