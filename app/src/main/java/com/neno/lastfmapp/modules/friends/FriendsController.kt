package com.neno.lastfmapp.modules.friends

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
import com.neno.lastfmapp.MainActivity
import com.neno.lastfmapp.R
import com.neno.lastfmapp.modules.artists.ArtistsController
import com.neno.lastfmapp.modules.dialog.NotifyDialog
import com.neno.lastfmapp.modules.utils.BundleStrings
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class FriendsController(bundle: Bundle) : BaseController(bundle)
{
    constructor(username: String) : this(Bundle().apply {
        putString(BundleStrings.USERNAME_KEY, username)
    })

    private lateinit var friendsView: View
    private lateinit var friendsActivity: Activity
    private lateinit var recyclerView: RecyclerView
    private lateinit var recyclerAdapter: FriendsRecyclerAdapter
    private lateinit var progressBar: ProgressBar
    private lateinit var swipeContainer: SwipeRefreshLayout

    private val username by lazy { args.getString(BundleStrings.USERNAME_KEY) }

    private val viewModel: FriendsViewModel by viewModel { parametersOf(username) }

    override fun toolbarTitle(): String? = resources?.getString(R.string.friends)

    override fun toolbarVisible(): Boolean = true

    override fun periodsVisible(): Boolean = false

    override fun currentNavigationUser(): String? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup, savedViewState: Bundle?): View
    {
        friendsView = inflater.inflate(R.layout.lists_layout, container, false)

        setupViews()
        setObservers()

        return friendsView
    }

    private fun setupViews()
    {
        friendsActivity = activity!!
        recyclerView = friendsView.findViewById(R.id.recyclerView)!!
        progressBar = friendsView.findViewById(R.id.progressBar)!!
        swipeContainer = friendsView.findViewById(R.id.swipeContainer)!!

        swipeContainer.setOnRefreshListener {
            viewModel.getFriends()
            swipeContainer.isRefreshing = false
        }

        recyclerView.layoutManager = LinearLayoutManager(friendsActivity)
        recyclerAdapter = FriendsRecyclerAdapter(
            friendsList = viewModel.friendsListState.value!!.friendsList,
            onFriendsItemClicked = {
                val mainActivity = activity as MainActivity

                router.pushController(
                    RouterTransaction.with(
                        ArtistsController(
                            username = it,
                            period = mainActivity.getSelectedPeriod()
                        )
                    )
                        .popChangeHandler(HorizontalChangeHandler())
                        .pushChangeHandler(HorizontalChangeHandler())
                )
            }
        )
        recyclerView.adapter = recyclerAdapter
    }

    private fun setObservers()
    {
        viewModel.friendsListState.observe(this, {
            recyclerAdapter.updateList(it.friendsList)
        })

        viewModel.screenState.observe(this, {
            when
            {
                it.isLoading ->
                {
                    progressBar.visibility = View.VISIBLE

                    friendsActivity.window.setFlags(
                        WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                        WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
                    )
                }
                it.errorMessage != null ->
                {
                    progressBar.visibility = View.GONE
                    NotifyDialog(it.errorMessage).show((activity as AppCompatActivity).supportFragmentManager, "Error!")

                    friendsActivity.window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                }
                else ->
                {
                    progressBar.visibility = View.GONE

                    friendsActivity.window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                }
            }
        })
    }
}