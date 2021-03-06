package com.neno.lastfmapp.modules.recents

import android.animation.ValueAnimator
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity
import androidx.core.animation.doOnEnd
import androidx.core.animation.doOnStart
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.neno.lastfmapp.R
import com.neno.lastfmapp.dp
import com.neno.lastfmapp.modules.details.DetailsFragment
import com.neno.lastfmapp.modules.dialog.NotifyDialog
import com.neno.lastfmapp.modules.utils.BundleStrings
import com.neno.lastfmapp.modules.utils.fragments.SecondaryFragment
import com.neno.lastfmapp.modules.utils.fragments.addFragment
import com.neno.lastfmapp.setGone
import com.neno.lastfmapp.setVisible
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class RecentsFragment : SecondaryFragment()
{
    private lateinit var recyclerView: RecyclerView
    private lateinit var recyclerAdapter: RecentsRecyclerAdapter
    private lateinit var progressBar: ProgressBar
    private lateinit var scrobbleButton: Button
    private lateinit var swipeContainer: SwipeRefreshLayout
    private var isReloading = false

    private val username by lazy { arguments?.getString(BundleStrings.USERNAME_KEY) }

    private val viewModel: RecentsViewModel by viewModel { parametersOf(username) }

    override fun toolbarTitle(): String? = username

    override fun currentNavigationUser(): String? = username

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
        recyclerView = view.findViewById(R.id.recyclerView)!!
        progressBar = view.findViewById(R.id.progressBar)!!
        scrobbleButton = view.findViewById(R.id.buttonScrobble)!!
        swipeContainer = view.findViewById(R.id.swipeContainer)!!

        swipeContainer.setOnRefreshListener {
            isReloading = true
            viewModel.getRecentTracks(true)
            swipeContainer.isRefreshing = false
        }

        val layoutManager = LinearLayoutManager(activity)

        recyclerView.layoutManager = layoutManager

        scrobbleButton.setOnClickListener { viewModel.scrobbleTracks(recyclerAdapter.getSelectedItems()) }

        recyclerAdapter = RecentsRecyclerAdapter(
            tracksList = listOf(),
            onTrackItemClicked = { artist, track ->

                DetailsFragment().also {
                    val bundle = Bundle()
                    bundle.putString(BundleStrings.USERNAME_KEY, username)
                    bundle.putString(BundleStrings.ARTIST_KEY, artist)
                    bundle.putString(BundleStrings.TRACK_KEY, track)
                    it.arguments = bundle

                    parentFragmentManager.addFragment(R.id.fragment_container, it)
                }
            },
            onSelectionChange = { selectionMode, selectedCount ->
                if (selectionMode)
                {
                    scrobbleButton.text = resources.getString(R.string.scrobble_songs, selectedCount)

                    if (!scrobbleButton.isVisible)
                    {
                        ValueAnimator.ofFloat(0f, 1f).also {
                            it.doOnStart { scrobbleButton.setVisible() }
                            it.duration = 300
                            it.addUpdateListener { animator -> scrobbleButton.alpha = animator.animatedValue as Float }
                            it.start()
                        }

                        ValueAnimator.ofFloat(
                            resources.displayMetrics.widthPixels.toFloat(),
                            resources.displayMetrics.widthPixels.toFloat() - (136.dp + 16.dp)
                        ).also {
                            it.duration = 300
                            it.addUpdateListener { animator -> scrobbleButton.x = animator.animatedValue as Float }
                            it.start()
                        }
                    }
                } else
                {
                    if (!scrobbleButton.isGone)
                    {
                        ValueAnimator.ofFloat(1f, 0f).also {
                            it.duration = 300
                            it.addUpdateListener { animator -> scrobbleButton.alpha = animator.animatedValue as Float }
                            it.start()
                        }

                        ValueAnimator.ofFloat(
                            resources.displayMetrics.widthPixels.toFloat() - (136.dp + 16.dp),
                            resources.displayMetrics.widthPixels.toFloat()
                        ).also {
                            it.duration = 300
                            it.addUpdateListener { animator -> scrobbleButton.x = animator.animatedValue as Float }
                            it.start()
                            it.doOnEnd { scrobbleButton.setGone() }
                        }
                    }
                }
            }
        )

        recyclerView.adapter = recyclerAdapter
        recyclerView.setHasFixedSize(true)

        recyclerView.addOnScrollListener(
            object : RecyclerView.OnScrollListener()
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

                        viewModel.getRecentTracks(false)
                    }
                }
            })
    }

    private fun setObservers()
    {
        viewModel.recentsListState.observe(viewLifecycleOwner, {
            recyclerAdapter.updateList(it)

            if (isReloading)
            {
                recyclerView.scrollToPosition(0)
                isReloading = false
            }
        })

        viewModel.screenState.observe(viewLifecycleOwner, {
            when
            {
                it.isLoading -> progressBar.visibility = View.VISIBLE

                it.isListUpdating -> progressBar.visibility = View.GONE

                it.errorMessage != null ->
                {
                    progressBar.visibility = View.GONE
                    NotifyDialog(it.errorMessage).show((activity as AppCompatActivity).supportFragmentManager, "Error!")

                    recyclerAdapter.removeLoadingItem()
                }

                it.scrobbles != null ->
                {
                    //val scrobbledTracks = it.scrobbles.first() // May use this at some point
                    val failedScrobbles = it.scrobbles.last()

                    val errorMessage = if (failedScrobbles == 0) resources.getString(R.string.scrobble_successful)
                    else resources.getString(R.string.scrobble_unsuccessful, failedScrobbles)

                    progressBar.visibility = View.GONE
                    NotifyDialog(errorMessage).show((activity as AppCompatActivity).supportFragmentManager, "Error!")

                    recyclerAdapter.removeLoadingItem()
                }

                else ->
                {
                    progressBar.visibility = View.GONE

                    recyclerAdapter.removeLoadingItem()
                }
            }
        })
    }

}