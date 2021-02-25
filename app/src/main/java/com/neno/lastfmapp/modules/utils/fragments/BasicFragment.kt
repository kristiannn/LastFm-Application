package com.neno.lastfmapp.modules.utils.fragments

abstract class BasicFragment : BaseFragment()
{
    override fun toolbarVisible(): Boolean = false

    override fun periodsVisible(): Boolean = false

    override fun tabsVisible(): Boolean = false

    override fun toolbarTitle(): String? = ""

    override fun currentNavigationUser(): String? = null
}