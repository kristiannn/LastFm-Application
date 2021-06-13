package com.neno.lastfmapp.modules.details

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.neno.lastfmapp.R
import com.neno.lastfmapp.loadImage
import com.neno.lastfmapp.repository.models.AlbumTrackWrapper
import com.neno.lastfmapp.repository.models.ArtistWrapper

class DetailsRecyclerAdapter(
    private var detailsList: List<DetailsItem> = listOf()
) : RecyclerView.Adapter<RecyclerView.ViewHolder>()
{
    private val viewPool = RecyclerView.RecycledViewPool()

    inner class HorizontalVH(itemView: View) : RecyclerView.ViewHolder(itemView)
    {
        val labelTextView: TextView = itemView.findViewById(R.id.tvLabel)
        val valueTextView: TextView = itemView.findViewById(R.id.tvValue)
    }

    inner class VerticalVH(itemView: View) : RecyclerView.ViewHolder(itemView)
    {
        val labelTextView: TextView = itemView.findViewById(R.id.tvLabel)
        val valueTextView: TextView = itemView.findViewById(R.id.tvValue)
    }

    inner class ImageVH(itemView: View) : RecyclerView.ViewHolder(itemView)
    {
        val coverImageView: ImageView = itemView.findViewById(R.id.ivCover)
    }

    inner class TagsVH(itemView: View) : RecyclerView.ViewHolder(itemView)
    {
        val tagsRecyclerView: RecyclerView = itemView.findViewById(R.id.recyclerView)
    }

    inner class SimilarArtistsVH(itemView: View) : RecyclerView.ViewHolder(itemView)
    {
        val titleTextView: TextView = itemView.findViewById(R.id.tvTitle)
        val similarArtistsRecyclerView: RecyclerView = itemView.findViewById(R.id.recyclerView)
    }

    inner class AlbumTracksVH(itemView: View) : RecyclerView.ViewHolder(itemView)
    {
        val titleTextView: TextView = itemView.findViewById(R.id.tvTitle)
        val similarArtistsRecyclerView: RecyclerView = itemView.findViewById(R.id.recyclerView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder
    {
        return when (viewType)
        {
            HORIZONTAL_TYPE -> HorizontalVH(
                LayoutInflater.from(parent.context).inflate(R.layout.rv_label_value_horizontal, parent, false)
            )
            VERTICAL_TYPE -> VerticalVH(
                LayoutInflater.from(parent.context).inflate(R.layout.rv_label_value_vertical, parent, false)
            )
            IMAGE_TYPE -> ImageVH(LayoutInflater.from(parent.context).inflate(R.layout.rv_image, parent, false))
            TAGS_TYPE -> TagsVH(
                LayoutInflater.from(parent.context).inflate(R.layout.recycler_view_layout, parent, false)
            )
            SIMILAR_ARTISTS_TYPE -> SimilarArtistsVH(
                LayoutInflater.from(parent.context).inflate(R.layout.recycler_view_titled_layout, parent, false)
            )
            ALBUM_TRACKS_TYPE -> AlbumTracksVH(
                LayoutInflater.from(parent.context).inflate(R.layout.recycler_view_titled_layout, parent, false)
            )
            else -> HorizontalVH(
                LayoutInflater.from(parent.context).inflate(R.layout.rv_label_value_horizontal, parent, false)
            )
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int)
    {
        when (holder)
        {
            is HorizontalVH ->
            {
                val item = detailsList[position] as DetailsItem.LabelValueHorizontal
                holder.labelTextView.text = holder.itemView.resources.getString(item.labelValue.first)
                holder.valueTextView.text = item.labelValue.second
            }
            is VerticalVH ->
            {
                val item = detailsList[position] as DetailsItem.LabelValueVertical
                holder.labelTextView.text = holder.itemView.resources.getString(item.labelValue.first)
                holder.valueTextView.text = item.labelValue.second
            }
            is ImageVH ->
            {
                val item = detailsList[position] as DetailsItem.CoverImage
                holder.coverImageView.loadImage(item.url)
            }
            is TagsVH ->
            {
                val item = detailsList[position] as DetailsItem.Tags
                val childLayoutManager =
                    LinearLayoutManager(holder.tagsRecyclerView.context, RecyclerView.HORIZONTAL, false)
                childLayoutManager.initialPrefetchItemCount = item.tagsList.size
                holder.tagsRecyclerView.apply {
                    layoutManager = childLayoutManager
                    adapter = TagsRecyclerAdapter(item.tagsList)
                    setRecycledViewPool(viewPool)
                }
            }
            is SimilarArtistsVH ->
            {
                val item = detailsList[position] as DetailsItem.SimilarArtists
                holder.titleTextView.text = holder.itemView.resources.getString(R.string.similar_artists)
                val childLayoutManager =
                    LinearLayoutManager(holder.similarArtistsRecyclerView.context, RecyclerView.HORIZONTAL, false)
                childLayoutManager.initialPrefetchItemCount = item.artistsList.size
                holder.similarArtistsRecyclerView.apply {
                    layoutManager = childLayoutManager
                    adapter = SimilarArtistsRecyclerAdapter(item.artistsList)
                    setRecycledViewPool(viewPool)
                }
            }
            is AlbumTracksVH ->
            {
                val item = detailsList[position] as DetailsItem.AlbumTracks

                if (item.tracksList.isNotEmpty())
                    holder.titleTextView.text = holder.itemView.resources.getString(R.string.album_tracks)

                val childLayoutManager =
                    LinearLayoutManager(holder.similarArtistsRecyclerView.context, RecyclerView.VERTICAL, false)
                childLayoutManager.initialPrefetchItemCount = item.tracksList.size
                holder.similarArtistsRecyclerView.apply {
                    layoutManager = childLayoutManager
                    adapter = AlbumTracksRecyclerAdapter(item.tracksList)
                    setRecycledViewPool(viewPool)
                }
            }
        }
    }

    override fun getItemCount(): Int = detailsList.size

    override fun getItemViewType(position: Int): Int
    {
        return when (detailsList[position])
        {
            is DetailsItem.LabelValueHorizontal -> HORIZONTAL_TYPE
            is DetailsItem.LabelValueVertical -> VERTICAL_TYPE
            is DetailsItem.CoverImage -> IMAGE_TYPE
            is DetailsItem.Tags -> TAGS_TYPE
            is DetailsItem.SimilarArtists -> SIMILAR_ARTISTS_TYPE
            is DetailsItem.AlbumTracks -> ALBUM_TRACKS_TYPE
            else -> HORIZONTAL_TYPE
        }
    }

    fun updateList(newList: List<DetailsItem>)
    {
        detailsList = newList
        notifyDataSetChanged() //It's a static page, no point of DiffUtils here..
    }

    companion object
    {
        private const val HORIZONTAL_TYPE = 0
        private const val VERTICAL_TYPE = 1
        private const val IMAGE_TYPE = 2
        private const val TAGS_TYPE = 3
        private const val SIMILAR_ARTISTS_TYPE = 4
        private const val ALBUM_TRACKS_TYPE = 5
    }

    inner class TagsRecyclerAdapter(
        private var tagsList: List<String>
    ) : RecyclerView.Adapter<TagsRecyclerAdapter.TagVH>()
    {
        inner class TagVH(itemView: View) : RecyclerView.ViewHolder(itemView)
        {
            val tagTextView: TextView = itemView.findViewById(R.id.tvTag)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TagVH
        {
            return TagVH(LayoutInflater.from(parent.context).inflate(R.layout.rv_details_tags, parent, false))
        }

        override fun onBindViewHolder(holder: TagVH, position: Int)
        {
            holder.tagTextView.text = tagsList[position]
        }

        override fun getItemCount(): Int = tagsList.size
    }

    inner class SimilarArtistsRecyclerAdapter(
        private var artistsList: List<ArtistWrapper>
    ) : RecyclerView.Adapter<SimilarArtistsRecyclerAdapter.ArtistVH>()
    {
        inner class ArtistVH(itemView: View) : RecyclerView.ViewHolder(itemView)
        {
            val coverImageView: ImageView = itemView.findViewById(R.id.ivCover)
            val nameTextView: TextView = itemView.findViewById(R.id.tvName)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ArtistVH
        {
            return ArtistVH(
                LayoutInflater.from(parent.context).inflate(R.layout.rv_similar_artist_track, parent, false)
            )
        }

        override fun onBindViewHolder(holder: ArtistVH, position: Int)
        {
            holder.coverImageView.loadImage(artistsList[position].image)
            holder.nameTextView.text = artistsList[position].artist
        }

        override fun getItemCount(): Int = artistsList.size
    }

    inner class AlbumTracksRecyclerAdapter(
        private var tracksList: List<AlbumTrackWrapper>
    ) : RecyclerView.Adapter<AlbumTracksRecyclerAdapter.TracksVH>()
    {
        inner class TracksVH(itemView: View) : RecyclerView.ViewHolder(itemView)
        {
            val positionTextView: TextView = itemView.findViewById(R.id.tvPosition)
            val nameTextView: TextView = itemView.findViewById(R.id.tvName)
            val durationTextView: TextView = itemView.findViewById(R.id.tvDuration)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TracksVH
        {
            return TracksVH(
                LayoutInflater.from(parent.context).inflate(R.layout.rv_album_track, parent, false)
            )
        }

        override fun onBindViewHolder(holder: TracksVH, position: Int)
        {
            holder.positionTextView.text = (position + 1).toString()
            holder.nameTextView.text = tracksList[position].name
            holder.durationTextView.text = tracksList[position].duration
        }

        override fun getItemCount(): Int = tracksList.size
    }
}