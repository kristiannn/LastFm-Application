package com.neno.lastfmapp.modules.login

import android.app.Activity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import com.neno.lastfmapp.MainActivity
import com.neno.lastfmapp.R
import com.neno.lastfmapp.databinding.LoginLayoutBinding
import com.neno.lastfmapp.modules.charts.ChartsFragment
import com.neno.lastfmapp.modules.dialog.NotifyDialog
import com.neno.lastfmapp.modules.utils.BundleStrings
import com.neno.lastfmapp.modules.utils.fragments.BasicFragment
import com.neno.lastfmapp.modules.utils.fragments.setRootFragment
import com.neno.lastfmapp.setGone
import com.neno.lastfmapp.setVisible
import org.koin.androidx.viewmodel.ext.android.viewModel

class LoginFragment : BasicFragment()
{
    private lateinit var binding: LoginLayoutBinding
    private val viewModel: LoginViewModel by viewModel()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View
    {
        binding = LoginLayoutBinding.inflate(inflater, container, false)
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

        binding.buttonLogin.setOnClickListener {
            onLoginClick()
        }

        binding.etPassword.setOnEditorActionListener { _, actionId, _ ->
            return@setOnEditorActionListener when (actionId)
            {
                EditorInfo.IME_ACTION_DONE, EditorInfo.IME_ACTION_GO, EditorInfo.IME_ACTION_SEND, EditorInfo.IME_ACTION_NEXT ->
                {
                    onLoginClick()

                    true
                }
                else -> false
            }
        }
    }

    private fun setObservers()
    {
        viewModel.screenState.observe(viewLifecycleOwner) {
            when
            {
                it.isLoading ->
                {
                    binding.progressBar.setVisible()

                    activity?.window?.setFlags(
                        WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                        WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
                    )
                }
                it.errorMessage != null ->
                {
                    binding.progressBar.setGone()
                    activity?.window?.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)

                    NotifyDialog(it.errorMessage).show((activity as AppCompatActivity).supportFragmentManager, "Error!")
                }
                it.userLogged != null ->
                {
                    binding.progressBar.setGone()
                    activity?.window?.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)

                    val mainActivity = activity as MainActivity
                    mainActivity.updateNavigationHeader()

                    ChartsFragment().also { chartFragment ->
                        val bundle = Bundle()
                        bundle.putString(BundleStrings.USERNAME_KEY, it.userLogged)
                        bundle.putString(BundleStrings.PERIOD_KEY, mainActivity.getSelectedPeriod())
                        chartFragment.arguments = bundle

                        parentFragmentManager.setRootFragment(R.id.fragment_container, chartFragment)
                    }
                }
                else ->
                {
                    binding.progressBar.setGone()

                    activity?.window?.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                }
            }
        }
    }

    private fun onLoginClick()
    {
        val imm = activity?.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(binding.etUsername.windowToken, 0)
        viewModel.getProfile(binding.etUsername.text.toString(), binding.etPassword.text.toString())
    }
}