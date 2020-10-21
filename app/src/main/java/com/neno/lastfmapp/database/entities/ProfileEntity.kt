package com.neno.lastfmapp.database.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.neno.lastfmapp.repository.models.ProfileWrapper

@Entity
data class ProfileEntity(
    @PrimaryKey
    @ColumnInfo(collate = ColumnInfo.NOCASE)
    val username: String,
    val profilePicture: String,
    val totalScrobbles: Int
)

fun ProfileEntity.mapToRepository(): ProfileWrapper
{
    return ProfileWrapper(
        username = username,
        profilePicture = profilePicture,
        totalScrobbles = totalScrobbles
    )
}