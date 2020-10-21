package com.neno.lastfmapp.modules.tracks

import androidx.recyclerview.widget.DiffUtil
import com.neno.lastfmapp.repository.models.TrackWrapper

class TracksDiffUtils(
    private val oldTracksList: List<TrackWrapper?>,
    private val newTracksList: List<TrackWrapper?>
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
        return oldTracksList[oldItemPosition]?.playCount == newTracksList[newItemPosition]?.playCount
    }
}