package com.neno.lastfmapp.modules.artists

import android.app.Activity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
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

class ArtistsController(bundle: Bundle) : BaseController(bundle)
{
    constructor(username: String, period: String) : this(Bundle().apply {
        putString(BundleStrings.USERNAME_KEY, username)
        putString(BundleStrings.PERIOD_KEY, period)
    })

    private lateinit var artistsView: View
    private lateinit var artistsActivity: Activity
    private lateinit var recyclerView: RecyclerView
    private lateinit var recyclerAdapter: ArtistsRecyclerAdapter
    private lateinit var progressBar: ProgressBar
    private lateinit var swipeContainer: SwipeRefreshLayout

    private val username by lazy { args.getString(BundleStrings.USERNAME_KEY) }
    private val period by lazy { args.getString(BundleStrings.PERIOD_KEY) }

    private val viewModel: ArtistsViewModel by viewModel { parametersOf(username, period) }

    override fun toolbarTitle(): String? = username

    override fun toolbarVisible(): Boolean = true

    override fun periodsVisible(): Boolean = true

    override fun currentNavigationUser(): String? = username

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup, savedViewState: Bundle?): View
    {
        artistsView = inflater.inflate(R.layout.lists_layout, container, false)

        setupViews()
        setObservers()

        return artistsView
    }

    private fun setupViews()
    {
        artistsActivity = activity!!
        recyclerView = artistsView.findViewById(R.id.recyclerView)!!
        progressBar = artistsView.findViewById(R.id.progressBar)!!
        swipeContainer = artistsView.findViewById(R.id.swipeContainer)!!

        swipeContainer.setOnRefreshListener {
            viewModel.getArtists(true)
            swipeContainer.isRefreshing = false
        }

        val layoutManager = LinearLayoutManager(artistsActivity)

        recyclerView.layoutManager = layoutManager
        recyclerAdapter = ArtistsRecyclerAdapter(
            artistsList = viewModel.artistsListState.value!!.artistsList,
            onArtistItemClicked = {
                router.pushController(
                    RouterTransaction.with(
                        DetailsController(artist = it)
                    )
                        .popChangeHandler(HorizontalChangeHandler())
                        .pushChangeHandler(HorizontalChangeHandler())
                )
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
        viewModel.artistsListState.observe(this, {
            recyclerAdapter.updateList(it.artistsList)
        })

        viewModel.screenState.observe(this, {
            when
            {
                it.isLoading ->
                {
                    progressBar.visibility = View.VISIBLE

                    artistsActivity.window.setFlags(
                        WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                        WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
                    )
                }
                it.isListUpdating ->
                {
                    progressBar.visibility = View.GONE

                    artistsActivity.window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                }
                it.errorMessage != null ->
                {
                    progressBar.visibility = View.GONE
                    NotifyDialog(it.errorMessage).show((activity as AppCompatActivity).supportFragmentManager, "Error!")

                    recyclerAdapter.removeLoadingItem()
                    artistsActivity.window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                }
                else ->
                {
                    progressBar.visibility = View.GONE

                    recyclerAdapter.removeLoadingItem()
                    artistsActivity.window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                }
            }
        })
    }
}