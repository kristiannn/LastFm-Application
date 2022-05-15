package com.neno.lastfmapp.repository.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class RecentTrackWrapper(
    val track: String,
    val artist: String,
    val album: String?,
    val date: Int?,
    val image: String,
    val totalScrobbles: Int
) : Parcelable