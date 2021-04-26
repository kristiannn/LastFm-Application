package com.neno.lastfmapp.modules.details

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.neno.lastfmapp.R
import com.neno.lastfmapp.modules.dialog.NotifyDialog
import com.neno.lastfmapp.modules.utils.BundleStrings
import com.neno.lastfmapp.modules.utils.fragments.SecondaryFragment
import com.neno.lastfmapp.setGone
import com.neno.lastfmapp.setVisible
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class DetailsFragment : SecondaryFragment()
{
    private lateinit var recyclerView: RecyclerView
    private lateinit var progressBar: ProgressBar

    private val recyclerAdapter: DetailsRecyclerAdapter by lazy { DetailsRecyclerAdapter() }

    private val username by lazy { arguments?.getString(BundleStrings.USERNAME_KEY) }
    private val artist by lazy { arguments?.getString(BundleStrings.ARTIST_KEY) }
    private val album by lazy { arguments?.getString(BundleStrings.ALBUM_KEY) }
    private val track by lazy { arguments?.getString(BundleStrings.TRACK_KEY) }

    private val viewModel: DetailsViewModel by viewModel { parametersOf(artist, album, track) }

    override fun toolbarTitle(): String? = if (track != null) track else if (album != null) album else artist

    override fun currentNavigationUser(): String? = username

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View?
    {
        return inflater.inflate(R.layout.details_list_layout, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?)
    {
        super.onActivityCreated(savedInstanceState)

        setObservers()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?)
    {
        super.onViewCreated(view, savedInstanceState)

        setupViews(view)
    }

    private fun setupViews(view: View)
    {
        progressBar = view.findViewById(R.id.progressBar)!!
        recyclerView = view.findViewById(R.id.recyclerView)!!

        recyclerView.layoutManager = LinearLayoutManager(activity)
        recyclerView.adapter = recyclerAdapter
        recyclerView.setHasFixedSize(true)
    }

    private fun setObservers()
    {
        viewModel.detailsState.observe(viewLifecycleOwner, {
            recyclerAdapter.updateList(it)
        })

        viewModel.screenState.observe(viewLifecycleOwner, {
            when
            {
                it.isLoading -> progressBar.setVisible()
                it.errorMessage != null ->
                {
                    progressBar.setGone()
                    NotifyDialog(it.errorMessage).show((activity as AppCompatActivity).supportFragmentManager, "Error!")
                }
                else -> progressBar.setGone()
            }
        })
    }
}