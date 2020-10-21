package com.neno.lastfmapp.modules.details

import android.app.Activity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.neno.lastfmapp.BaseController
import com.neno.lastfmapp.R
import com.neno.lastfmapp.modules.dialog.NotifyDialog
import com.neno.lastfmapp.modules.utils.BundleStrings
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

// TODO - this needs to be completely redone with recyclerview! It's too much of a mess like this
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

    //This is a bit ridiculous and should become a recyclerview at some point.
    private lateinit var coverIv: ImageView
    private lateinit var artistLabelTv: TextView
    private lateinit var artistValueTv: TextView
    private lateinit var albumLabelTv: TextView
    private lateinit var albumValueTv: TextView
    private lateinit var trackLabelTv: TextView
    private lateinit var trackValueTv: TextView
    private lateinit var durationLabelTv: TextView
    private lateinit var durationValueTv: TextView
    private lateinit var publishedLabelTv: TextView
    private lateinit var publishedValueTv: TextView
    private lateinit var listenersLabelTv: TextView
    private lateinit var listenersValueTv: TextView
    private lateinit var playCountLabelTv: TextView
    private lateinit var playCountValueTv: TextView
    private lateinit var bioLabelTv: TextView
    private lateinit var bioValueTv: TextView
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
        detailsView = inflater.inflate(R.layout.details_layout, container, false)

        setupViews()
        setObservers()

        return detailsView
    }

    private fun setupViews()
    {
        detailsActivity = activity!!
        coverIv = detailsView.findViewById(R.id.ivCover)!!
        progressBar = detailsView.findViewById(R.id.progressBar)!!
        artistLabelTv = detailsView.findViewById(R.id.tvArtistLabel)!!
        artistValueTv = detailsView.findViewById(R.id.tvArtistValue)!!
        albumLabelTv = detailsView.findViewById(R.id.tvAlbumLabel)!!
        albumValueTv = detailsView.findViewById(R.id.tvAlbumValue)!!
        trackLabelTv = detailsView.findViewById(R.id.tvTrackLabel)!!
        trackValueTv = detailsView.findViewById(R.id.tvTrackValue)!!
        durationLabelTv = detailsView.findViewById(R.id.tvDurationLabel)!!
        durationValueTv = detailsView.findViewById(R.id.tvDurationValue)!!
        publishedLabelTv = detailsView.findViewById(R.id.tvPublishedLabel)!!
        publishedValueTv = detailsView.findViewById(R.id.tvPublishedValue)!!
        listenersLabelTv = detailsView.findViewById(R.id.tvListenersLabel)!!
        listenersValueTv = detailsView.findViewById(R.id.tvListenersValue)!!
        playCountLabelTv = detailsView.findViewById(R.id.tvPlayCountLabel)!!
        playCountValueTv = detailsView.findViewById(R.id.tvPlayCountValue)!!
        bioLabelTv = detailsView.findViewById(R.id.tvBioLabel)!!
        bioValueTv = detailsView.findViewById(R.id.tvBioValue)!!
    }

    private fun setObservers()
    {
        viewModel.detailsState.observe(this, {
            when
            {
                it.artistDetails != null ->
                {
                    Glide.with(detailsView).load(it.artistDetails.image).into(coverIv)

                    albumLabelTv.visibility = View.GONE
                    albumValueTv.visibility = View.GONE
                    trackLabelTv.visibility = View.GONE
                    trackValueTv.visibility = View.GONE
                    durationLabelTv.visibility = View.GONE
                    durationValueTv.visibility = View.GONE

                    artistValueTv.text = it.artistDetails.artist
                    listenersValueTv.text = it.artistDetails.listeners
                    playCountValueTv.text = it.artistDetails.playCount

                    if (it.artistDetails.published != null) publishedValueTv.text = it.artistDetails.published
                    else publishedValueTv.text = "-"

                    if (it.artistDetails.bio != null) bioValueTv.text = it.artistDetails.bio
                    else bioValueTv.text = "-"
                }

                it.albumDetails != null ->
                {
                    Glide.with(detailsView).load(it.albumDetails.image).into(coverIv)

                    trackLabelTv.visibility = View.GONE
                    trackValueTv.visibility = View.GONE
                    durationLabelTv.visibility = View.GONE
                    durationValueTv.visibility = View.GONE

                    artistValueTv.text = it.albumDetails.artist
                    albumValueTv.text = it.albumDetails.album
                    listenersValueTv.text = it.albumDetails.listeners
                    playCountValueTv.text = it.albumDetails.playCount

                    if (it.albumDetails.published != null) publishedValueTv.text = it.albumDetails.published
                    else publishedValueTv.text = "-"

                    if (it.albumDetails.bio != null) bioValueTv.text = it.albumDetails.bio
                    else bioValueTv.text = "-"
                }

                it.trackDetails != null ->
                {
                    Glide.with(detailsView).load(it.trackDetails.image).into(coverIv)

                    trackValueTv.text = it.trackDetails.track
                    durationValueTv.text = it.trackDetails.duration
                    listenersValueTv.text = it.trackDetails.listeners
                    playCountValueTv.text = it.trackDetails.playCount

                    if (it.trackDetails.artist != null) artistValueTv.text = it.trackDetails.artist
                    else artistValueTv.text = "-"

                    if (it.trackDetails.album != null) albumValueTv.text = it.trackDetails.album
                    else albumValueTv.text = "-"

                    if (it.trackDetails.published != null) publishedValueTv.text = it.trackDetails.published
                    else publishedValueTv.text = "-"

                    if (it.trackDetails.bio != null) bioValueTv.text = it.trackDetails.bio
                    else bioValueTv.text = "-"
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