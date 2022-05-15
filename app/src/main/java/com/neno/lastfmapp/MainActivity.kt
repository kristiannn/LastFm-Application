package com.neno.lastfmapp

import android.animation.ValueAnimator
import android.os.Bundle
import android.view.View
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
import com.google.android.material.tabs.TabLayout
import com.neno.lastfmapp.databinding.ActivityMainBinding
import com.neno.lastfmapp.modules.charts.ChartsFragment
import com.neno.lastfmapp.modules.friends.FriendsFragment
import com.neno.lastfmapp.modules.login.LoginFragment
import com.neno.lastfmapp.modules.recents.RecentsFragment
import com.neno.lastfmapp.modules.settings.SettingsFragment
import com.neno.lastfmapp.modules.utils.AccountManager
import com.neno.lastfmapp.modules.utils.BundleStrings
import com.neno.lastfmapp.modules.utils.fragments.*
import com.neno.lastfmapp.network.utils.LastFmPeriodParams
import org.koin.android.ext.android.inject

class MainActivity : AppCompatActivity()
{
    private lateinit var drawerToggle: ActionBarDrawerToggle
    private lateinit var binding: ActivityMainBinding
    private var currentUser: String? = null

    private val navigationHeader: View by lazy { binding.navView.getHeaderView(0) }
    private val navigationUsername: TextView by lazy { navigationHeader.findViewById(R.id.tvUsername) }
    private val navigationProfilePicture: ImageView by lazy { navigationHeader.findViewById(R.id.ivProfilePicture) }

    private val accountManager: AccountManager by inject()

    val tabsLayout: TabLayout by lazy { findViewById(R.id.tabLayout) }

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setTheme(accountManager.getCurrentTheme())
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

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

                icon.color = binding.buttonPeriods.currentTextColor
                binding.toolbar.navigationIcon = icon
                binding.drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED)
            } else if (lastFragment is SecondaryFragment)
            {
                val icon = DrawerArrowDrawable(this@MainActivity)

                val animator = ValueAnimator.ofFloat(0f, 1f)
                animator.duration = 300
                animator.addUpdateListener { animation -> icon.progress = animation.animatedValue as Float }
                animator.start()

                icon.color = binding.buttonPeriods.currentTextColor
                binding.toolbar.navigationIcon = icon
                binding.drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
            }
        }

        if (savedInstanceState != null) return

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
        if (binding.drawerLayout.isDrawerOpen(GravityCompat.START))
        {
            binding.drawerLayout.closeDrawer(GravityCompat.START)
        } else
        {
            super.onBackPressed()
        }
    }

    private fun setupNavigationDrawer()
    {
        drawerToggle = ActionBarDrawerToggle(
            this,
            binding.drawerLayout,
            binding.toolbar,
            R.string.navigation_drawer_open,
            R.string.navigation_drawer_close
        )

        drawerToggle.drawerArrowDrawable.color = binding.buttonPeriods.currentTextColor
        binding.drawerLayout.addDrawerListener(drawerToggle)
        drawerToggle.syncState()

        binding.toolbar.setNavigationOnClickListener {

            if (supportFragmentManager.fragments.last() is ListsFragment)
            {
                binding.drawerLayout.openDrawer(binding.navView)
            } else
            {
                onBackPressed()
            }
        }

        binding.navView.setNavigationItemSelectedListener { item ->
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

                R.id.nav_settings ->
                {
                    supportFragmentManager.addFragment(R.id.fragment_container, SettingsFragment())
                }
            }

            binding.drawerLayout.closeDrawer(GravityCompat.START)
            true
        }
    }

    private fun setupPeriodSelector()
    {
        //Set button text
        when (getSelectedPeriod())
        {
            LastFmPeriodParams.Overall -> binding.buttonPeriods.text = resources.getString(R.string.period_overall)
            LastFmPeriodParams.Year -> binding.buttonPeriods.text = resources.getString(R.string.period_year)
            LastFmPeriodParams.HalfYear -> binding.buttonPeriods.text = resources.getString(R.string.period_half_year)
            LastFmPeriodParams.Quarter -> binding.buttonPeriods.text = resources.getString(R.string.period_quarter)
            LastFmPeriodParams.Month -> binding.buttonPeriods.text = resources.getString(R.string.period_month)
            LastFmPeriodParams.Week -> binding.buttonPeriods.text = resources.getString(R.string.period_week)
        }

        binding.buttonPeriods.setOnClickListener {

            val popup = PopupMenu(this, binding.buttonPeriods)
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
        binding.buttonPeriods.text = buttonTitle

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

    fun setToolbarTitle(title: String?) = title.let { binding.toolbar.title = it ?: "" }

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

            binding.drawerLayout.closeDrawer(GravityCompat.START)
        }
    }

    fun setToolbarVisibility(visible: Boolean)
    {
        if (binding.appBar.isVisible == visible) return

        if (visible)
        {
            binding.drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED)
            binding.appBar.visibility = View.VISIBLE
        } else
        {
            binding.drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
            binding.appBar.visibility = View.GONE
        }
    }

    fun setPeriodSelectorVisibility(visible: Boolean)
    {
        if (visible)
        {
            binding.buttonPeriods.visibility = View.VISIBLE
            binding.toolbar.setTitleMargin(
                binding.toolbar.titleMarginStart,
                binding.toolbar.titleMarginTop,
                resources.getDimension(R.dimen.periods_button_width).toInt(),
                binding.toolbar.titleMarginBottom
            )

            val animator = ValueAnimator.ofFloat(0f, 1f)
            animator.duration = 300
            animator.addUpdateListener { animation -> binding.buttonPeriods.alpha = animation.animatedValue as Float }
            animator.start()

            animator.doOnEnd { drawerToggle.syncState() }
        } else
        {
            val animator = ValueAnimator.ofFloat(1f, 0f)
            animator.duration = 300
            animator.addUpdateListener { animation -> binding.buttonPeriods.alpha = animation.animatedValue as Float }
            animator.start()

            animator.doOnEnd {
                binding.buttonPeriods.visibility = View.GONE
                binding.toolbar.setTitleMargin(
                    binding.toolbar.titleMarginStart,
                    binding.toolbar.titleMarginTop,
                    binding.toolbar.titleMarginStart,
                    binding.toolbar.titleMarginBottom
                )
            }
        }
    }
}