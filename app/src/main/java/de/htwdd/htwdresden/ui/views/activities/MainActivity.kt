package de.htwdd.htwdresden.ui.views.activities

import android.os.Bundle
import android.os.Handler
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.onNavDestinationSelected
import androidx.navigation.ui.setupWithNavController
import de.htwdd.htwdresden.R
import de.htwdd.htwdresden.utils.extensions.dp
import de.htwdd.htwdresden.utils.holders.CryptoSharedPreferencesHolder
import es.dmoral.toasty.Toasty
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity: AppCompatActivity() {

    private val appBarConfiguration: AppBarConfiguration by lazy {
        AppBarConfiguration(setOf(                                                                  // Top Level Fragments
            R.id.overview_page_fragment,
            R.id.timetable_page_fragment,
            R.id.grades_page_fragment,
            R.id.exams_page_fragment,
            R.id.canteen_page_fragment,
            R.id.room_occupancy_page_fragment,
            R.id.campus_plan_page_fragment,
            R.id.mangement_page_fragment,
            R.id.settings_page_fragment
        ), drawerLayout)
    }
    private val navController: NavController by lazy { findNavController(R.id.navigationHost) }
    private var doubleBackToExitPressedOnce = false
    private val cph by lazy { CryptoSharedPreferencesHolder.instance }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setupNavigation()

        if (cph.needsOnboarding()) {
            navController.navigate(R.id.onboarding_page_fragment)
        }
    }

    private fun setupNavigation() {
        setSupportActionBar(toolbar)
        collapsingToolbarLayout.setupWithNavController(toolbar, navController, appBarConfiguration)
        navigationView.apply {
            setupWithNavController(navController)
            setNavigationItemSelectedListener { item ->
                drawerLayout.closeDrawers()
                if (item.onNavDestinationSelected(navController)) {
                    item.apply {
                        isCheckable = true
                        isChecked   = true
                    }
                    true
                } else {
                    false
                }
            }
        }

        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.meals_pager_page_fragment,
                R.id.room_occupancy_detail_page_fragment -> {
                    drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
                }

                R.id.study_group_page_fragment,
                R.id.login_page_fragment,
                R.id.onboarding_page_fragment -> {
                    window.statusBarColor = getColor(R.color.light_gray)
                    drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
                    appBarLayout.apply {
                        setExpanded(false, false)
                        val lp = layoutParams as CoordinatorLayout.LayoutParams
                        lp.height = 0
                        layoutParams = lp
                        invalidate()
                    }
                }
                else -> {
                    window.statusBarColor = getColor(R.color.primary_dark)
                    drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED)
                    appBarLayout.apply {
                        val lp = layoutParams as CoordinatorLayout.LayoutParams
                        lp.height = 140.dp
                        layoutParams = lp
                        invalidate()
                        setExpanded(true, true)
                    }
                }
            }
        }
    }

    override fun onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawers()
        } else {
            when (navController.currentDestination?.id) {
                R.id.overview_page_fragment -> {
                    if (doubleBackToExitPressedOnce) {
                        super.onBackPressed()
                        return
                    }
                    doubleBackToExitPressedOnce = true
                    Toasty.info(this, getString(R.string.double_tap_to_exit), Toast.LENGTH_SHORT).show()
                    Handler().postDelayed({ doubleBackToExitPressedOnce = false }, 2000)
                }
                R.id.onboarding_page_fragment -> return
                else -> super.onBackPressed()
            }
        }
    }

    override fun onSupportNavigateUp() = navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
}