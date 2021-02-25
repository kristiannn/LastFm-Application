package com.neno.lastfmapp.modules.utils.fragments

import androidx.fragment.app.Fragment
import com.neno.lastfmapp.MainActivity

abstract class BaseFragment : Fragment()
{
    override fun onResume()
    {
        super.onResume()
        updateSettings()
    }

    protected abstract fun toolbarTitle(): String?

    protected abstract fun currentNavigationUser(): String?

    protected abstract fun toolbarVisible(): Boolean

    protected abstract fun periodsVisible(): Boolean

    protected abstract fun tabsVisible(): Boolean

    fun updateSettings()
    {
        val activity = activity as MainActivity
        activity.setToolbarVisibility(toolbarVisible())
        activity.setToolbarTitle(toolbarTitle())
        activity.setPeriodSelectorVisibility(periodsVisible())
        activity.setTabsVisibility(tabsVisible())
        activity.setCurrentNavUser(currentNavigationUser())
    }
}