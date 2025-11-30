package com.chat.myapplication.ui.dashboard

import MultiDividerDecoration
import android.content.Intent
import android.os.Build
import androidx.activity.OnBackPressedCallback
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.core.view.isVisible
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.NavController
import androidx.navigation.NavOptions
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.chat.myapplication.R
import com.chat.myapplication.base.BaseActivity
import com.chat.myapplication.core.deeplink.DeepLinkEvent
import com.chat.myapplication.core.deeplink.DeepLinkHandler
import com.chat.myapplication.core.domain.State
import com.chat.myapplication.databinding.ActivityHomeBinding
import com.chat.myapplication.databinding.DrawerHeaderBinding
import com.chat.myapplication.ui.wizard.emailverified.EmailVerifiedSuccessBottomSheetFragment
import com.chat.myapplication.ui.wizard.resetpassword.ResetPasswordBottomSheetFragment
import com.chat.myapplication.utility.AppConstants
import com.chat.myapplication.utility.setBadgeCount
import com.chat.myapplication.utility.setOnSingleClickListener
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class HomeActivity : BaseActivity<ActivityHomeBinding>(ActivityHomeBinding::inflate) {

    private lateinit var navController: NavController
    private lateinit var appBarConfiguration: AppBarConfiguration
    private val viewModel: HomeViewModel by viewModels()

    @Inject
    lateinit var deepLinkHandler: DeepLinkHandler

    override fun initUserInterface() {
        setSupportActionBar(bi.toolbar)
        setupNavigation()
        initObservers()
        handleDeepLinkFromIntent()
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        if (deepLinkHandler.shouldReInitSession(intent)) {
            deepLinkHandler.reInitBranchSession(this) { event ->
                handleDeepLinkEvent(event)
            }
        }
    }

    private fun handleDeepLinkFromIntent() {
        val event = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableExtra(DeepLinkEvent.EXTRA_DEEP_LINK_EVENT, DeepLinkEvent::class.java)
        } else {
            @Suppress("DEPRECATION")
            intent.getParcelableExtra(DeepLinkEvent.EXTRA_DEEP_LINK_EVENT)
        }
        event?.let { handleDeepLinkEvent(it) }
    }

    private fun handleDeepLinkEvent(event: DeepLinkEvent) {
        when (event) {
            is DeepLinkEvent.VerifyAccount -> {
                // TODO: Handle verify account API call
            }
            is DeepLinkEvent.ResetPassword -> {
                viewModel.verifyChangePasswordToken(event.token)
            }
            is DeepLinkEvent.VerifyEmailChange -> {
                viewModel.verifyEmailChange(event.token)
            }
            is DeepLinkEvent.ReferralCode,
            is DeepLinkEvent.InfluencerJob -> {
                // Already handled in LauncherScreenActivity
            }
            is DeepLinkEvent.Unknown -> {
                // Handle unknown deep links or log for future implementation
            }
            is DeepLinkEvent.None -> {
                // No deep link action needed
            }
        }
    }

    private fun setupNavigation() {

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController

        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.nav_home
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

        bi.navigationView.setBadgeCount(R.id.nav_messages, preferenceManager.unreadMessages)
        bi.navigationView.setBadgeCount(R.id.nav_bonus, getString(R.string.new_))

        handleDrawerDetails()

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (!navController.popBackStack()) {
                    finish()
                }
            }
        })

    }

    private fun handleDrawerDetails() {
        val headerView = bi.navigationView.getHeaderView(0)
        val headerBinding = DrawerHeaderBinding.bind(headerView)

        // Initial load
        updateDrawerHeader(headerBinding)

        // Update header when drawer opens (for dynamic changes)
        bi.drawerLayout.addDrawerListener(object : DrawerLayout.DrawerListener {
            override fun onDrawerSlide(drawerView: android.view.View, slideOffset: Float) {}
            override fun onDrawerClosed(drawerView: android.view.View) {}
            override fun onDrawerStateChanged(newState: Int) {}
            override fun onDrawerOpened(drawerView: android.view.View) {
                updateDrawerHeader(headerBinding)
            }
        })

        headerBinding.imgProfile.setOnSingleClickListener {
            bi.drawerLayout.closeDrawer(GravityCompat.START)
            navController.navigate(R.id.profileFragment)
        }
    }

    private fun updateDrawerHeader(headerBinding: DrawerHeaderBinding) {
        // Set user name
        val fullName = buildString {
            append(preferenceManager.firstName)
            if (preferenceManager.lastName.isNotEmpty()) {
                append(" ")
                append(preferenceManager.lastName)
            }
        }
        headerBinding.txtName.text = fullName.ifEmpty { getString(R.string.app_name) }

        // Set profile image
        val profileImage = preferenceManager.profileImage
        if (profileImage.isNotEmpty()) {
            val imageUrl = AppConstants.IMAGE_URL + profileImage
            Glide.with(this)
                .load(imageUrl)
                .placeholder(R.drawable.ic_avatar)
                .error(R.drawable.ic_avatar)
                .circleCrop()
                .into(headerBinding.imgProfile)
        }

        // Show/hide influencer badge
        headerBinding.ivInfluencer.isVisible = preferenceManager.customerType == CUSTOMER_TYPE_INFLUENCER
    }

    companion object {
        private const val CUSTOMER_TYPE_INFLUENCER = "influencer"
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    private fun initObservers() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.verifyEmailResponse.collectLatest { state ->
                        when (state) {
                            is State.Loading -> showProgressBar()
                            is State.Success -> {
                                hideProgressBar()
                                showEmailVerifiedSuccessBottomSheet()
                            }
                            is State.Error -> {
                                hideProgressBar()
                            }
                            else -> Unit
                        }
                    }
                }

                launch {
                    viewModel.verifyChangePasswordTokenResponse.collectLatest { state ->
                        when (state) {
                            is State.Loading -> showProgressBar()
                            is State.Success -> {
                                hideProgressBar()
                                showResetPasswordBottomSheet()
                            }
                            is State.Error -> {
                                hideProgressBar()
                            }
                            else -> Unit
                        }
                    }
                }
            }
        }
    }

    private fun showEmailVerifiedSuccessBottomSheet() {
        val bottomSheet = EmailVerifiedSuccessBottomSheetFragment.newInstance()
        bottomSheet.onContinueClick = {
            // Handle continue action if needed
        }
        bottomSheet.show(supportFragmentManager, EmailVerifiedSuccessBottomSheetFragment.TAG)
    }

    private fun showResetPasswordBottomSheet() {
        val bottomSheet = ResetPasswordBottomSheetFragment.newInstance()
        bottomSheet.onPasswordReset = {
            // Handle password reset success if needed
        }
        bottomSheet.show(supportFragmentManager, ResetPasswordBottomSheetFragment.TAG)
    }
}