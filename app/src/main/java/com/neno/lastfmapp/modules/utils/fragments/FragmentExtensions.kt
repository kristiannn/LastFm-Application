package com.neno.lastfmapp.modules.utils.fragments

import androidx.annotation.IdRes
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction

fun FragmentManager.clearBackStack()
{
    for (i in 0 until this.backStackEntryCount)
    {
        this.popBackStack()
    }
}

fun FragmentManager.setRootFragment(@IdRes fragmentContainer: Int, fragment: Fragment)
{
    this.clearBackStack()

    this
        .beginTransaction()
        .setPrimaryNavigationFragment(fragment)
        .replace(fragmentContainer, fragment)
        .commit()
}

fun FragmentManager.addFragment(@IdRes fragmentContainer: Int, fragment: Fragment)
{
    this
        .beginTransaction()
        .setReorderingAllowed(true)
        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
        .hide(this.fragments.last())
        .add(fragmentContainer, fragment)
        .addToBackStack(null)
        .commit()
}

fun FragmentManager.replaceFragment(@IdRes fragmentContainer: Int, fragment: Fragment)
{
    this.popBackStack()

    this
        .beginTransaction()
        .setReorderingAllowed(true)
        .replace(fragmentContainer, fragment)
        .addToBackStack(null)
        .commit()
}