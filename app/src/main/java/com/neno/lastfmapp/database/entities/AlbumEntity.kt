package com.neno.lastfmapp.database.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import com.neno.lastfmapp.repository.models.AlbumWrapper

@Entity(
    foreignKeys = [ForeignKey(
        entity = ProfileEntity::class,
        parentColumns = arrayOf("username"),
        childColumns = arrayOf("user")
    )],
    primaryKeys = ["user", "album", "artist", "period"]
)
data class AlbumEntity(
    @ColumnInfo(collate = ColumnInfo.NOCASE)
    val user: String,
    val period: String,
    val album: String,
    val artist: String,
    val playCount: Int,
    val image: String
)

fun AlbumEntity.mapToRepository(): AlbumWrapper
{
    return AlbumWrapper(
        album = album,
        artist = artist,
        playCount = playCount,
        image = image
    )
}