package com.neno.lastfmapp.database.daos

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.neno.lastfmapp.database.entities.ArtistUpdateEntity

@Dao
interface ArtistUpdateDao
{
    @Query("SELECT time FROM ArtistUpdateEntity WHERE user = (:username) AND period=(:period) AND page=(:page)")
    fun getArtistsUpdateTime(username: String, period: String, page: Int): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertUpdateTimes(artistUpdateEntity: ArtistUpdateEntity)
}