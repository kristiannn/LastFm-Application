package com.neno.lastfmapp.database.daos

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.neno.lastfmapp.database.entities.ProfileEntity

@Dao
interface ProfileDao
{
    @Query("SELECT * FROM ProfileEntity WHERE username = (:username) COLLATE NOCASE")
    fun getProfile(username: String): ProfileEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertProfile(profileEntity: ProfileEntity)
}