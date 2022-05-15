package com.neno.lastfmapp.repository.models

import android.os.Parcelable
import com.neno.lastfmapp.database.entities.AlbumEntity
import kotlinx.parcelize.Parcelize

@Parcelize
data class AlbumWrapper(
    val album: String,
    val artist: String,
    val playCount: Int,
    val image: String
) : Parcelable

fun AlbumWrapper.mapToDb(username: String, period: String): AlbumEntity
{
    return AlbumEntity(
        user = username,
        period = period,
        album = album,
        artist = artist,
        playCount = playCount,
        image = image
    )
}