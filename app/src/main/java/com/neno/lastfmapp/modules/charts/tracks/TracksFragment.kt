package com.neno.lastfmapp.modules.charts.tracks

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.neno.lastfmapp.MainActivity
import com.neno.lastfmapp.R
import com.neno.lastfmapp.modules.details.DetailsFragment
import com.neno.lastfmapp.modules.dialog.NotifyDialog
import com.neno.lastfmapp.modules.utils.BundleStrings
import com.neno.lastfmapp.modules.utils.fragments.addFragment
import com.neno.lastfmapp.setGone
import com.neno.lastfmapp.setVisible
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class TracksFragment : Fragment()
{
    private lateinit var recyclerView: RecyclerView
    private lateinit var recyclerAdapter: TracksRecyclerAdapter
    private lateinit var progressBar: ProgressBar
    private lateinit var swipeContainer: SwipeRefreshLayout

    private val username by lazy { arguments?.getString(BundleStrings.USERNAME_KEY) }
    private val period by lazy { arguments?.getString(BundleStrings.PERIOD_KEY) }

    private val viewModel: TracksViewModel by viewModel { parametersOf(username, period) }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View?
    {
        return inflater.inflate(R.layout.lists_layout, container, false)
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
        recyclerView = view.findViewById(R.id.recyclerView)
        progressBar = view.findViewById(R.id.progressBar)
        swipeContainer = view.findViewById(R.id.swipeContainer)

        swipeContainer.setOnRefreshListener {
            viewModel.getTracks(true)
            swipeContainer.isRefreshing = false
        }

        val layoutManager = LinearLayoutManager(activity)

        recyclerView.layoutManager = layoutManager

        recyclerAdapter = TracksRecyclerAdapter(
            tracksList = listOf(),
            onTrackItemClicked = { artist, track ->

                DetailsFragment().also {
                    val bundle = Bundle()
                    bundle.putString(BundleStrings.USERNAME_KEY, username)
                    bundle.putString(BundleStrings.ARTIST_KEY, artist)
                    bundle.putString(BundleStrings.TRACK_KEY, track)
                    it.arguments = bundle

                    (activity as MainActivity).supportFragmentManager.addFragment(R.id.fragment_container, it)
                }
            })

        recyclerView.adapter = recyclerAdapter
        recyclerView.setHasFixedSize(true)

        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener()
        {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int)
            {
                super.onScrollStateChanged(recyclerView, newState)

                if (!viewModel.screenState.value!!.isLoading && !viewModel.screenState.value!!.isListUpdating
                    && recyclerView.layoutManager!!.itemCount == layoutManager.findLastVisibleItemPosition() + 1
                )
                {
                    recyclerAdapter.addLoadingItem()
                    recyclerView.scrollToPosition(recyclerView.layoutManager!!.itemCount - 1)

                    viewModel.getTracks(false)
                }
            }
        })
    }

    private fun setObservers()
    {
        viewModel.tracksListState.observe(viewLifecycleOwner, {
            recyclerAdapter.updateList(it)
        })

        viewModel.screenState.observe(viewLifecycleOwner, {
            when
            {
                it.isLoading -> progressBar.setVisible()

                it.isListUpdating -> progressBar.setGone()

                it.errorMessage != null ->
                {
                    progressBar.setGone()
                    NotifyDialog(it.errorMessage).show((activity as AppCompatActivity).supportFragmentManager, "Error!")

                    recyclerAdapter.removeLoadingItem()
                }

                else ->
                {
                    progressBar.setGone()

                    recyclerAdapter.removeLoadingItem()
                }
            }
        })
    }
}