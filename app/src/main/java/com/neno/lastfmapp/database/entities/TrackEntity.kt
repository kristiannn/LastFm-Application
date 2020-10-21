package com.neno.lastfmapp.database.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import com.neno.lastfmapp.repository.models.TrackWrapper

@Entity(
    foreignKeys = [ForeignKey(
        entity = ProfileEntity::class,
        parentColumns = arrayOf("username"),
        childColumns = arrayOf("user")
    )],
    primaryKeys = ["user", "track", "artist", "period"]
)
data class TrackEntity(
    @ColumnInfo(collate = ColumnInfo.NOCASE)
    val user: String,
    val period: String,
    val track: String,
    val artist: String,
    val playCount: Int,
    val image: String
)

fun TrackEntity.mapToRepository(): TrackWrapper
{
    return TrackWrapper(
        track = track,
        artist = artist,
        playCount = playCount,
        image = image
    )
}