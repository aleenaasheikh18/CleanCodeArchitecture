package com.chat.myapplication.ui.auth

import android.util.Log
import androidx.activity.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.chat.myapplication.base.BaseActivity
import com.chat.myapplication.core.data.auth.model.SignInRequest
import com.chat.myapplication.databinding.ActivityLauncherBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import com.chat.myapplication.core.domain.State
import com.chat.myapplication.utility.TAG

@AndroidEntryPoint
class LauncherScreenActivity : BaseActivity<ActivityLauncherBinding>(ActivityLauncherBinding::inflate) {

    private val viewModel: LauncherViewModel by viewModels()

    override fun initUserInterface() {

        initApiObserver()
        val email = "abc@gmail.com"
        val password = "12345"

        viewModel.signIn(SignInRequest(email, password))

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
}