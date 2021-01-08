package com.neno.lastfmapp.database.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey

@Entity(
    foreignKeys = [ForeignKey(
        entity = ProfileEntity::class,
        parentColumns = arrayOf("username"),
        childColumns = arrayOf("user")
    )],
    primaryKeys = ["user", "period", "page"]
)
data class ArtistUpdateEntity(
    @ColumnInfo(collate = ColumnInfo.NOCASE)
    val user: String,
    val time: Int,
    val period: String,
    val page: Int
)