package com.neno.lastfmapp.modules.friends

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
import com.neno.lastfmapp.MainActivity
import com.neno.lastfmapp.R
import com.neno.lastfmapp.modules.charts.ChartsFragment
import com.neno.lastfmapp.modules.dialog.NotifyDialog
import com.neno.lastfmapp.modules.utils.BundleStrings
import com.neno.lastfmapp.modules.utils.fragments.SecondaryFragment
import com.neno.lastfmapp.modules.utils.fragments.addFragment
import com.neno.lastfmapp.setGone
import com.neno.lastfmapp.setVisible
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class FriendsFragment : SecondaryFragment()
{
    private lateinit var recyclerView: RecyclerView
    private lateinit var recyclerAdapter: FriendsRecyclerAdapter
    private lateinit var progressBar: ProgressBar
    private lateinit var swipeContainer: SwipeRefreshLayout

    private val username by lazy { arguments?.getString(BundleStrings.USERNAME_KEY) }

    private val viewModel: FriendsViewModel by viewModel { parametersOf(username) }

    override fun toolbarTitle(): String = resources.getString(R.string.friends)

    override fun currentNavigationUser(): String? = username

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View?
    {
        return inflater.inflate(R.layout.lists_layout, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?)
    {
        super.onViewCreated(view, savedInstanceState)

        setupViews(view)
        setObservers()
    }

    private fun setupViews(view: View)
    {
        recyclerView = view.findViewById(R.id.recyclerView)
        progressBar = view.findViewById(R.id.progressBar)
        swipeContainer = view.findViewById(R.id.swipeContainer)

        swipeContainer.setOnRefreshListener {
            viewModel.getFriends()
            swipeContainer.isRefreshing = false
        }

        recyclerView.layoutManager = LinearLayoutManager(activity)
        recyclerAdapter = FriendsRecyclerAdapter(
            friendsList = viewModel.friendsListState.value!!.friendsList,
            onFriendsItemClicked = { username ->
                val mainActivity = activity as MainActivity

                ChartsFragment().also {
                    val bundle = Bundle()
                    bundle.putString(BundleStrings.USERNAME_KEY, username)
                    bundle.putString(BundleStrings.PERIOD_KEY, mainActivity.getSelectedPeriod())
                    it.arguments = bundle

                    parentFragmentManager.addFragment(R.id.fragment_container, it)
                }
            }
        )
        recyclerView.adapter = recyclerAdapter
    }

    private fun setObservers()
    {
        viewModel.friendsListState.observe(viewLifecycleOwner, {
            recyclerAdapter.updateList(it.friendsList)
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