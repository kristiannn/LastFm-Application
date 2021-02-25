package com.neno.lastfmapp.modules.details

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?)
    {
        super.onViewCreated(view, savedInstanceState)

        setupViews(view)
        setObservers()
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
            when
            {
                it.artistDetails != null ->
                {
                    val list: List<LabelValue> = listOf(
                        LabelValue(null, it.artistDetails.image, null),
                        LabelValue(resources.getString(R.string.artist), it.artistDetails.artist),
                        LabelValue(resources.getString(R.string.published), it.artistDetails.published ?: "-"),
                        LabelValue(resources.getString(R.string.listeners), it.artistDetails.listeners),
                        LabelValue(resources.getString(R.string.playCount), it.artistDetails.playCount),
                        LabelValue(resources.getString(R.string.bio), it.artistDetails.bio ?: "-", false)
                    )

                    recyclerAdapter.updateList(list)
                }

                it.albumDetails != null ->
                {
                    val list: List<LabelValue> = listOf(
                        LabelValue(null, it.albumDetails.image, null),
                        LabelValue(resources.getString(R.string.artist), it.albumDetails.artist),
                        LabelValue(resources.getString(R.string.album), it.albumDetails.album),
                        LabelValue(resources.getString(R.string.published), it.albumDetails.published ?: "-"),
                        LabelValue(resources.getString(R.string.listeners), it.albumDetails.listeners),
                        LabelValue(resources.getString(R.string.playCount), it.albumDetails.playCount),
                        LabelValue(resources.getString(R.string.bio), it.albumDetails.bio ?: "-", false)
                    )

                    recyclerAdapter.updateList(list)
                }

                it.trackDetails != null ->
                {
                    val list: List<LabelValue> = listOf(
                        LabelValue(null, it.trackDetails.image, null),
                        LabelValue(resources.getString(R.string.artist), it.trackDetails.artist ?: "-"),
                        LabelValue(resources.getString(R.string.album), it.trackDetails.album ?: "-"),
                        LabelValue(resources.getString(R.string.track), it.trackDetails.track),
                        LabelValue(resources.getString(R.string.duration), it.trackDetails.duration),
                        LabelValue(resources.getString(R.string.published), it.trackDetails.published ?: "-"),
                        LabelValue(resources.getString(R.string.listeners), it.trackDetails.listeners),
                        LabelValue(resources.getString(R.string.playCount), it.trackDetails.playCount),
                        LabelValue(resources.getString(R.string.bio), it.trackDetails.bio ?: "-", false)
                    )

                    recyclerAdapter.updateList(list)
                }
            }
        })

        viewModel.screenState.observe(viewLifecycleOwner, {
            when
            {
                it.isLoading ->
                {
                    progressBar.setVisible()
                    activity?.window?.setFlags(
                        WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                        WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
                    )
                }
                it.errorMessage != null ->
                {
                    progressBar.setGone()
                    NotifyDialog(it.errorMessage).show((activity as AppCompatActivity).supportFragmentManager, "Error!")

                    activity?.window?.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                }
                else ->
                {
                    progressBar.setGone()

                    activity?.window?.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                }
            }
        })
    }
}