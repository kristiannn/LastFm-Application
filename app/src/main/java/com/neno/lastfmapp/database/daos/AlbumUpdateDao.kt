package com.neno.lastfmapp.database.daos

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.neno.lastfmapp.database.entities.AlbumUpdateEntity

@Dao
interface AlbumUpdateDao
{
    @Query("SELECT time FROM AlbumUpdateEntity WHERE user = (:username) AND period=(:period) AND page=(:page)")
    fun getAlbumsUpdateTime(username: String, period: String, page: Int): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertUpdateTimes(albumUpdateEntity: AlbumUpdateEntity)
}