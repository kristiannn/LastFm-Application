package com.neno.lastfmapp.modules.recents

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.neno.lastfmapp.R
import com.neno.lastfmapp.dp
import com.neno.lastfmapp.loadRoundedImage
import com.neno.lastfmapp.modules.charts.tracks.OnTrackItemClicked
import com.neno.lastfmapp.modules.utils.TimeCalculator
import com.neno.lastfmapp.repository.models.RecentTrackWrapper
import org.koin.core.context.GlobalContext.get

class RecentsRecyclerAdapter(
    private var tracksList: List<RecentTrackWrapper?>,
    val onTrackItemClicked: OnTrackItemClicked,
    val onSelectionChange: OnSelectionChange
) : RecyclerView.Adapter<RecyclerView.ViewHolder>()
{
    private val timeCalculator: TimeCalculator by get().inject()

    private var isLoading = false
    private var selectionMode = false
    private var selectedTracks: MutableList<RecentTrackWrapper> = mutableListOf()

    inner class TracksRecyclerViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
    {
        val positionTextView: TextView = itemView.findViewById(R.id.tvPosition)
        val coverImageView: ImageView = itemView.findViewById(R.id.ivCover)
        val artistTextView: TextView = itemView.findViewById(R.id.tvArtist)
        val trackTextView: TextView = itemView.findViewById(R.id.tvTrackAlbum)
        val dateTextView: TextView = itemView.findViewById(R.id.tvDetails)
    }

    inner class ProgressBarRecyclerViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder
    {
        return if (viewType == TRACK_ITEM_TYPE)
        {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.rv_albums_tracks, parent, false)

            TracksRecyclerViewHolder(view)
        } else
        {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.rv_progress_bar, parent, false)

            ProgressBarRecyclerViewHolder(view)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int)
    {
        if (holder is TracksRecyclerViewHolder)
        {
            if (isItemSelected(position)) holder.itemView.setBackgroundResource(R.drawable.recycler_item_selected)
            else holder.itemView.setBackgroundResource(R.drawable.recycler_item)

            holder.positionTextView.visibility = View.GONE
            holder.dateTextView.setPaddingRelative(0, 0, 8.dp, 0)

            holder.artistTextView.text = tracksList[position]!!.artist
            holder.trackTextView.text = tracksList[position]!!.track
            holder.dateTextView.text =
                if (tracksList[position]!!.date != null) timeCalculator.convertToTime(tracksList[position]!!.date!!)
                else holder.itemView.resources.getString(R.string.now_playing)

            holder.coverImageView.loadRoundedImage(tracksList[position]!!.image)

            holder.itemView.setOnClickListener {
                if (selectionMode)
                {
                    if (isItemSelected(position))
                    {
                        selectedTracks.remove(tracksList[position])
                        if (selectedTracks.size == 0)
                        {
                            selectionMode = false
                        }
                    } else
                    {
                        tracksList[position]?.let { selectedTracks.add(it) }
                    }


                    onSelectionChange.invoke(selectionMode, selectedItemsCount())
                } else
                {
                    onTrackItemClicked.invoke(
                        tracksList[position]!!.artist, tracksList[position]!!.track
                    )
                }

                notifyItemChanged(position)
            }

            holder.itemView.setOnLongClickListener {

                if (isItemSelected(position))
                {
                    selectedTracks.remove(tracksList[position])
                    if (selectedTracks.size == 0)
                    {
                        selectionMode = false
                    }
                } else
                {
                    tracksList[position]?.let {
                        selectedTracks.add(it)

                        if (!selectionMode)
                        {
                            selectionMode = true
                        }
                    }
                }

                onSelectionChange.invoke(selectionMode, selectedItemsCount())
                notifyItemChanged(position)
                true
            }
        }
    }

    override fun getItemCount(): Int = tracksList.size

    override fun getItemViewType(position: Int): Int
    {
        return if (tracksList[position] == null) PROGRESS_ITEM_TYPE else TRACK_ITEM_TYPE
    }

    private fun isItemSelected(position: Int): Boolean = selectedTracks.contains(tracksList[position])

    private fun selectedItemsCount(): Int = selectedTracks.size

    fun getSelectedItems(): List<RecentTrackWrapper> = selectedTracks

    fun updateList(newTracksList: List<RecentTrackWrapper>)
    {
        isLoading = false

        val diffResult = DiffUtil.calculateDiff(RecentsDiffUtils(tracksList, newTracksList))
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

            val diffResult = DiffUtil.calculateDiff(RecentsDiffUtils(tracksList, newTracksList))
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

typealias OnSelectionChange = (selectionMode: Boolean, selectedCount: Int) -> Unit