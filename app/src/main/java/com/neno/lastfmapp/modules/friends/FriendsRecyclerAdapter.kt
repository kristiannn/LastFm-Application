package com.neno.lastfmapp.modules.friends

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.neno.lastfmapp.R
import com.neno.lastfmapp.format
import com.neno.lastfmapp.loadRoundedImage
import com.neno.lastfmapp.repository.models.FriendWrapper

class FriendsRecyclerAdapter(
    private var friendsList: List<FriendWrapper>,
    val onFriendsItemClicked: OnFriendsItemClicked
) : RecyclerView.Adapter<FriendsRecyclerAdapter.FriendsRecyclerViewHolder>()
{
    class FriendsRecyclerViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
    {
        val profilePictureImageView: ImageView = itemView.findViewById(R.id.ivProfilePicture)
        val usernameTextView: TextView = itemView.findViewById(R.id.tvUsername)
        val listeningTextView: TextView = itemView.findViewById(R.id.tvListening)
        val scrobblesTextView: TextView = itemView.findViewById(R.id.tvDetails)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FriendsRecyclerViewHolder
    {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.friends_layout, parent, false)

        return FriendsRecyclerViewHolder(view)
    }

    override fun onBindViewHolder(holder: FriendsRecyclerViewHolder, position: Int)
    {
        val username =
            if (friendsList[position].realName.isNotEmpty()) friendsList[position].realName else friendsList[position].username

        holder.usernameTextView.text = username
        holder.listeningTextView.text = friendsList[position].lastScrobble
        holder.scrobblesTextView.text = friendsList[position].totalScrobbles.format()
        holder.profilePictureImageView.loadRoundedImage(friendsList[position].profilePicture)

        holder.itemView.setOnClickListener { onFriendsItemClicked.invoke(friendsList[position].username) }
    }

    override fun getItemCount(): Int = friendsList.size

    fun updateList(newFriendsList: List<FriendWrapper>)
    {
        val diffResult = DiffUtil.calculateDiff(FriendsDiffUtils(friendsList, newFriendsList))
        friendsList = newFriendsList
        diffResult.dispatchUpdatesTo(this)
    }
}

typealias OnFriendsItemClicked = (username: String) -> Unit