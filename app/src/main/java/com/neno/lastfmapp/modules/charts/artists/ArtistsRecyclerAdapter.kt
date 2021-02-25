package com.neno.lastfmapp.modules.charts.artists

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.neno.lastfmapp.R
import com.neno.lastfmapp.loadRoundedImage
import com.neno.lastfmapp.repository.models.ArtistWrapper

class ArtistsRecyclerAdapter(
    private var artistsList: List<ArtistWrapper?>,
    private val onArtistItemClicked: OnArtistItemClicked
) : RecyclerView.Adapter<RecyclerView.ViewHolder>()
{
    private var isLoading = false

    inner class ArtistsRecyclerViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
    {
        val positionTextView: TextView = itemView.findViewById(R.id.tvPosition)
        val coverImageView: ImageView = itemView.findViewById(R.id.ivCover)
        val artistTextView: TextView = itemView.findViewById(R.id.tvArtist)
        val scrobblesTextView: TextView = itemView.findViewById(R.id.tvDetails)
    }

    inner class ProgressBarRecyclerViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder
    {
        return if (viewType == ARTIST_ITEM_TYPE)
        {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.artists_layout, parent, false)

            ArtistsRecyclerViewHolder(view)
        } else
        {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.progressbar_layout, parent, false)

            ProgressBarRecyclerViewHolder(view)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int)
    {
        if (holder is ArtistsRecyclerViewHolder)
        {
            holder.positionTextView.text = (position + 1).toString()
            holder.artistTextView.text = artistsList[position]!!.artist
            holder.scrobblesTextView.text = artistsList[position]!!.playCount.toString()
            holder.coverImageView.loadRoundedImage(artistsList[position]!!.image)

            holder.itemView.setOnClickListener { onArtistItemClicked.invoke(artistsList[position]!!.artist) }
        }
    }

    override fun getItemCount(): Int = artistsList.size

    override fun getItemViewType(position: Int): Int
    {
        return if (artistsList[position] == null) PROGRESS_ITEM_TYPE else ARTIST_ITEM_TYPE
    }

    fun updateList(newArtistsList: List<ArtistWrapper>)
    {
        isLoading = false

        val diffResult = DiffUtil.calculateDiff(ArtistsDiffUtils(artistsList, newArtistsList))
        artistsList = newArtistsList
        diffResult.dispatchUpdatesTo(this)
    }

    fun addLoadingItem()
    {
        if (!isLoading)
        {
            isLoading = true

            val newArtistsList = artistsList + null
            artistsList = newArtistsList

            this.notifyItemInserted(newArtistsList.size - 1)
        }
    }

    fun removeLoadingItem()
    {
        if (isLoading && artistsList.last() == null)
        {
            isLoading = false

            val newArtistsList = artistsList.filterNotNull()

            val diffResult = DiffUtil.calculateDiff(ArtistsDiffUtils(artistsList, newArtistsList))
            artistsList = newArtistsList
            diffResult.dispatchUpdatesTo(this)
        }
    }

    companion object
    {
        private const val ARTIST_ITEM_TYPE = 0
        private const val PROGRESS_ITEM_TYPE = 1
    }
}

typealias OnArtistItemClicked = (artist: String) -> Unit