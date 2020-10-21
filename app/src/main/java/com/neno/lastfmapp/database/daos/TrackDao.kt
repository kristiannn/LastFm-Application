package com.neno.lastfmapp.database.daos

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.neno.lastfmapp.database.entities.TrackEntity

@Dao
interface TrackDao
{
    @Query("SELECT * FROM TrackEntity WHERE user = (:username) AND period = (:period) ORDER BY playCount DESC LIMIT 50 OFFSET (:offset)")
    fun getTracks(username: String, period: String, offset: Int): List<TrackEntity>?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertTrack(trackEntity: TrackEntity)
}