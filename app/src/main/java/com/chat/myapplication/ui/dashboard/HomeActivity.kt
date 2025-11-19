package com.chat.myapplication.ui.dashboard

import MultiDividerDecoration
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import androidx.recyclerview.widget.RecyclerView
import com.chat.myapplication.R
import com.chat.myapplication.base.BaseActivity
import com.chat.myapplication.databinding.ActivityHomeBinding
import com.chat.myapplication.databinding.DrawerHeaderBinding
import com.chat.myapplication.utility.setBadgeCount
import com.chat.myapplication.utility.setOnSingleClickListener

class HomeActivity : BaseActivity<ActivityHomeBinding>(ActivityHomeBinding::inflate) {

    private lateinit var navController: NavController
    private lateinit var appBarConfiguration: AppBarConfiguration

    override fun initUserInterface() {
        setSupportActionBar(bi.toolbar)
        setupNavigation()
    }

    private fun setupNavigation() {
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController

        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.homeFragment,
                R.id.myOrdersFragment,
                R.id.settingsFragment
            ),
            bi.drawerLayout
        )

        setSupportActionBar(bi.toolbar)
        setupActionBarWithNavController(navController, appBarConfiguration)
        bi.navigationView.setupWithNavController(navController)

        val menuView = bi.navigationView.getChildAt(0) as RecyclerView
        val dividerDrawable = ContextCompat.getDrawable(this, R.drawable.drawer_divider)!!
        fun dpToPx(dp: Int) = (dp * resources.displayMetrics.density).toInt()

        menuView.addItemDecoration(
            MultiDividerDecoration(
                drawable = dividerDrawable,
                positions = listOf(3, 4, 5),
                spaceAbove = dpToPx(8),
                spaceBelow = dpToPx(8)
            )
        )

        bi.navigationView.setBadgeCount(R.id.nav_messages, 100)
        bi.navigationView.setBadgeCount(R.id.nav_bonus, getString(R.string.new_))

        handleDrawerDetails()
        bi.navigationView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.homeFragment -> {
                    navController.navigate(R.id.homeFragment)
                    true
                }
                R.id.myOrdersFragment -> {
                    navController.navigate(R.id.myOrdersFragment)
                    true
                }
                R.id.settingsFragment -> {
                    navController.navigate(R.id.settingsFragment)
                    true
                }
                R.id.nav_messages -> {
                    navController.navigate(R.id.messagesFragment)
                    true
                }
                R.id.nav_bonus -> {

                    true
                }
                else -> false
            }.also {
                // Close drawer after click
                bi.drawerLayout.closeDrawer(GravityCompat.START)
            }
        }

    }

    private fun handleDrawerDetails(){

        val headerView = bi.navigationView.getHeaderView(0)
        val headerBi = DrawerHeaderBinding.bind(headerView)
        headerBi.imgProfile.setOnSingleClickListener {
            bi.drawerLayout.closeDrawer(GravityCompat.START)
            navController.navigate(R.id.profileFragment)
        }

    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }


}