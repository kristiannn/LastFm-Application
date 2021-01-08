package com.neno.lastfmapp.database.daos

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.neno.lastfmapp.database.entities.TrackUpdateEntity

@Dao
interface TrackUpdateDao
{
    @Query("SELECT time FROM TrackUpdateEntity WHERE user = (:username) AND period=(:period) AND page=(:page)")
    fun getTracksUpdateTime(username: String, period: String, page: Int): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertUpdateTimes(trackUpdateEntity: TrackUpdateEntity)
}