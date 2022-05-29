package com.neno.lastfmapp.modules.charts.artists

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.neno.lastfmapp.MainActivity
import com.neno.lastfmapp.R
import com.neno.lastfmapp.databinding.ListsLayoutBinding
import com.neno.lastfmapp.modules.details.DetailsFragment
import com.neno.lastfmapp.modules.dialog.NotifyDialog
import com.neno.lastfmapp.modules.utils.BundleStrings
import com.neno.lastfmapp.modules.utils.fragments.addFragment
import com.neno.lastfmapp.setGone
import com.neno.lastfmapp.setVisible
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class ArtistsFragment : Fragment()
{
    private lateinit var binding: ListsLayoutBinding
    private lateinit var recyclerAdapter: ArtistsRecyclerAdapter

    private val username by lazy { arguments?.getString(BundleStrings.USERNAME_KEY) }
    private val period by lazy { arguments?.getString(BundleStrings.PERIOD_KEY) }

    private val viewModel: ArtistsViewModel by viewModel { parametersOf(username, period) }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View
    {
        binding = ListsLayoutBinding.inflate(inflater, container, false)
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
        binding.swipeContainer.setOnRefreshListener {
            viewModel.getArtists(true)
            binding.swipeContainer.isRefreshing = false
        }

        val layoutManager = LinearLayoutManager(activity)

        binding.recyclerView.layoutManager = layoutManager
        recyclerAdapter = ArtistsRecyclerAdapter(
            artistsList = listOf(),
            onArtistItemClicked = { artist, image ->

                DetailsFragment().also {
                    val bundle = Bundle()
                    bundle.putString(BundleStrings.USERNAME_KEY, username)
                    bundle.putString(BundleStrings.ARTIST_KEY, artist)
                    bundle.putString(BundleStrings.IMAGE_KEY, image)
                    it.arguments = bundle

                    (activity as MainActivity).supportFragmentManager.addFragment(R.id.fragment_container, it)
                }
            })
        binding.recyclerView.adapter = recyclerAdapter
        binding.recyclerView.setHasFixedSize(true)

        binding.recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener()
        {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int)
            {
                super.onScrollStateChanged(recyclerView, newState)

                if (!viewModel.screenState.value!!.isLoading && !viewModel.screenState.value!!.isListUpdating
                    && recyclerView.layoutManager!!.itemCount == layoutManager.findLastVisibleItemPosition() + 1
                    && viewModel.screenState.value!!.errorMessage == null
                )
                {
                    recyclerAdapter.addLoadingItem()
                    recyclerView.scrollToPosition(recyclerView.layoutManager!!.itemCount - 1)

                    viewModel.getArtists(false)
                }
            }
        })
    }

    private fun setObservers()
    {
        viewModel.artistsListState.observe(viewLifecycleOwner) {
            recyclerAdapter.updateList(it)
        }

        viewModel.screenState.observe(viewLifecycleOwner) {
            when
            {
                it.isLoading -> binding.progressBar.setVisible()

                it.isListUpdating -> binding.progressBar.setGone()

                it.errorMessage != null ->
                {
                    binding.progressBar.setGone()
                    NotifyDialog(it.errorMessage).show((activity as AppCompatActivity).supportFragmentManager, "Error!")
                    recyclerAdapter.removeLoadingItem()
                }

                else ->
                {
                    binding.progressBar.setGone()
                    recyclerAdapter.removeLoadingItem()
                }
            }
        }
    }
}