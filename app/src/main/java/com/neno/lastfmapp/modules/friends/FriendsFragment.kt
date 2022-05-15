package com.neno.lastfmapp.modules.friends

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.neno.lastfmapp.MainActivity
import com.neno.lastfmapp.R
import com.neno.lastfmapp.databinding.ListsLayoutBinding
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
    //    private lateinit var recyclerView: RecyclerView
    private lateinit var binding: ListsLayoutBinding
    private lateinit var recyclerAdapter: FriendsRecyclerAdapter
//    private lateinit var progressBar: ProgressBar
//    private lateinit var swipeContainer: SwipeRefreshLayout

    private val username by lazy { arguments?.getString(BundleStrings.USERNAME_KEY) }

    private val viewModel: FriendsViewModel by viewModel { parametersOf(username) }

    override fun toolbarTitle(): String = resources.getString(R.string.friends)

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
            viewModel.getFriends()
            binding.swipeContainer.isRefreshing = false
        }

        binding.recyclerView.layoutManager = LinearLayoutManager(activity)
        recyclerAdapter = FriendsRecyclerAdapter(
            friendsList = listOf(),
            onFriendsItemClicked = { username, realName ->
                val mainActivity = activity as MainActivity

                ChartsFragment().also {
                    val bundle = Bundle()
                    bundle.putString(BundleStrings.USERNAME_KEY, username)
                    bundle.putString(BundleStrings.PERIOD_KEY, mainActivity.getSelectedPeriod())
                    bundle.putString(BundleStrings.REALNAME_KEY, realName)
                    it.arguments = bundle

                    parentFragmentManager.addFragment(R.id.fragment_container, it)
                }
            }
        )
        binding.recyclerView.adapter = recyclerAdapter
    }

    private fun setObservers()
    {
        viewModel.friendsListState.observe(viewLifecycleOwner) {
            recyclerAdapter.updateList(it)
        }

        viewModel.screenState.observe(viewLifecycleOwner) {
            when
            {
                it.isLoading -> binding.progressBar.setVisible()

                it.errorMessage != null ->
                {
                    binding.progressBar.setGone()
                    NotifyDialog(it.errorMessage).show((activity as AppCompatActivity).supportFragmentManager, "Error!")

                    activity?.window?.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                }

                else -> binding.progressBar.setGone()
            }
        }
    }
}