package com.neno.lastfmapp.database.daos

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.neno.lastfmapp.database.entities.ArtistEntity

@Dao
interface ArtistDao
{
    @Query("SELECT * FROM ArtistEntity WHERE user = (:username) AND period = (:period) ORDER BY playCount DESC LIMIT 50 OFFSET (:offset)")
    fun getArtists(username: String, period: String, offset: Int): List<ArtistEntity>?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertArtist(artistEntity: ArtistEntity)
}