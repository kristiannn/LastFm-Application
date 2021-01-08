package com.neno.lastfmapp.modules.details

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
import com.neno.lastfmapp.BaseController
import com.neno.lastfmapp.R
import com.neno.lastfmapp.modules.dialog.NotifyDialog
import com.neno.lastfmapp.modules.utils.BundleStrings
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class DetailsController(bundle: Bundle) : BaseController(bundle)
{
    constructor(artist: String) : this(Bundle().apply {
        putString(BundleStrings.ARTIST_KEY, artist)
    })

    constructor(artist: String, album: String) : this(Bundle().apply {
        putString(BundleStrings.ARTIST_KEY, artist)
        putString(BundleStrings.ALBUM_KEY, album)
    })

    constructor(artist: String, track: String, album: String? = null) : this(Bundle().apply {
        putString(BundleStrings.ARTIST_KEY, artist)
        putString(BundleStrings.TRACK_KEY, track)
    })

    private lateinit var detailsView: View
    private lateinit var detailsActivity: Activity

    private lateinit var recyclerView: RecyclerView
    private lateinit var recyclerAdapter: DetailsRecyclerAdapter
    private lateinit var progressBar: ProgressBar

    private val artist by lazy { args.getString(BundleStrings.ARTIST_KEY) }
    private val album by lazy { args.getString(BundleStrings.ALBUM_KEY) }
    private val track by lazy { args.getString(BundleStrings.TRACK_KEY) }

    private val viewModel: DetailsViewModel by viewModel { parametersOf(artist, album, track) }

    override fun toolbarTitle(): String? = if (track != null) track else if (album != null) album else artist

    override fun toolbarVisible(): Boolean = true

    override fun periodsVisible(): Boolean = false

    override fun currentNavigationUser(): String? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup, savedViewState: Bundle?): View
    {
        detailsView = inflater.inflate(R.layout.lists_layout_secondary, container, false)

        setupViews()
        setObservers()

        return detailsView
    }

    private fun setupViews()
    {
        detailsActivity = activity!!
        progressBar = detailsView.findViewById(R.id.progressBar)!!
        recyclerView = detailsView.findViewById(R.id.recyclerView)!!

        val layoutManager = LinearLayoutManager(detailsActivity)
        recyclerView.layoutManager = layoutManager
        recyclerAdapter = DetailsRecyclerAdapter()
        recyclerView.adapter = recyclerAdapter
        recyclerView.setHasFixedSize(true)
    }

    private fun setObservers()
    {
        viewModel.detailsState.observe(this, {
            when
            {
                it.artistDetails != null ->
                {
                    val list: List<LabelValue> = listOf(
                        LabelValue(null, it.artistDetails.image, null),
                        LabelValue(resources?.getString(R.string.artist), it.artistDetails.artist),
                        LabelValue(resources?.getString(R.string.published), it.artistDetails.published ?: "-"),
                        LabelValue(resources?.getString(R.string.listeners), it.artistDetails.listeners),
                        LabelValue(resources?.getString(R.string.playCount), it.artistDetails.playCount),
                        LabelValue(resources?.getString(R.string.bio), it.artistDetails.bio ?: "-", false)
                    )

                    recyclerAdapter.updateList(list)
                }

                it.albumDetails != null ->
                {
                    val list: List<LabelValue> = listOf(
                        LabelValue(null, it.albumDetails.image, null),
                        LabelValue(resources?.getString(R.string.artist), it.albumDetails.artist),
                        LabelValue(resources?.getString(R.string.album), it.albumDetails.album),
                        LabelValue(resources?.getString(R.string.published), it.albumDetails.published ?: "-"),
                        LabelValue(resources?.getString(R.string.listeners), it.albumDetails.listeners),
                        LabelValue(resources?.getString(R.string.playCount), it.albumDetails.playCount),
                        LabelValue(resources?.getString(R.string.bio), it.albumDetails.bio ?: "-", false)
                    )

                    recyclerAdapter.updateList(list)
                }

                it.trackDetails != null ->
                {
                    val list: List<LabelValue> = listOf(
                        LabelValue(null, it.trackDetails.image, null),
                        LabelValue(resources?.getString(R.string.artist), it.trackDetails.artist ?: "-"),
                        LabelValue(resources?.getString(R.string.album), it.trackDetails.album ?: "-"),
                        LabelValue(resources?.getString(R.string.track), it.trackDetails.track),
                        LabelValue(resources?.getString(R.string.duration), it.trackDetails.duration),
                        LabelValue(resources?.getString(R.string.published), it.trackDetails.published ?: "-"),
                        LabelValue(resources?.getString(R.string.listeners), it.trackDetails.listeners),
                        LabelValue(resources?.getString(R.string.playCount), it.trackDetails.playCount),
                        LabelValue(resources?.getString(R.string.bio), it.trackDetails.bio ?: "-", false)
                    )

                    recyclerAdapter.updateList(list)
                }
            }
        })

        viewModel.screenState.observe(this, {
            when
            {
                it.isLoading ->
                {
                    progressBar.visibility = View.VISIBLE
                    detailsActivity.window.setFlags(
                        WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                        WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
                    )
                }
                it.errorMessage != null ->
                {
                    progressBar.visibility = View.GONE
                    NotifyDialog(it.errorMessage).show((activity as AppCompatActivity).supportFragmentManager, "Error!")

                    detailsActivity.window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                }
                else ->
                {
                    progressBar.visibility = View.GONE

                    detailsActivity.window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                }
            }
        })
    }
}