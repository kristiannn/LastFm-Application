package com.neno.lastfmapp.modules.details

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.neno.lastfmapp.databinding.DetailsListLayoutBinding
import com.neno.lastfmapp.modules.dialog.NotifyDialog
import com.neno.lastfmapp.modules.utils.BundleStrings
import com.neno.lastfmapp.modules.utils.fragments.SecondaryFragment
import com.neno.lastfmapp.setGone
import com.neno.lastfmapp.setVisible
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class DetailsFragment : SecondaryFragment()
{
    private lateinit var binding: DetailsListLayoutBinding
    private val recyclerAdapter: DetailsRecyclerAdapter by lazy { DetailsRecyclerAdapter() }

    private val username by lazy { arguments?.getString(BundleStrings.USERNAME_KEY) }
    private val artist by lazy { arguments?.getString(BundleStrings.ARTIST_KEY) }
    private val album by lazy { arguments?.getString(BundleStrings.ALBUM_KEY) }
    private val track by lazy { arguments?.getString(BundleStrings.TRACK_KEY) }

    private val viewModel: DetailsViewModel by viewModel { parametersOf(artist, album, track) }

    override fun toolbarTitle(): String? = if (track != null) track else if (album != null) album else artist

    override fun currentNavigationUser(): String? = username

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View
    {
        binding = DetailsListLayoutBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?)
    {
        super.onViewCreated(view, savedInstanceState)

        setObservers()
        setupViews()
    }

    private fun setupViews()
    {
        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(activity)
            adapter = recyclerAdapter
            setHasFixedSize(true)
        }
    }

    private fun setObservers()
    {
        viewModel.detailsState.observe(viewLifecycleOwner) {
            recyclerAdapter.updateList(it)
        }

        viewModel.screenState.observe(viewLifecycleOwner) {
            when
            {
                it.isLoading -> binding.progressBar.setVisible()
                it.errorMessage != null ->
                {
                    binding.progressBar.setGone()
                    NotifyDialog(it.errorMessage).show((activity as AppCompatActivity).supportFragmentManager, "Error!")
                }
                else -> binding.progressBar.setGone()
            }
        }
    }
}