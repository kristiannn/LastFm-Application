package com.neno.lastfmapp.database.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    foreignKeys = [ForeignKey(
        entity = ProfileEntity::class,
        parentColumns = arrayOf("username"),
        childColumns = arrayOf("user")
    )]
)
data class UpdateTimesEntity(
    @PrimaryKey
    @ColumnInfo(collate = ColumnInfo.NOCASE)
    val user: String,
    val artists: Int = 0,
    val albums: Int = 0,
    val tracks: Int = 0
)