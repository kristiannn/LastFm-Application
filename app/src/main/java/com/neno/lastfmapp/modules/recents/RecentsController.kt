package com.neno.lastfmapp.modules.recents

import android.app.Activity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Button
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.bluelinelabs.conductor.RouterTransaction
import com.bluelinelabs.conductor.changehandler.HorizontalChangeHandler
import com.neno.lastfmapp.BaseController
import com.neno.lastfmapp.R
import com.neno.lastfmapp.modules.details.DetailsController
import com.neno.lastfmapp.modules.dialog.NotifyDialog
import com.neno.lastfmapp.modules.utils.BundleStrings
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class RecentsController(bundle: Bundle) : BaseController(bundle)
{
    constructor(username: String) : this(Bundle().apply {
        putString(BundleStrings.USERNAME_KEY, username)
    })

    private lateinit var recentsView: View
    private lateinit var recentsActivity: Activity
    private lateinit var recyclerView: RecyclerView
    private lateinit var recyclerAdapter: RecentsRecyclerAdapter
    private lateinit var progressBar: ProgressBar
    private lateinit var scrobbleButton: Button
    private lateinit var swipeContainer: SwipeRefreshLayout

    private val username by lazy { args.getString(BundleStrings.USERNAME_KEY) }

    private val viewModel: RecentsViewModel by viewModel { parametersOf(username) }

    override fun toolbarTitle(): String? = username

    override fun toolbarVisible(): Boolean = true

    override fun periodsVisible(): Boolean = false

    override fun currentNavigationUser(): String? = username

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup, savedViewState: Bundle?): View
    {
        recentsView = inflater.inflate(R.layout.lists_layout, container, false)

        setupViews()
        setObservers()

        return recentsView
    }

    private fun setupViews()
    {
        recentsActivity = activity!!
        recyclerView = recentsView.findViewById(R.id.recyclerView)!!
        progressBar = recentsView.findViewById(R.id.progressBar)!!
        scrobbleButton = recentsView.findViewById(R.id.buttonScrobble)!!
        swipeContainer = recentsView.findViewById(R.id.swipeContainer)!!

        scrobbleButton.visibility = View.INVISIBLE

        swipeContainer.setOnRefreshListener {
            viewModel.getRecentTracks(true)
            swipeContainer.isRefreshing = false
        }

        val layoutManager = LinearLayoutManager(recentsActivity)

        recyclerView.layoutManager = layoutManager

        scrobbleButton.setOnClickListener {

        }

        recyclerAdapter = RecentsRecyclerAdapter(
            tracksList = viewModel.recentsListState.value!!.tracksList,
            onTrackItemClicked = { artist, track ->
                router.pushController(
                    RouterTransaction.with(
                        DetailsController(
                            artist = artist,
                            track = track
                        )
                    )
                        .popChangeHandler(HorizontalChangeHandler())
                        .pushChangeHandler(HorizontalChangeHandler())
                )
            },
            onSelectionChange = { selectionMode, selectedCount ->
                if (selectionMode)
                {
                    scrobbleButton.text = resources?.getString(R.string.scrobble_songs, selectedCount)

                    if (scrobbleButton.visibility != View.VISIBLE)
                    {
                        scrobbleButton.animate()
                            .translationYBy(-scrobbleButton.height.toFloat())
                            .setDuration(300L)
                            .withStartAction { scrobbleButton.visibility = View.VISIBLE }
                            .start()
                    }
                } else
                {
                    if (scrobbleButton.visibility != View.GONE)
                    {
                        scrobbleButton.animate()
                            .translationYBy(scrobbleButton.height.toFloat())
                            .setDuration(300L)
                            .withEndAction { scrobbleButton.visibility = View.GONE }
                            .start()
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
        viewModel.recentsListState.observe(this, {
            recyclerAdapter.updateList(it.tracksList)
        })

        viewModel.screenState.observe(this, {
            when
            {
                it.isLoading ->
                {
                    progressBar.visibility = View.VISIBLE

                    recentsActivity.window.setFlags(
                        WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                        WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
                    )
                }
                it.isListUpdating ->
                {
                    progressBar.visibility = View.GONE

                    recentsActivity.window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                }
                it.errorMessage != null ->
                {
                    progressBar.visibility = View.GONE
                    NotifyDialog(it.errorMessage).show((activity as AppCompatActivity).supportFragmentManager, "Error!")

                    recyclerAdapter.removeLoadingItem()
                    recentsActivity.window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                }
                else ->
                {
                    progressBar.visibility = View.GONE

                    recyclerAdapter.removeLoadingItem()
                    recentsActivity.window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                }
            }
        })
    }

}