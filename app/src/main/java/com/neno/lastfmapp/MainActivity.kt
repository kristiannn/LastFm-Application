package com.neno.lastfmapp

import android.animation.ValueAnimator
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.PopupMenu
import android.widget.TextView
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.graphics.drawable.DrawerArrowDrawable
import androidx.core.animation.doOnEnd
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.bluelinelabs.conductor.*
import com.bluelinelabs.conductor.changehandler.HorizontalChangeHandler
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.google.android.material.navigation.NavigationView
import com.neno.lastfmapp.modules.albums.AlbumsController
import com.neno.lastfmapp.modules.artists.ArtistsController
import com.neno.lastfmapp.modules.friends.FriendsController
import com.neno.lastfmapp.modules.login.LoginController
import com.neno.lastfmapp.modules.recents.RecentsController
import com.neno.lastfmapp.modules.tracks.TracksController
import com.neno.lastfmapp.modules.utils.AccountManager
import com.neno.lastfmapp.network.utils.LastFmPeriodParams
import kotlinx.android.synthetic.main.activity_main.*
import org.koin.android.ext.android.inject

class MainActivity : AppCompatActivity()
{
    private lateinit var container: ViewGroup
    private lateinit var router: Router

    private lateinit var drawer: DrawerLayout
    private lateinit var drawerToggle: ActionBarDrawerToggle
    private lateinit var navigationView: NavigationView
    private lateinit var navigationHeader: View
    private lateinit var navigationUsername: TextView
    private lateinit var navigationProfilePicture: ImageView
    private lateinit var periodsButton: Button

    private var currentUser: String? = null
    private val accountManager: AccountManager by inject()

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setupNavigationDrawer()
        updateNavigationHeader()
        setupPeriodSelector()

        container = findViewById(R.id.conductor_layout)

        router = Conductor.attachRouter(this, container, savedInstanceState)
        if (!router.hasRootController())
        {
            if (accountManager.isUserLogged())
            {
                router.setRoot(
                    RouterTransaction.with(
                        ArtistsController(
                            username = accountManager.getUser(),
                            period = getSelectedPeriod()
                        )
                    )
                )
            } else
            {
                router.setRoot(RouterTransaction.with(LoginController()))
            }
        }

        router.addChangeListener(object : ControllerChangeHandler.ControllerChangeListener
        {
            override fun onChangeCompleted(
                to: Controller?,
                from: Controller?,
                isPush: Boolean,
                container: ViewGroup,
                handler: ControllerChangeHandler
            )
            {
                if (router.backstackSize % 2 > 0)
                {
                    val icon = DrawerArrowDrawable(this@MainActivity)

                    val animator = ValueAnimator.ofFloat(1f, 0f)
                    animator.duration = 600
                    animator.addUpdateListener { animation -> icon.progress = animation.animatedValue as Float }
                    animator.start()

                    animator.doOnEnd { drawerToggle.syncState() }

                    toolbar.navigationIcon = icon
                    drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED)
                } else
                {
                    val icon = DrawerArrowDrawable(this@MainActivity)

                    val animator = ValueAnimator.ofFloat(0f, 1f)
                    animator.duration = 600
                    animator.addUpdateListener { animation -> icon.progress = animation.animatedValue as Float }
                    animator.start()

                    toolbar.navigationIcon = icon
                    drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
                }
            }
        })
    }

    override fun onBackPressed()
    {
        if (drawer.isDrawerOpen(GravityCompat.START))
        {
            drawer.closeDrawer(GravityCompat.START)
        } else if (!router.handleBack())
        {
            super.onBackPressed()
        }
    }

    private fun setupNavigationDrawer()
    {
        drawer = findViewById(R.id.drawer_layout)
        navigationView = findViewById(R.id.nav_view)
        navigationHeader = navigationView.getHeaderView(0)
        navigationUsername = navigationHeader.findViewById(R.id.tvUsername)
        navigationProfilePicture = navigationHeader.findViewById(R.id.ivProfilePicture)

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

            if (router.backstackSize % 2 > 0)
            {
                drawer.openDrawer(navigationView)
            } else
            {
                router.handleBack()
            }
        }
        navigationView.setNavigationItemSelectedListener { item ->
            when (item.itemId)
            {
                R.id.nav_artists ->
                {
                    router.replaceTopController(
                        RouterTransaction.with(
                            ArtistsController(
                                username = currentUser!!,
                                period = getSelectedPeriod()
                            )
                        )
                            .popChangeHandler(HorizontalChangeHandler())
                    )
                }

                R.id.nav_albums ->
                {
                    router.replaceTopController(
                        RouterTransaction.with(
                            AlbumsController(
                                username = currentUser!!,
                                period = getSelectedPeriod()
                            )
                        )
                            .popChangeHandler(HorizontalChangeHandler())
                    )
                }

                R.id.nav_tracks ->
                {
                    router.replaceTopController(
                        RouterTransaction.with(
                            TracksController(
                                username = currentUser!!,
                                period = getSelectedPeriod()
                            )
                        )
                            .popChangeHandler(HorizontalChangeHandler())
                    )
                }

                R.id.nav_friends ->
                {
                    router.pushController(
                        RouterTransaction.with(FriendsController(currentUser!!))
                            .popChangeHandler(HorizontalChangeHandler())
                    )
                }

                R.id.nav_recently_played ->
                {
                    router.replaceTopController(
                        RouterTransaction.with(RecentsController(currentUser!!))
                            .popChangeHandler(HorizontalChangeHandler())
                    )
                }

                R.id.nav_logout ->
                {
                    accountManager.logoutUser()

                    router.setRoot(
                        RouterTransaction.with(LoginController())
                            .popChangeHandler(HorizontalChangeHandler())
                    )
                }
            }

            drawer.closeDrawer(GravityCompat.START)
            true
        }
    }

    private fun setupPeriodSelector()
    {
        periodsButton = findViewById(R.id.buttonPeriods)

        //Set button text
        when (getSelectedPeriod())
        {
            LastFmPeriodParams.Overall.tag -> periodsButton.text = resources.getString(R.string.period_overall)
            LastFmPeriodParams.Year.tag -> periodsButton.text = resources.getString(R.string.period_year)
            LastFmPeriodParams.HalfYear.tag -> periodsButton.text = resources.getString(R.string.period_half_year)
            LastFmPeriodParams.Quarter.tag -> periodsButton.text = resources.getString(R.string.period_quarter)
            LastFmPeriodParams.Month.tag -> periodsButton.text = resources.getString(R.string.period_month)
            LastFmPeriodParams.Week.tag -> periodsButton.text = resources.getString(R.string.period_week)
        }

        periodsButton.setOnClickListener {

            val popup = PopupMenu(this@MainActivity, periodsButton)
            popup.menuInflater.inflate(R.menu.periods_popup, popup.menu)

            popup.setOnMenuItemClickListener { menuItem ->
                when (menuItem.itemId)
                {
                    R.id.period_overall ->
                    {
                        periodSelected(LastFmPeriodParams.Overall.tag, menuItem.title.toString())
                    }

                    R.id.period_year ->
                    {
                        periodSelected(LastFmPeriodParams.Year.tag, menuItem.title.toString())
                    }

                    R.id.period_half_year ->
                    {
                        periodSelected(LastFmPeriodParams.HalfYear.tag, menuItem.title.toString())
                    }

                    R.id.period_quarter ->
                    {
                        periodSelected(LastFmPeriodParams.Quarter.tag, menuItem.title.toString())
                    }

                    R.id.period_month ->
                    {
                        periodSelected(LastFmPeriodParams.Month.tag, menuItem.title.toString())
                    }

                    R.id.period_week ->
                    {
                        periodSelected(LastFmPeriodParams.Week.tag, menuItem.title.toString())
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

        when (router.backstack.last().controller)
        {
            is ArtistsController -> router.replaceTopController(
                RouterTransaction.with(
                    ArtistsController(
                        username = currentUser!!,
                        period = selectedPeriod
                    )
                )
            )

            is AlbumsController -> router.replaceTopController(
                RouterTransaction.with(
                    AlbumsController(
                        username = currentUser!!,
                        period = selectedPeriod
                    )
                )
            )

            is TracksController -> router.replaceTopController(
                RouterTransaction.with(
                    TracksController(
                        username = currentUser!!,
                        period = selectedPeriod
                    )
                )
            )
        }
    }

    fun setCurrentNavUser(username: String?)
    {
        currentUser = username ?: accountManager.getUser()
    }

    fun getSelectedPeriod(): String = accountManager.getPeriodPreference()

    fun updateNavigationHeader()
    {
        navigationUsername.text = accountManager.getUser()
        Glide.with(this).load(accountManager.getProfilePicture())
            .transform(RoundedCorners(20))
            .into(navigationProfilePicture)

        navigationProfilePicture.setOnClickListener {

            router.replaceTopController(
                RouterTransaction.with(
                    ArtistsController(
                        username = accountManager.getUser(),
                        period = getSelectedPeriod()
                    )
                )
                    .popChangeHandler(HorizontalChangeHandler())
            )

            drawer.closeDrawer(GravityCompat.START)
        }
    }

    fun setToolbarTitle(title: String?)
    {
        if (title.isNullOrEmpty()) toolbar.title = ""

        toolbar.title = title
    }

    fun setToolbarVisibility(visible: Boolean)
    {
        if (visible)
        {
            drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED)
            toolbar.visibility = View.VISIBLE
        } else
        {
            drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
            toolbar.visibility = View.GONE
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
                136.dp,
                toolbar.titleMarginBottom
            )

            val animator = ValueAnimator.ofFloat(0f, 1f)
            animator.duration = 600
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
