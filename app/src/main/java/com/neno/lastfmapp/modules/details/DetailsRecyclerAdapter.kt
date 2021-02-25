package com.neno.lastfmapp.modules.details

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.neno.lastfmapp.R
import com.neno.lastfmapp.loadImage

class DetailsRecyclerAdapter(
    private var labelValue: List<LabelValue> = listOf()
) : RecyclerView.Adapter<RecyclerView.ViewHolder>()
{
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

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder
    {
        return when (viewType)
        {
            HORIZONTAL_TYPE ->
            {
                val view =
                    LayoutInflater.from(parent.context).inflate(R.layout.label_value_horizontal_layout, parent, false)

                HorizontalVH(view)
            }
            VERTICAL_TYPE ->
            {
                val view =
                    LayoutInflater.from(parent.context).inflate(R.layout.label_value_vertical_layout, parent, false)

                VerticalVH(view)
            }
            else ->
            {
                val view = LayoutInflater.from(parent.context).inflate(R.layout.label_value_image_layout, parent, false)

                ImageVH(view)
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int)
    {
        when (holder)
        {
            is HorizontalVH ->
            {
                holder.labelTextView.text = labelValue[position].label
                holder.valueTextView.text = labelValue[position].value
            }
            is VerticalVH ->
            {
                holder.labelTextView.text = labelValue[position].label
                holder.valueTextView.text = labelValue[position].value
            }
            is ImageVH ->
            {
                holder.coverImageView.loadImage(labelValue[position].value)
            }
        }
    }

    override fun getItemCount(): Int = labelValue.size

    override fun getItemViewType(position: Int): Int
    {
        return if (labelValue[position].horizontal == true) HORIZONTAL_TYPE else if (labelValue[position].horizontal == false) VERTICAL_TYPE else IMAGE_TYPE
    }

    fun updateList(newList: List<LabelValue>)
    {
        labelValue = newList
        notifyDataSetChanged() //It's a static page, no point of DiffUtils here..
    }

    companion object
    {
        private const val HORIZONTAL_TYPE = 0
        private const val VERTICAL_TYPE = 1
        private const val IMAGE_TYPE = 2
    }
}

data class LabelValue(
    val label: String?,
    val value: String?,
    val horizontal: Boolean? = true
)