package com.neno.lastfmapp.modules.recents

import androidx.recyclerview.widget.DiffUtil
import com.neno.lastfmapp.repository.models.RecentTrackWrapper

class RecentsDiffUtils(
    private val oldTracksList: List<RecentTrackWrapper?>,
    private val newTracksList: List<RecentTrackWrapper?>
) : DiffUtil.Callback()
{
    override fun getOldListSize(): Int = oldTracksList.size

    override fun getNewListSize(): Int = newTracksList.size

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean
    {
        return oldTracksList[oldItemPosition]?.track == newTracksList[newItemPosition]?.track
                && oldTracksList[oldItemPosition]?.artist == newTracksList[newItemPosition]?.artist
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean
    {
        return oldTracksList[oldItemPosition]?.date == newTracksList[newItemPosition]?.date
    }
}