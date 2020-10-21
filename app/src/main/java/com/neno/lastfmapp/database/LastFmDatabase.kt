package com.neno.lastfmapp.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.neno.lastfmapp.database.daos.*
import com.neno.lastfmapp.database.entities.*

@Database(
    entities = [
        ProfileEntity::class,
        ArtistEntity::class,
        AlbumEntity::class,
        TrackEntity::class,
        UpdateTimesEntity::class
    ],
    version = 1
)
abstract class LastFmDatabase : RoomDatabase()
{
    abstract fun profileDao(): ProfileDao
    abstract fun artistDao(): ArtistDao
    abstract fun albumDao(): AlbumDao
    abstract fun trackDao(): TrackDao
    abstract fun updateTimesDao(): UpdateTimesDao
}