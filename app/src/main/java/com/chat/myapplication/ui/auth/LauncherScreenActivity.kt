package com.chat.myapplication.ui.auth

import android.media.MediaPlayer
import android.os.Handler
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
import com.chat.myapplication.databinding.ActivityLauncherBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import com.chat.myapplication.core.domain.State
import com.chat.myapplication.utility.TAG
import com.chat.myapplication.utility.setOnSingleClickListener

@AndroidEntryPoint
class LauncherScreenActivity : BaseActivity<ActivityLauncherBinding>(ActivityLauncherBinding::inflate),Animation.AnimationListener {

    private val viewModel: LauncherViewModel by viewModels()

    override fun initUserInterface() {

        initApiObserver()
        val email = "abc@gmail.com"
        val password = "12345"

        initSplashAnimations()
        //viewModel.signIn(SignInRequest(email, password))

    }


    private fun initSplashAnimations(){
        val logoAnim = AnimationUtils.loadAnimation(this, R.anim.splash_animation)
        viewDataBinding.ivSplash.startAnimation(logoAnim)

        val splashScreenTime = 1000
        Handler().postDelayed({
            val logoAnim1 = AnimationUtils.loadAnimation(this, R.anim.splash_animation1)
            viewDataBinding.ivSplash.animation = logoAnim1
            logoAnim1.setAnimationListener(this)
            startVideo()
        }, splashScreenTime.toLong())

    }

    private fun startVideo() {
        try {
            val videoPath = "android.resource://" + packageName + "/" + R.raw.eater_video
            viewDataBinding.videoView.setVideoPath(videoPath)
            viewDataBinding.videoView.setOnPreparedListener { obj: MediaPlayer? -> obj!!.start() }
            viewDataBinding.videoView.setOnCompletionListener { obj: MediaPlayer? -> obj!!.start() }
            val metrics = DisplayMetrics()
            windowManager.defaultDisplay.getMetrics(metrics)
            val params = viewDataBinding.videoView.layoutParams as RelativeLayout.LayoutParams
            params.width = metrics.widthPixels
            params.height = metrics.heightPixels
            params.leftMargin = 0
            params.rightMargin = 0
            params.bottomMargin = 0
            params.topMargin = 0
            viewDataBinding.videoView.layoutParams = params
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun initApiObserver(){

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.signInResponse.collectLatest { state ->
                    when (state) {
                        is State.Loading -> showProgressBar()
                        is State.Success -> {
                            hideProgressBar()
                        }
                        is State.Error -> {
                            showInfoDialog(description = state.message)
                        }
                    }
                }
            }
        }
    }

    override fun onAnimationEnd(animation: Animation?) {
        viewDataBinding.relativeLayout.visibility = View.VISIBLE
        viewDataBinding.llSplash.visibility = View.GONE
    }

    override fun onAnimationRepeat(animation: Animation?) = Unit

    override fun onAnimationStart(animation: Animation?) = Unit

}