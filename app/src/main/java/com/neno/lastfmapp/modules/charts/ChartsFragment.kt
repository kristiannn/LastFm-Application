package com.neno.lastfmapp.modules.charts

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.tabs.TabLayoutMediator
import com.neno.lastfmapp.MainActivity
import com.neno.lastfmapp.R
import com.neno.lastfmapp.databinding.ViewpagerLayoutBinding
import com.neno.lastfmapp.modules.utils.BundleStrings
import com.neno.lastfmapp.modules.utils.fragments.ListsFragment

class ChartsFragment : ListsFragment()
{
    private lateinit var binding: ViewpagerLayoutBinding
    private lateinit var chartsAdapter: ChartsAdapter

    private val username by lazy { arguments?.getString(BundleStrings.USERNAME_KEY) }
    private val period by lazy { arguments?.getString(BundleStrings.PERIOD_KEY) }
    private val realName by lazy { arguments?.getString(BundleStrings.REALNAME_KEY) }

    override fun toolbarTitle(): String? = if (realName.isNullOrEmpty()) username else realName

    override fun currentNavigationUser(): String? = username

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View
    {
        binding = ViewpagerLayoutBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?)
    {
        super.onViewCreated(view, savedInstanceState)

        val tabLayout = (activity as MainActivity).tabsLayout

        chartsAdapter = ChartsAdapter(this, username!!, period!!)
        binding.viewPager.apply {
            adapter = chartsAdapter
            offscreenPageLimit = 2
        }

        TabLayoutMediator(tabLayout, binding.viewPager) { tab, position ->
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