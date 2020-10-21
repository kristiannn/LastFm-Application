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
import com.bluelinelabs.conductor.RouterTransaction
import com.bluelinelabs.conductor.changehandler.HorizontalChangeHandler
import com.neno.lastfmapp.BaseController
import com.neno.lastfmapp.MainActivity
import com.neno.lastfmapp.R
import com.neno.lastfmapp.modules.artists.ArtistsController
import com.neno.lastfmapp.modules.dialog.NotifyDialog
import org.koin.androidx.viewmodel.ext.android.viewModel

class LoginController : BaseController()
{
    private lateinit var loginView: View
    private lateinit var loginActivity: Activity
    private lateinit var usernameEditText: EditText
    private lateinit var loginButton: Button
    private lateinit var progressBar: ProgressBar
    private lateinit var imm: InputMethodManager

    private val viewModel: LoginViewModel by viewModel()

    override fun toolbarTitle(): String? = null

    override fun toolbarVisible(): Boolean = false

    override fun periodsVisible(): Boolean = false

    override fun currentNavigationUser(): String? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup, savedViewState: Bundle?): View
    {
        loginView = inflater.inflate(R.layout.login_layout, container, false)

        findViews()
        viewSetup()
        setObservers()

        return loginView
    }

    private fun findViews()
    {
        loginActivity = activity!!
        usernameEditText = loginView.findViewById(R.id.etUsername)
        loginButton = loginView.findViewById(R.id.buttonLogin)
        progressBar = loginView.findViewById(R.id.progressBar)
        imm = loginActivity.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
    }

    private fun viewSetup()
    {
        loginButton.setOnClickListener {
            onLoginClick()
        }

        usernameEditText.setOnEditorActionListener { _, actionId, _ ->
            return@setOnEditorActionListener when (actionId)
            {
                EditorInfo.IME_ACTION_DONE ->
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
        viewModel.screenState.observe(this) {
            when
            {
                it.isLoading ->
                {
                    progressBar.visibility = View.VISIBLE

                    loginActivity.window.setFlags(
                        WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                        WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
                    )
                }
                it.errorMessage != null ->
                {
                    progressBar.visibility = View.GONE
                    loginActivity.window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)

                    NotifyDialog(it.errorMessage).show((activity as AppCompatActivity).supportFragmentManager, "Error!")
                }
                it.userLogged != null ->
                {
                    progressBar.visibility = View.GONE
                    loginActivity.window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)

                    val mainActivity = activity as MainActivity
                    mainActivity.updateNavigationHeader()

                    router.setRoot(
                        RouterTransaction.with(
                            ArtistsController(
                                username = it.userLogged,
                                period = mainActivity.getSelectedPeriod()
                            )
                        )
                            .pushChangeHandler(HorizontalChangeHandler())
                            .popChangeHandler(HorizontalChangeHandler())
                    )
                }
                else ->
                {
                    progressBar.visibility = View.GONE

                    loginActivity.window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                }
            }
        }
    }

    private fun onLoginClick()
    {
        imm.hideSoftInputFromWindow(usernameEditText.windowToken, 0)
        viewModel.getProfile(usernameEditText.text.toString())
    }
}