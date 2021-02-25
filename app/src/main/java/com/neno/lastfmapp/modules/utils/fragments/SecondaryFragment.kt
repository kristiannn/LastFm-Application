package com.neno.lastfmapp.modules.utils.fragments

abstract class SecondaryFragment : BaseFragment()
{
    override fun toolbarVisible(): Boolean = true

    override fun periodsVisible(): Boolean = false

    override fun tabsVisible(): Boolean = false

    protected abstract override fun toolbarTitle(): String?

    protected abstract override fun currentNavigationUser(): String?
}