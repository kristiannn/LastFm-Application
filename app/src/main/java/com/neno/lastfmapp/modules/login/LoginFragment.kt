package com.neno.lastfmapp.modules.login

import android.app.Activity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity
import com.neno.lastfmapp.*
import com.neno.lastfmapp.modules.charts.ChartsFragment
import com.neno.lastfmapp.modules.dialog.NotifyDialog
import com.neno.lastfmapp.modules.utils.fragments.BasicFragment
import com.neno.lastfmapp.modules.utils.BundleStrings
import com.neno.lastfmapp.modules.utils.fragments.setRootFragment
import kotlinx.android.synthetic.main.activity_main.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class LoginFragment : BasicFragment()
{
    private lateinit var usernameEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var loginButton: Button
    private lateinit var progressBar: ProgressBar
    private lateinit var imm: InputMethodManager

    private val viewModel: LoginViewModel by viewModel()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View?
    {
        return inflater.inflate(R.layout.login_layout, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?)
    {
        super.onActivityCreated(savedInstanceState)

        setObservers()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?)
    {
        super.onViewCreated(view, savedInstanceState)

        setupViews(view)
    }

    private fun setupViews(view: View)
    {
        usernameEditText = view.findViewById(R.id.etUsername)
        passwordEditText = view.findViewById(R.id.etPassword)
        loginButton = view.findViewById(R.id.buttonLogin)
        progressBar = view.findViewById(R.id.progressBar)
        imm = activity?.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager

        loginButton.setOnClickListener {
            onLoginClick()
        }

        passwordEditText.setOnEditorActionListener { _, actionId, _ ->
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
                    progressBar.setVisible()

                    activity?.window?.setFlags(
                        WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                        WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
                    )
                }
                it.errorMessage != null ->
                {
                    progressBar.setGone()
                    activity?.window?.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)

                    NotifyDialog(it.errorMessage).show((activity as AppCompatActivity).supportFragmentManager, "Error!")
                }
                it.userLogged != null ->
                {
                    progressBar.setGone()
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
                    progressBar.setGone()

                    activity?.window?.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                }
            }
        }
    }

    private fun onLoginClick()
    {
        imm.hideSoftInputFromWindow(usernameEditText.windowToken, 0)
        viewModel.getProfile(usernameEditText.text.toString(), passwordEditText.text.toString())
    }
}