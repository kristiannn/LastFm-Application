package com.neno.lastfmapp

import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelStore
import androidx.lifecycle.ViewModelStoreOwner
import com.bluelinelabs.conductor.archlifecycle.LifecycleController

abstract class BaseController(bundle: Bundle? = null) : LifecycleController(bundle), ViewModelStoreOwner
{
    private val viewModelStore = ViewModelStore()

    init
    {
        retainViewMode = RetainViewMode.RETAIN_DETACH
    }

    override fun onAttach(view: View)
    {
        super.onAttach(view)

        val activity = (activity as MainActivity?)!!

        activity.setToolbarVisibility(toolbarVisible())
        activity.setToolbarTitle(toolbarTitle())
        activity.setPeriodSelectorVisibility(periodsVisible())
        activity.setCurrentNavUser(currentNavigationUser())
    }

    override fun onDestroy()
    {
        super.onDestroy()
        viewModelStore.clear()
    }

    override fun getViewModelStore(): ViewModelStore
    {
        return viewModelStore
    }

    protected abstract fun toolbarTitle(): String?

    protected abstract fun toolbarVisible(): Boolean

    protected abstract fun periodsVisible(): Boolean

    protected abstract fun currentNavigationUser(): String?
}