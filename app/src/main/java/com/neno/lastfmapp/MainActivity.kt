package com.neno.lastfmapp

import android.animation.ValueAnimator
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.PopupMenu
import android.widget.TextView
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.graphics.drawable.DrawerArrowDrawable
import androidx.core.animation.doOnEnd
import androidx.core.animation.doOnStart
import androidx.core.view.GravityCompat
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView
import com.google.android.material.tabs.TabLayout
import com.neno.lastfmapp.modules.charts.ChartsFragment
import com.neno.lastfmapp.modules.friends.FriendsFragment
import com.neno.lastfmapp.modules.login.LoginFragment
import com.neno.lastfmapp.modules.recents.RecentsFragment
import com.neno.lastfmapp.modules.utils.*
import com.neno.lastfmapp.modules.utils.fragments.*
import com.neno.lastfmapp.network.utils.LastFmPeriodParams
import kotlinx.android.synthetic.main.activity_main.*
import org.koin.android.ext.android.inject

class MainActivity : AppCompatActivity()
{
    private lateinit var drawerToggle: ActionBarDrawerToggle
    private var currentUser: String? = null

    private val drawer: DrawerLayout by lazy { findViewById(R.id.drawer_layout) }
    private val navigationView: NavigationView by lazy { findViewById(R.id.nav_view) }
    private val navigationHeader: View by lazy { navigationView.getHeaderView(0) }
    private val navigationUsername: TextView by lazy { navigationHeader.findViewById(R.id.tvUsername) }
    private val navigationProfilePicture: ImageView by lazy { navigationHeader.findViewById(R.id.ivProfilePicture) }
    private val periodsButton: Button by lazy { findViewById(R.id.buttonPeriods) }
    private val accountManager: AccountManager by inject()

    val tabsLayout: TabLayout by lazy { findViewById(R.id.tabLayout) }

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        setupNavigationDrawer()
        updateNavigationHeader()
        setupPeriodSelector()

        supportFragmentManager.addOnBackStackChangedListener {
            val lastFragment = supportFragmentManager.fragments.last()

            currentUser = lastFragment.arguments?.getString(BundleStrings.USERNAME_KEY)

            if (lastFragment is BaseFragment) lastFragment.updateSettings()

            if (lastFragment is ListsFragment)
            {
                val icon = DrawerArrowDrawable(this@MainActivity)

                val animator = ValueAnimator.ofFloat(1f, 0f)
                animator.duration = 300
                animator.addUpdateListener { animation -> icon.progress = animation.animatedValue as Float }
                animator.start()

                animator.doOnEnd { drawerToggle.syncState() }

                toolbar.navigationIcon = icon
                drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED)
            } else if (lastFragment is SecondaryFragment)
            {
                val icon = DrawerArrowDrawable(this@MainActivity)

                val animator = ValueAnimator.ofFloat(0f, 1f)
                animator.duration = 300
                animator.addUpdateListener { animation -> icon.progress = animation.animatedValue as Float }
                animator.start()

                toolbar.navigationIcon = icon
                drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
            }
        }

        if (accountManager.isUserLogged())
        {
            ChartsFragment().also {
                val bundle = Bundle()
                bundle.putString(BundleStrings.USERNAME_KEY, accountManager.getUser())
                bundle.putString(BundleStrings.PERIOD_KEY, getSelectedPeriod())
                it.arguments = bundle

                supportFragmentManager.setRootFragment(R.id.fragment_container, it)
            }
        } else
        {
            LoginFragment().also { supportFragmentManager.setRootFragment(R.id.fragment_container, it) }
        }
    }

    override fun onBackPressed()
    {
        if (drawer.isDrawerOpen(GravityCompat.START))
        {
            drawer.closeDrawer(GravityCompat.START)
        } else
        {
            super.onBackPressed()
        }
    }

    private fun setupNavigationDrawer()
    {
        drawerToggle = ActionBarDrawerToggle(
            this,
            drawer,
            toolbar,
            R.string.navigation_drawer_open,
            R.string.navigation_drawer_close
        )

        drawer.addDrawerListener(drawerToggle)
        drawerToggle.syncState()

        toolbar.setNavigationOnClickListener {

            if (supportFragmentManager.fragments.last() is ListsFragment)
            {
                drawer.openDrawer(navigationView)
            } else
            {
                onBackPressed()
            }
        }

        navigationView.setNavigationItemSelectedListener { item ->
            when (item.itemId)
            {
                R.id.nav_charts ->
                {
                    if (supportFragmentManager.fragments.last() !is ListsFragment)
                    {
                        ChartsFragment().also {
                            val bundle = Bundle()
                            bundle.putString(BundleStrings.USERNAME_KEY, getCurrentNavUser())
                            bundle.putString(BundleStrings.PERIOD_KEY, getSelectedPeriod())
                            it.arguments = bundle

                            supportFragmentManager.addFragment(R.id.fragment_container, it)
                        }
                    }
                }

                R.id.nav_friends ->
                {
                    FriendsFragment().also {
                        val bundle = Bundle()
                        bundle.putString(BundleStrings.USERNAME_KEY, getCurrentNavUser())
                        it.arguments = bundle

                        supportFragmentManager.addFragment(R.id.fragment_container, it)
                    }
                }

                R.id.nav_recently_played ->
                {
                    RecentsFragment().also {
                        val bundle = Bundle()
                        bundle.putString(BundleStrings.USERNAME_KEY, getCurrentNavUser())
                        it.arguments = bundle

                        supportFragmentManager.addFragment(R.id.fragment_container, it)
                    }
                }

                R.id.nav_logout ->
                {
                    accountManager.logoutUser()
                    LoginFragment().also { supportFragmentManager.setRootFragment(R.id.fragment_container, it) }
                }
            }

            drawer.closeDrawer(GravityCompat.START)
            true
        }
    }

    private fun setupPeriodSelector()
    {
        //Set button text
        when (getSelectedPeriod())
        {
            LastFmPeriodParams.Overall -> periodsButton.text = resources.getString(R.string.period_overall)
            LastFmPeriodParams.Year -> periodsButton.text = resources.getString(R.string.period_year)
            LastFmPeriodParams.HalfYear -> periodsButton.text = resources.getString(R.string.period_half_year)
            LastFmPeriodParams.Quarter -> periodsButton.text = resources.getString(R.string.period_quarter)
            LastFmPeriodParams.Month -> periodsButton.text = resources.getString(R.string.period_month)
            LastFmPeriodParams.Week -> periodsButton.text = resources.getString(R.string.period_week)
        }

        periodsButton.setOnClickListener {

            val popup = PopupMenu(this, periodsButton)
            popup.menuInflater.inflate(R.menu.periods_popup, popup.menu)

            popup.setOnMenuItemClickListener { menuItem ->
                when (menuItem.itemId)
                {
                    R.id.period_overall ->
                    {
                        periodSelected(LastFmPeriodParams.Overall, menuItem.title.toString())
                    }

                    R.id.period_year ->
                    {
                        periodSelected(LastFmPeriodParams.Year, menuItem.title.toString())
                    }

                    R.id.period_half_year ->
                    {
                        periodSelected(LastFmPeriodParams.HalfYear, menuItem.title.toString())
                    }

                    R.id.period_quarter ->
                    {
                        periodSelected(LastFmPeriodParams.Quarter, menuItem.title.toString())
                    }

                    R.id.period_month ->
                    {
                        periodSelected(LastFmPeriodParams.Month, menuItem.title.toString())
                    }

                    R.id.period_week ->
                    {
                        periodSelected(LastFmPeriodParams.Week, menuItem.title.toString())
                    }
                }
                true
            }

            popup.show()
        }
    }

    private fun periodSelected(selectedPeriod: String, buttonTitle: String)
    {
        accountManager.setPeriodPreference(selectedPeriod)
        periodsButton.text = buttonTitle

        ChartsFragment().also {
            val bundle = Bundle()
            bundle.putString(BundleStrings.USERNAME_KEY, getCurrentNavUser())
            bundle.putString(BundleStrings.PERIOD_KEY, getSelectedPeriod())
            it.arguments = bundle

            supportFragmentManager.apply {
                if (this.backStackEntryCount == 0)
                    this.setRootFragment(R.id.fragment_container, it)
                else
                    this.replaceFragment(R.id.fragment_container, it)
            }
        }
    }

    private fun getCurrentNavUser() = currentUser ?: accountManager.getUser()

    fun getSelectedPeriod(): String = accountManager.getPeriodPreference()

    fun setToolbarTitle(title: String?) = title.let { toolbar.title = it ?: "" }

    fun setTabsVisibility(visible: Boolean)
    {
        if (!tabsLayout.isGone && !visible)
        {
            ValueAnimator.ofFloat(1f, 0f).also {
                it.duration = 300
                it.addUpdateListener { animator -> tabsLayout.alpha = animator.animatedValue as Float }
                it.start()
            }

            ValueAnimator.ofFloat(56.dp.toFloat(), 0f).also {
                it.duration = 300
                it.addUpdateListener { animator -> tabsLayout.y = animator.animatedValue as Float }
                it.start()
                it.doOnEnd { tabsLayout.setGone() }
            }
        } else if (!tabsLayout.isVisible && visible)
        {
            ValueAnimator.ofFloat(0f, 1f).also {
                it.doOnStart { tabsLayout.setVisible() }
                it.duration = 300
                it.addUpdateListener { animator -> tabsLayout.alpha = animator.animatedValue as Float }
                it.start()
            }

            ValueAnimator.ofFloat(0f, 56.dp.toFloat()).also {
                it.duration = 300
                it.addUpdateListener { animator -> tabsLayout.y = animator.animatedValue as Float }
                it.start()
            }
        }
    }

    fun setCurrentNavUser(username: String?) = username.let { currentUser = username ?: accountManager.getUser() }

    fun updateNavigationHeader()
    {
        navigationUsername.text = accountManager.getUser()
        navigationProfilePicture.loadRoundedImage(accountManager.getProfilePicture())

        navigationProfilePicture.setOnClickListener {

            ChartsFragment().also {
                val bundle = Bundle()
                bundle.putString(BundleStrings.USERNAME_KEY, accountManager.getUser())
                bundle.putString(BundleStrings.PERIOD_KEY, getSelectedPeriod())
                it.arguments = bundle

                supportFragmentManager.setRootFragment(R.id.fragment_container, it)
            }

            drawer.closeDrawer(GravityCompat.START)
        }
    }

    fun setToolbarVisibility(visible: Boolean)
    {
        if (appBar.isVisible == visible) return

        if (visible)
        {
            drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED)
            appBar.visibility = View.VISIBLE
        } else
        {
            drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
            appBar.visibility = View.GONE
        }
    }

    fun setPeriodSelectorVisibility(visible: Boolean)
    {
        if (visible)
        {
            periodsButton.visibility = View.VISIBLE
            toolbar.setTitleMargin(
                toolbar.titleMarginStart,
                toolbar.titleMarginTop,
                resources.getDimension(R.dimen.periods_button_width).toInt(),
                toolbar.titleMarginBottom
            )

            val animator = ValueAnimator.ofFloat(0f, 1f)
            animator.duration = 300
            animator.addUpdateListener { animation -> periodsButton.alpha = animation.animatedValue as Float }
            animator.start()

            animator.doOnEnd { drawerToggle.syncState() }
        } else
        {
            val animator = ValueAnimator.ofFloat(1f, 0f)
            animator.duration = 300
            animator.addUpdateListener { animation -> periodsButton.alpha = animation.animatedValue as Float }
            animator.start()

            animator.doOnEnd {
                periodsButton.visibility = View.GONE
                toolbar.setTitleMargin(
                    toolbar.titleMarginStart,
                    toolbar.titleMarginTop,
                    toolbar.titleMarginStart,
                    toolbar.titleMarginBottom
                )
            }
        }
    }
}