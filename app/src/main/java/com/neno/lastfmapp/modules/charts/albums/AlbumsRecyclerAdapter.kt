package com.neno.lastfmapp.modules.charts.albums

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.neno.lastfmapp.R
import com.neno.lastfmapp.loadRoundedImage
import com.neno.lastfmapp.repository.models.AlbumWrapper

class AlbumsRecyclerAdapter(
    private var albumsList: List<AlbumWrapper?>,
    private val onAlbumItemClicked: OnAlbumItemClicked
) : RecyclerView.Adapter<RecyclerView.ViewHolder>()
{
    private var isLoading = false

    inner class AlbumsRecyclerViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
    {
        val positionTextView: TextView = itemView.findViewById(R.id.tvPosition)
        val coverImageView: ImageView = itemView.findViewById(R.id.ivCover)
        val albumTextView: TextView = itemView.findViewById(R.id.tvTrackAlbum)
        val artistTextView: TextView = itemView.findViewById(R.id.tvArtist)
        val scrobblesTextView: TextView = itemView.findViewById(R.id.tvDetails)
    }

    inner class ProgressBarRecyclerViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder
    {
        return if (viewType == ALBUM_ITEM_TYPE)
        {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.rv_albums_tracks, parent, false)

            AlbumsRecyclerViewHolder(view)
        } else
        {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.rv_progress_bar, parent, false)

            ProgressBarRecyclerViewHolder(view)
        }

    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int)
    {
        if (holder is AlbumsRecyclerViewHolder)
        {
            holder.positionTextView.text = (position + 1).toString()
            holder.artistTextView.text = albumsList[position]!!.artist
            holder.albumTextView.text = albumsList[position]!!.album
            holder.scrobblesTextView.text = albumsList[position]!!.playCount.toString()
            holder.coverImageView.loadRoundedImage(albumsList[position]!!.image)

            holder.itemView.setOnClickListener {
                onAlbumItemClicked.invoke(
                    albumsList[position]!!.artist, albumsList[position]!!.album
                )
            }
        }
    }

    override fun getItemCount(): Int = albumsList.size

    override fun getItemViewType(position: Int): Int
    {
        return if (albumsList[position] == null) PROGRESS_ITEM_TYPE else ALBUM_ITEM_TYPE
    }

    fun updateList(newAlbumsList: List<AlbumWrapper>)
    {
        isLoading = false

        val diffResult = DiffUtil.calculateDiff(AlbumsDiffUtils(albumsList, newAlbumsList))
        albumsList = newAlbumsList
        diffResult.dispatchUpdatesTo(this)
    }

    fun addLoadingItem()
    {
        if (!isLoading)
        {
            isLoading = true

            val newAlbumsList = albumsList + null
            albumsList = newAlbumsList

            this.notifyItemInserted(newAlbumsList.size - 1)
        }
    }

    fun removeLoadingItem()
    {
        if (isLoading && albumsList.last() == null)
        {
            isLoading = false

            val newAlbumsList = albumsList.filterNotNull()

            val diffResult = DiffUtil.calculateDiff(AlbumsDiffUtils(albumsList, newAlbumsList))
            albumsList = newAlbumsList
            diffResult.dispatchUpdatesTo(this)
        }
    }

    companion object
    {
        private const val ALBUM_ITEM_TYPE = 0
        private const val PROGRESS_ITEM_TYPE = 1
    }
}

typealias OnAlbumItemClicked = (artist: String, album: String) -> Unit