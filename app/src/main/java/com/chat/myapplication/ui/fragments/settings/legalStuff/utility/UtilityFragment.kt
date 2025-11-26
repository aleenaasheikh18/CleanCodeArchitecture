package com.chat.myapplication.ui.fragments.settings.legalStuff.utility

import android.annotation.SuppressLint
import android.webkit.WebViewClient
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.chat.myapplication.R
import com.chat.myapplication.base.BaseFragment
import com.chat.myapplication.core.data.settings.model.PolicyData
import com.chat.myapplication.core.domain.State
import com.chat.myapplication.databinding.FragmentUtilityBinding
import com.chat.myapplication.ui.fragments.settings.SettingType
import com.chat.myapplication.utility.AppConstants
import com.chat.myapplication.utility.AppConstants.SCREEN_TYPE
import com.chat.myapplication.utility.showToast
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class UtilityFragment : BaseFragment<FragmentUtilityBinding>(FragmentUtilityBinding::inflate) {

    private val viewModel: LegalStuffViewModel by viewModels()
    private var screenType: String = ""

    override fun initUserInterface() {
        screenType = arguments?.getString(SCREEN_TYPE).orEmpty()
        setupToolbarTitle()
        setupWebView()
        initApiObserver()
        bi.webView.loadUrl("file:///android_asset/placeholder.html")
        viewModel.getPolicyDocuments()
    }

    private fun setupToolbarTitle() {
        val titleRes = when (screenType) {
            SettingType.PRIVACY_POLICY.name -> R.string.policy
            SettingType.TERMS_SERVICE.name -> R.string.terms_service
            else -> return
        }
        (requireActivity() as? AppCompatActivity)?.supportActionBar?.title = getString(titleRes)
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun setupWebView() {
        bi.webView.apply {
            settings.javaScriptEnabled = true
            settings.domStorageEnabled = true
            settings.loadWithOverviewMode = true
            settings.useWideViewPort = true
            settings.builtInZoomControls = true
            settings.displayZoomControls = false
            settings.setSupportZoom(true)
            settings.cacheMode = android.webkit.WebSettings.LOAD_DEFAULT
            settings.databaseEnabled = true
            webViewClient = WebViewClient()
            setInitialScale(1)
        }
    }

    private fun initApiObserver() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.utilityResponse.collectLatest { state ->
                    when (state) {
                        is State.Loading -> Unit
                        is State.Success -> {
                            handleData(state.data.policyData)
                        }
                        is State.Error -> {
                            showInfoDialog(description = state.message)
                        }
                        else -> Unit
                    }
                }
            }
        }
    }

    private fun handleData(data: PolicyData) {
        val fileUrl = when (screenType) {
            SettingType.PRIVACY_POLICY.name -> AppConstants.IMAGE_URL + data.eaterPolicy.orEmpty()
            SettingType.TERMS_SERVICE.name -> AppConstants.IMAGE_URL + data.eaterTerms.orEmpty()
            else -> ""
        }

        if (fileUrl.isNotEmpty()) {
            bi.webView.loadUrl(AppConstants.GOOGLE_DOCS_VIEWER_URL + fileUrl)
        } else {
            showToast(context, getString(R.string.unable_to_load))
        }
    }
}