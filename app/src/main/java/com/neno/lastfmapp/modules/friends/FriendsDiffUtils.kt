package com.neno.lastfmapp.modules.friends

import androidx.recyclerview.widget.DiffUtil
import com.neno.lastfmapp.repository.models.FriendWrapper

class FriendsDiffUtils(
    private val oldFriendsList: List<FriendWrapper>,
    private val newFriendsList: List<FriendWrapper>
) : DiffUtil.Callback()
{
    override fun getOldListSize(): Int = oldFriendsList.size

    override fun getNewListSize(): Int = newFriendsList.size

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean
    {
        return oldFriendsList[oldItemPosition].username == newFriendsList[newItemPosition].username
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean
    {
        return oldFriendsList[oldItemPosition].totalScrobbles == newFriendsList[newItemPosition].totalScrobbles &&
                oldFriendsList[oldItemPosition].lastScrobble == newFriendsList[newItemPosition].lastScrobble &&
                oldFriendsList[oldItemPosition].lastScrobbleTime == newFriendsList[newItemPosition].lastScrobbleTime &&
                oldFriendsList[oldItemPosition].profilePicture == newFriendsList[newItemPosition].profilePicture
    }
}