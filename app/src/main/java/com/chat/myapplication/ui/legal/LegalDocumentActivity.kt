package com.chat.myapplication.ui.legal

import android.os.Bundle
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.chat.myapplication.R
import com.chat.myapplication.base.BaseActivity
import com.chat.myapplication.databinding.ActivityLegalDocumentBinding
import com.chat.myapplication.utility.AppConstants.SCREEN_TYPE
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LegalDocumentActivity : BaseActivity<ActivityLegalDocumentBinding>(ActivityLegalDocumentBinding::inflate) {

    private lateinit var navController: NavController

    override fun initUserInterface() {
        setupNavigation()
        setupToolbar()
    }

    private fun setupNavigation() {
        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_legal) as NavHostFragment
        navController = navHostFragment.navController

        val screenType = intent.getStringExtra(SCREEN_TYPE).orEmpty()
        val bundle = Bundle().apply {
            putString(SCREEN_TYPE, screenType)
        }
        navController.setGraph(R.navigation.nav_legal, bundle)
    }

    private fun setupToolbar() {
        bi.toolbar.setNavigationOnClickListener {
            finish()
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp() || super.onSupportNavigateUp()
    }
}
