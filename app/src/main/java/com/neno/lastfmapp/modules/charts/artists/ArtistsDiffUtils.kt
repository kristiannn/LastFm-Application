package com.neno.lastfmapp.modules.charts.artists

import androidx.recyclerview.widget.DiffUtil
import com.neno.lastfmapp.repository.models.ArtistWrapper

class ArtistsDiffUtils(
    private val oldArtistsList: List<ArtistWrapper?>,
    private val newArtistsList: List<ArtistWrapper?>
) : DiffUtil.Callback()
{
    override fun getOldListSize(): Int = oldArtistsList.size

    override fun getNewListSize(): Int = newArtistsList.size

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean
    {
        return oldArtistsList[oldItemPosition]?.artist == newArtistsList[newItemPosition]?.artist
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean
    {
        return oldArtistsList[oldItemPosition]?.playCount == newArtistsList[newItemPosition]?.playCount
    }

    //TODO - show if artist has gone up or down
    override fun getChangePayload(oldItemPosition: Int, newItemPosition: Int): Any?
    {
        return super.getChangePayload(oldItemPosition, newItemPosition)
    }
}