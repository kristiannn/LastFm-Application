package com.neno.lastfmapp.database.daos

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.neno.lastfmapp.database.entities.UpdateTimesEntity

@Dao
interface UpdateTimesDao
{
    @Query("SELECT artists FROM UpdateTimesEntity WHERE user = (:username)")
    fun getUpdateTimeArtists(username: String): Int

    @Query("SELECT albums FROM UpdateTimesEntity WHERE user = (:username)")
    fun getUpdateTimeAlbums(username: String): Int

    @Query("SELECT tracks FROM UpdateTimesEntity WHERE user = (:username)")
    fun getUpdateTimeTracks(username: String): Int

    @Query("UPDATE UpdateTimesEntity SET artists=(:newUnixTimestamp) WHERE user = (:username)")
    fun setUpdateTimeArtists(username: String, newUnixTimestamp: Int)

    @Query("UPDATE UpdateTimesEntity SET albums=(:newUnixTimestamp) WHERE user = (:username)")
    fun setUpdateTimeAlbums(username: String, newUnixTimestamp: Int)

    @Query("UPDATE UpdateTimesEntity SET tracks=(:newUnixTimestamp) WHERE user = (:username)")
    fun setUpdateTimeTracks(username: String, newUnixTimestamp: Int)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertUpdateTimes(updateTimesEntity: UpdateTimesEntity)
}