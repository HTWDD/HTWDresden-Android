package de.htwdd.htwdresden.ui.views.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.onNavDestinationSelected
import androidx.navigation.ui.setupWithNavController
import de.htwdd.htwdresden.R
import kotlinx.android.synthetic.main.activity_main_layout.*


class MainActivity: AppCompatActivity() {


    // region - Properties
    private val appBarConfiguration: AppBarConfiguration by lazy { AppBarConfiguration(navController.graph, drawerLayout) }
    private val navController: NavController by lazy { findNavController(R.id.navigationHost) }
    // endregion

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_layout)
        setupNavigation()
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
    }

    override fun onSupportNavigateUp() = navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
}