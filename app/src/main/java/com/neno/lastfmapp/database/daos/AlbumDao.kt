package com.neno.lastfmapp.database.daos

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.neno.lastfmapp.database.entities.AlbumEntity

@Dao
interface AlbumDao
{
    @Query("SELECT * FROM AlbumEntity WHERE user = (:username) AND period = (:period) ORDER BY playCount DESC LIMIT 50 OFFSET (:offset)")
    fun getAlbums(username: String, period: String, offset: Int): List<AlbumEntity>?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAlbum(albumEntity: AlbumEntity)
}