package com.neno.lastfmapp.modules.charts.tracks

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.neno.lastfmapp.R
import com.neno.lastfmapp.loadRoundedImage
import com.neno.lastfmapp.repository.models.TrackWrapper

class TracksRecyclerAdapter(
    private var tracksList: List<TrackWrapper?>,
    val onTrackItemClicked: OnTrackItemClicked
) : RecyclerView.Adapter<RecyclerView.ViewHolder>()
{
    private var isLoading = false

    inner class TracksRecyclerViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
    {
        val positionTextView: TextView = itemView.findViewById(R.id.tvPosition)
        val coverImageView: ImageView = itemView.findViewById(R.id.ivCover)
        val artistTextView: TextView = itemView.findViewById(R.id.tvArtist)
        val trackTextView: TextView = itemView.findViewById(R.id.tvTrackAlbum)
        val scrobblesTextView: TextView = itemView.findViewById(R.id.tvDetails)
    }

    inner class ProgressBarRecyclerViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder
    {
        return if (viewType == TRACK_ITEM_TYPE)
        {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.albums_tracks_layout, parent, false)

            TracksRecyclerViewHolder(view)
        } else
        {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.progressbar_layout, parent, false)

            ProgressBarRecyclerViewHolder(view)
        }

    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int)
    {
        if (holder is TracksRecyclerViewHolder)
        {
            holder.positionTextView.text = (position + 1).toString()
            holder.artistTextView.text = tracksList[position]!!.artist
            holder.trackTextView.text = tracksList[position]!!.track
            holder.scrobblesTextView.text = tracksList[position]!!.playCount.toString()
            holder.coverImageView.loadRoundedImage(tracksList[position]!!.image)

            holder.itemView.setOnClickListener {
                onTrackItemClicked.invoke(
                    tracksList[position]!!.artist, tracksList[position]!!.track
                )
            }
        }
    }

    override fun getItemCount(): Int = tracksList.size

    override fun getItemViewType(position: Int): Int
    {
        return if (tracksList[position] == null) PROGRESS_ITEM_TYPE else TRACK_ITEM_TYPE
    }

    fun updateList(newTracksList: List<TrackWrapper>)
    {
        isLoading = false

        val diffResult = DiffUtil.calculateDiff(TracksDiffUtils(tracksList, newTracksList))
        tracksList = newTracksList
        diffResult.dispatchUpdatesTo(this)
    }

    fun addLoadingItem()
    {
        if (!isLoading)
        {
            isLoading = true

            val newTracksList = tracksList + null
            tracksList = newTracksList

            this.notifyItemInserted(newTracksList.size - 1)
        }
    }

    fun removeLoadingItem()
    {
        if (isLoading && tracksList.last() == null)
        {
            isLoading = false

            val newTracksList = tracksList.filterNotNull()

            val diffResult = DiffUtil.calculateDiff(TracksDiffUtils(tracksList, newTracksList))
            tracksList = newTracksList
            diffResult.dispatchUpdatesTo(this)
        }
    }

    companion object
    {
        private const val TRACK_ITEM_TYPE = 0
        private const val PROGRESS_ITEM_TYPE = 1
    }
}

typealias OnTrackItemClicked = (artist: String, track: String) -> Unit