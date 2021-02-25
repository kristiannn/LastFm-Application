package com.neno.lastfmapp.modules.friends

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.neno.lastfmapp.R
import com.neno.lastfmapp.loadRoundedImage
import com.neno.lastfmapp.repository.models.ProfileWrapper

class FriendsRecyclerAdapter(
    private var friendsList: List<ProfileWrapper>,
    val onFriendsItemClicked: OnFriendsItemClicked
) : RecyclerView.Adapter<FriendsRecyclerAdapter.FriendsRecyclerViewHolder>()
{
    class FriendsRecyclerViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
    {
        val profilePictureImageView: ImageView = itemView.findViewById(R.id.ivProfilePicture)
        val usernameTextView: TextView = itemView.findViewById(R.id.tvUsername)
        val scrobblesTextView: TextView = itemView.findViewById(R.id.tvDetails)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FriendsRecyclerViewHolder
    {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.friends_layout, parent, false)

        return FriendsRecyclerViewHolder(view)
    }

    override fun onBindViewHolder(holder: FriendsRecyclerViewHolder, position: Int)
    {
        holder.usernameTextView.text = friendsList[position].username
        holder.scrobblesTextView.text = friendsList[position].totalScrobbles.toString()
        holder.profilePictureImageView.loadRoundedImage(friendsList[position].profilePicture)

        holder.itemView.setOnClickListener { onFriendsItemClicked.invoke(friendsList[position].username) }
    }

    override fun getItemCount(): Int = friendsList.size

    fun updateList(newFriendsList: List<ProfileWrapper>)
    {
        val diffResult = DiffUtil.calculateDiff(FriendsDiffUtils(friendsList, newFriendsList))
        friendsList = newFriendsList
        diffResult.dispatchUpdatesTo(this)
    }
}

typealias OnFriendsItemClicked = (username: String) -> Unit