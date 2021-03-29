package com.neno.lastfmapp.modules.charts

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.Toolbar
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.neno.lastfmapp.MainActivity
import com.neno.lastfmapp.R
import com.neno.lastfmapp.modules.utils.BundleStrings
import com.neno.lastfmapp.modules.utils.fragments.ListsFragment

class ChartsFragment : ListsFragment()
{
    private lateinit var tabLayout: TabLayout
    private lateinit var viewPager: ViewPager2
    private lateinit var chartsAdapter: ChartsAdapter

    private val username by lazy { arguments?.getString(BundleStrings.USERNAME_KEY) }
    private val period by lazy { arguments?.getString(BundleStrings.PERIOD_KEY) }

    override fun toolbarTitle(): String? = username

    override fun currentNavigationUser(): String? = username

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View?
    {
        return inflater.inflate(R.layout.viewpager_layout, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?)
    {
        super.onViewCreated(view, savedInstanceState)

        viewPager = view.findViewById(R.id.viewPager)
        tabLayout = (activity as MainActivity).tabsLayout

        chartsAdapter = ChartsAdapter(this, username!!, period!!)
        viewPager.adapter = chartsAdapter
        viewPager.offscreenPageLimit = 2

        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            val text = when (position)
            {
                0 -> resources.getString(R.string.artists)
                1 -> resources.getString(R.string.albums)
                else -> resources.getString(R.string.tracks)
            }

            tab.text = text
        }.attach()
    }
}