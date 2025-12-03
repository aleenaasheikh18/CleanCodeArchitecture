package com.chat.myapplication.ui.auth

import android.content.Intent
import android.media.MediaPlayer
import android.util.DisplayMetrics
import android.util.Log
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.RelativeLayout
import androidx.activity.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.chat.myapplication.R
import com.chat.myapplication.base.BaseActivity
import com.chat.myapplication.core.data.auth.model.SignInRequest
import com.chat.myapplication.core.deeplink.DeepLinkEvent
import com.chat.myapplication.core.deeplink.DeepLinkHandler
import com.chat.myapplication.core.domain.State
import com.chat.myapplication.databinding.ActivityLauncherBinding
import com.chat.myapplication.ui.dashboard.HomeActivity
import com.chat.myapplication.ui.fragments.settings.SettingType
import com.chat.myapplication.ui.legal.LegalDocumentActivity
import com.chat.myapplication.ui.wizard.onboarding.WizardBottomSheetFragment
import com.chat.myapplication.ui.wizard.youGotMail.YouGotMailFragment
import com.chat.myapplication.utility.AppConstants
import com.chat.myapplication.utility.AppConstants.SCREEN_TYPE
import com.chat.myapplication.utility.setOnSingleClickListener
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class LauncherScreenActivity : BaseActivity<ActivityLauncherBinding>(ActivityLauncherBinding::inflate),Animation.AnimationListener {

    private val viewModel: LauncherViewModel by viewModels()

    @Inject
    lateinit var deepLinkHandler: DeepLinkHandler

    private var pendingDeepLinkEvent: DeepLinkEvent = DeepLinkEvent.None
    private var currentWizard: WizardBottomSheetFragment? = null
    private var hasNavigatedFromDeepLink = false

    override fun initUserInterface() {
        initApiObserver()
        initSplashAnimations()
        initClickListener()
    }

    override fun onStart() {
        super.onStart()
        deepLinkHandler.initBranchSession(this) { event ->
            handleDeepLinkEvent(event)
        }
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

    private fun handleDeepLinkEvent(event: DeepLinkEvent) {
        when (event) {
            is DeepLinkEvent.ReferralCode -> {
                AppConstants.REFERRAL_CODE = event.code
                pendingDeepLinkEvent = DeepLinkEvent.None
            }
            is DeepLinkEvent.InfluencerJob -> {
                preferenceManager.influencerJobId = event.jobId
                pendingDeepLinkEvent = DeepLinkEvent.None
            }
            is DeepLinkEvent.LoginToken -> {
                // Verify login token - this will navigate on success
                viewModel.verifyLoginToken(event.token)
                pendingDeepLinkEvent = DeepLinkEvent.None
                hasNavigatedFromDeepLink = true
            }
            is DeepLinkEvent.VerifyEmailChange -> {
                // Email change verification requires user to be logged in
                if (preferenceManager.isLoggedIn) {
                    // Navigate immediately to Home with the event
                    hasNavigatedFromDeepLink = true
                    navigateToHomeWithEvent(event)
                } else {
                    // User must be logged in to verify email change
                    showInfoDialog(description = "Please login first to verify your email change")
                }
            }
            is DeepLinkEvent.ResetPassword -> {
                // Password reset - navigate immediately if logged in
                if (preferenceManager.isLoggedIn) {
                    hasNavigatedFromDeepLink = true
                    navigateToHomeWithEvent(event)
                } else {
                    // Store event to pass after login
                    pendingDeepLinkEvent = event
                }
            }
            is DeepLinkEvent.VerifyAccount -> {
                // Verify account token and login on success
                viewModel.verifyAccountToken(event.token)
                hasNavigatedFromDeepLink = true
            }
            is DeepLinkEvent.None -> {
                pendingDeepLinkEvent = DeepLinkEvent.None
            }
            else -> {
                pendingDeepLinkEvent = event
            }
        }
    }

    private fun initClickListener() {
        bi.btnLogin.setOnSingleClickListener {
            showWizardBottomSheet()
        }
    }

    private fun showWizardBottomSheet() {
        val wizard = WizardBottomSheetFragment.newInstance()
        currentWizard = wizard

        wizard.onWizardComplete = { email, password ->
            // Trim email and password before sending to API
            viewModel.signIn(SignInRequest(email.trim(), password.trim()))
        }
        wizard.onLoginSuccess = {
            // Login successful, navigate to home
            currentWizard = null
            navigateToHome()
        }
        wizard.onPasskeyLoginSuccess = { signInData ->
            // Passkey login successful, navigate to home
            currentWizard = null
            navigateToHome()
        }
        wizard.onGoogleLoginSuccess = { signInData ->
            // Google login successful, navigate to home
            currentWizard = null
            navigateToHome()
        }
        wizard.onRegistrationComplete = { email ->
            // Show You Got Mail screen after registration
            currentWizard = null
            showYouGotMailScreen(email)
        }
        wizard.onShowYouGotMail = { email ->
            // Show You Got Mail screen for password setup
            currentWizard = null
            showYouGotMailScreen(email)
        }
        wizard.onWizardCancelled = {
            currentWizard = null
        }
        wizard.onTermsClick = {
            openLegalDocument(SettingType.TERMS_SERVICE)
        }
        wizard.onPrivacyClick = {
            openLegalDocument(SettingType.PRIVACY_POLICY)
        }
        wizard.show(supportFragmentManager, WizardBottomSheetFragment.TAG)
    }

    private fun showYouGotMailScreen(email: String) {
        val sheet = YouGotMailFragment.newInstance(email)
        sheet.show(supportFragmentManager, "YouGotMailFragment")
    }

    private fun openLegalDocument(type: SettingType) {
        val intent = Intent(this, LegalDocumentActivity::class.java).apply {
            putExtra(SCREEN_TYPE, type.name)
        }
        startActivity(intent)
    }

    private fun initSplashAnimations() {
        val logoAnim = AnimationUtils.loadAnimation(this, R.anim.splash_animation)
        bi.ivSplash.startAnimation(logoAnim)

        lifecycleScope.launch {
            delay(SPLASH_DELAY)
            // Check if we already navigated from a deep link
            if (hasNavigatedFromDeepLink) {
                return@launch
            }
            if (preferenceManager.isLoggedIn) {
                navigateToHome()
            } else {
                showLoginScreen()
            }
        }
    }

    private fun showLoginScreen() {
        val logoAnim = AnimationUtils.loadAnimation(this, R.anim.splash_animation1)
        bi.ivSplash.animation = logoAnim
        logoAnim.setAnimationListener(this)
        startVideo()
    }

    private fun navigateToHome() {
        val intent = Intent(this, HomeActivity::class.java).apply {
            if (pendingDeepLinkEvent != DeepLinkEvent.None) {
                putExtra(DeepLinkEvent.EXTRA_DEEP_LINK_EVENT, pendingDeepLinkEvent)
            }
        }
        startActivity(intent)
        finish()
    }

    private fun navigateToHomeWithEvent(event: DeepLinkEvent) {
        val intent = Intent(this, HomeActivity::class.java).apply {
            putExtra(DeepLinkEvent.EXTRA_DEEP_LINK_EVENT, event)
        }
        startActivity(intent)
        finish()
    }

    private fun startVideo() {
        try {
            val videoPath = "android.resource://" + packageName + "/" + R.raw.eater_video
            bi.videoView.setVideoPath(videoPath)
            bi.videoView.setOnPreparedListener { obj: MediaPlayer? -> obj!!.start() }
            bi.videoView.setOnCompletionListener { obj: MediaPlayer? -> obj!!.start() }
            val metrics = DisplayMetrics()
            windowManager.defaultDisplay.getMetrics(metrics)
            val params = bi.videoView.layoutParams as RelativeLayout.LayoutParams
            params.width = metrics.widthPixels
            params.height = metrics.heightPixels
            params.leftMargin = 0
            params.rightMargin = 0
            params.bottomMargin = 0
            params.topMargin = 0
            bi.videoView.layoutParams = params
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun initApiObserver(){

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.signInResponse.collectLatest { state ->
                        when (state) {
                            is State.Loading -> currentWizard?.setLoginLoading(true)
                            is State.Success -> {
                                currentWizard?.onLoginSuccessful()
                            }
                            is State.Error -> {
                                Log.d("LauncherActivity", "signInResponse State.Error - message='${state.message}'")
                                currentWizard?.setLoginError(state.message)
                            }
                            else -> Unit
                        }
                    }
                }

                launch {
                    viewModel.verifyTokenResponse.collectLatest { state ->
                        when (state) {
                            is State.Loading -> showProgressBar()
                            is State.Success -> {
                                hideProgressBar()
                                navigateToHome()
                            }
                            is State.Error -> {
                                Log.d("LauncherActivity", "verifyTokenResponse State.Error - message='${state.message}'")
                                hideProgressBar()
                                showInfoDialog(description = state.message)
                            }
                            else -> Unit
                        }
                    }
                }

                launch {
                    viewModel.verifyAccountResponse.collectLatest { state ->
                        when (state) {
                            is State.Loading -> showProgressBar()
                            is State.Success -> {
                                hideProgressBar()
                                navigateToHome()
                            }
                            is State.Error -> {
                                Log.d("LauncherActivity", "verifyAccountResponse State.Error - message='${state.message}'")
                                hideProgressBar()
                                showInfoDialog(description = state.message)
                            }
                            else -> Unit
                        }
                    }
                }
            }
        }
    }

    override fun onAnimationEnd(animation: Animation?) {
        bi.relativeLayout.visibility = View.VISIBLE
        bi.llSplash.visibility = View.GONE
    }

    override fun onAnimationRepeat(animation: Animation?) = Unit

    override fun onAnimationStart(animation: Animation?) = Unit

    companion object {
        private const val SPLASH_DELAY = 1000L
    }

}