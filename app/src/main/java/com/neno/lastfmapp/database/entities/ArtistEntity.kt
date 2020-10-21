package com.neno.lastfmapp.database.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import com.neno.lastfmapp.repository.models.ArtistWrapper

@Entity(
    foreignKeys = [ForeignKey(
        entity = ProfileEntity::class,
        parentColumns = arrayOf("username"),
        childColumns = arrayOf("user")
    )],
    primaryKeys = ["user", "artist", "period"]
)
data class ArtistEntity(
    @ColumnInfo(collate = ColumnInfo.NOCASE)
    val user: String,
    val period: String,
    val artist: String,
    val playCount: Int,
    val image: String
)

fun ArtistEntity.mapToRepository(): ArtistWrapper
{
    return ArtistWrapper(
        artist = artist,
        playCount = playCount,
        image = image
    )
}