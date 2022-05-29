package com.neno.lastfmapp.modules.recents

import android.animation.ValueAnimator
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.animation.doOnEnd
import androidx.core.animation.doOnStart
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.neno.lastfmapp.R
import com.neno.lastfmapp.databinding.ListsLayoutBinding
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
    private lateinit var binding: ListsLayoutBinding
    private lateinit var recyclerAdapter: RecentsRecyclerAdapter

    private var isReloading = false

    private val username by lazy { arguments?.getString(BundleStrings.USERNAME_KEY) }

    private val viewModel: RecentsViewModel by viewModel { parametersOf(username) }

    override fun toolbarTitle(): String? = username

    override fun currentNavigationUser(): String? = username

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
            isReloading = true
            viewModel.getRecentTracks(true)
            binding.swipeContainer.isRefreshing = false
        }

        val layoutManager = LinearLayoutManager(activity)

        binding.recyclerView.layoutManager = layoutManager

        binding.buttonScrobble.setOnClickListener { viewModel.scrobbleTracks(recyclerAdapter.getSelectedItems()) }

        recyclerAdapter = RecentsRecyclerAdapter(
            tracksList = listOf(),
            onTrackItemClicked = { artist, track, image ->

                DetailsFragment().also {
                    val bundle = Bundle()
                    bundle.putString(BundleStrings.USERNAME_KEY, username)
                    bundle.putString(BundleStrings.ARTIST_KEY, artist)
                    bundle.putString(BundleStrings.TRACK_KEY, track)
                    bundle.putString(BundleStrings.IMAGE_KEY, image)
                    it.arguments = bundle

                    parentFragmentManager.addFragment(R.id.fragment_container, it)
                }
            },
            onSelectionChange = { selectionMode, selectedCount ->
                if (selectionMode)
                {
                    binding.buttonScrobble.text = resources.getString(R.string.scrobble_songs, selectedCount)

                    if (!binding.buttonScrobble.isVisible)
                    {
                        ValueAnimator.ofFloat(0f, 1f).also {
                            it.doOnStart { binding.buttonScrobble.setVisible() }
                            it.duration = 300
                            it.addUpdateListener { animator ->
                                binding.buttonScrobble.alpha = animator.animatedValue as Float
                            }
                            it.start()
                        }

                        ValueAnimator.ofFloat(
                            resources.displayMetrics.widthPixels.toFloat(),
                            resources.displayMetrics.widthPixels.toFloat() - (136.dp + 16.dp)
                        ).also {
                            it.duration = 300
                            it.addUpdateListener { animator ->
                                binding.buttonScrobble.x = animator.animatedValue as Float
                            }
                            it.start()
                        }
                    }
                } else
                {
                    if (!binding.buttonScrobble.isGone)
                    {
                        ValueAnimator.ofFloat(1f, 0f).also {
                            it.duration = 300
                            it.addUpdateListener { animator ->
                                binding.buttonScrobble.alpha = animator.animatedValue as Float
                            }
                            it.start()
                        }

                        ValueAnimator.ofFloat(
                            resources.displayMetrics.widthPixels.toFloat() - (136.dp + 16.dp),
                            resources.displayMetrics.widthPixels.toFloat()
                        ).also {
                            it.duration = 300
                            it.addUpdateListener { animator ->
                                binding.buttonScrobble.x = animator.animatedValue as Float
                            }
                            it.start()
                            it.doOnEnd { binding.buttonScrobble.setGone() }
                        }
                    }
                }
            }
        )

        binding.recyclerView.adapter = recyclerAdapter
        binding.recyclerView.setHasFixedSize(true)

        binding.recyclerView.addOnScrollListener(
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
        viewModel.recentsListState.observe(viewLifecycleOwner) {
            recyclerAdapter.updateList(it)

            if (isReloading)
            {
                binding.recyclerView.scrollToPosition(0)
                isReloading = false
            }
        }

        viewModel.screenState.observe(viewLifecycleOwner) {
            when
            {
                it.isLoading -> binding.progressBar.visibility = View.VISIBLE

                it.isListUpdating -> binding.progressBar.visibility = View.GONE

                it.errorMessage != null ->
                {
                    binding.progressBar.visibility = View.GONE
                    NotifyDialog(it.errorMessage).show((activity as AppCompatActivity).supportFragmentManager, "Error!")

                    recyclerAdapter.removeLoadingItem()
                }

                it.scrobbles != null ->
                {
                    //val scrobbledTracks = it.scrobbles.first() // May use this at some point
                    val failedScrobbles = it.scrobbles.last()

                    val errorMessage = if (failedScrobbles == 0) resources.getString(R.string.scrobble_successful)
                    else resources.getString(R.string.scrobble_unsuccessful, failedScrobbles)

                    binding.progressBar.visibility = View.GONE
                    NotifyDialog(errorMessage).show((activity as AppCompatActivity).supportFragmentManager, "Error!")

                    recyclerAdapter.removeLoadingItem()
                }

                else ->
                {
                    binding.progressBar.visibility = View.GONE

                    recyclerAdapter.removeLoadingItem()
                }
            }
        }
    }

}