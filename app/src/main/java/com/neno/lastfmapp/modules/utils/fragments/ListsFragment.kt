package com.neno.lastfmapp.modules.utils.fragments

abstract class ListsFragment : BaseFragment()
{
    override fun toolbarVisible(): Boolean = true

    override fun periodsVisible(): Boolean = true

    override fun tabsVisible(): Boolean = true

    protected abstract override fun toolbarTitle(): String?

    protected abstract override fun currentNavigationUser(): String?
}