package com.neno.lastfmapp.modules.charts

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.neno.lastfmapp.modules.charts.albums.AlbumsFragment
import com.neno.lastfmapp.modules.charts.artists.ArtistsFragment
import com.neno.lastfmapp.modules.charts.tracks.TracksFragment
import com.neno.lastfmapp.modules.utils.BundleStrings

class ChartsAdapter(
    fragment: Fragment,
    val username: String,
    val period: String
) : FragmentStateAdapter(fragment)
{
    override fun getItemCount(): Int = 3

    override fun createFragment(position: Int): Fragment
    {
        return when (position)
        {
            0 -> ArtistsFragment().also {
                it.arguments = Bundle().apply {
                    putString(BundleStrings.USERNAME_KEY, username)
                    putString(BundleStrings.PERIOD_KEY, period)
                }
            }

            1 -> AlbumsFragment().also {
                it.arguments = Bundle().apply {
                    putString(BundleStrings.USERNAME_KEY, username)
                    putString(BundleStrings.PERIOD_KEY, period)
                }
            }

            else -> TracksFragment().also {
                it.arguments = Bundle().apply {
                    putString(BundleStrings.USERNAME_KEY, username)
                    putString(BundleStrings.PERIOD_KEY, period)
                }
            }
        }
    }
}