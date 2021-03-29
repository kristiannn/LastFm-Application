package com.neno.lastfmapp.modules.charts.albums

import androidx.recyclerview.widget.DiffUtil
import com.neno.lastfmapp.repository.models.AlbumWrapper

class AlbumsDiffUtils(
    private val oldAlbumsList: List<AlbumWrapper?>,
    private val newAlbumsList: List<AlbumWrapper?>
) : DiffUtil.Callback()
{
    override fun getOldListSize(): Int = oldAlbumsList.size

    override fun getNewListSize(): Int = newAlbumsList.size

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean
    {
        return oldAlbumsList[oldItemPosition]?.album == newAlbumsList[newItemPosition]?.album
                && oldAlbumsList[oldItemPosition]?.artist == newAlbumsList[newItemPosition]?.artist
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean
    {
        return oldAlbumsList[oldItemPosition]?.playCount == newAlbumsList[newItemPosition]?.playCount &&
                newItemPosition == oldItemPosition
    }
}