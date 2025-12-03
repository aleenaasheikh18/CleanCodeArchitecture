package com.chat.myapplication.ui.wizard.youGotMail

import android.content.Intent
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.chat.myapplication.base.BaseBottomSheetDialogFragment
import com.chat.myapplication.core.domain.State
import com.chat.myapplication.databinding.LayoutWizardYouGotMailBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class YouGotMailFragment : BaseBottomSheetDialogFragment<LayoutWizardYouGotMailBinding>(
    LayoutWizardYouGotMailBinding::inflate
) {

    private val viewModel: YouGotMailViewModel by viewModels()

    private var userEmail: String = ""

    override fun initUserInterface() {
        userEmail = arguments?.getString(ARG_EMAIL) ?: ""

        // Set email in ViewModel for resend functionality
        viewModel.setEmail(userEmail)

        setupUI()
        setupClickListeners()
        initObservers()
    }

    private fun setupUI() {
        // Set title with user's name
        val userName = preferenceManager.firstName
        bi.tvTitle.text = getString(com.chat.myapplication.R.string.you_got_mail_title, userName)

        // Update the email detail text to include the user's email using string resource
        bi.tvEmailDetail.text = getString(com.chat.myapplication.R.string.email_verification_detail, userEmail)
    }

    private fun setupClickListeners() {
        bi.tvResendMagicLink.setOnClickListener {
            // Reset the text back to original before resending
            resetResendLinkText()
            viewModel.resendLoginLink()
        }

        bi.btnOpenMailApp.setOnClickListener {
            openEmailApp()
        }
    }

    private fun openEmailApp() {
        try {
            val intent = Intent(Intent.ACTION_MAIN).apply {
                addCategory(Intent.CATEGORY_APP_EMAIL)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            startActivity(intent)
            dismiss()
        } catch (_: Exception) {
            // If no email app found, show error
            showInfoDialog(description = "No email app found")
        }
    }

    private fun initObservers() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.resendState.collectLatest { state ->
                        when (state) {
                            is State.Loading -> {
                                showProgressBar()
                            }
                            is State.Success -> {
                                hideProgressBar()
                                showEmailSentSuccess()
                            }
                            is State.Error -> {
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

    private fun showEmailSentSuccess() {
        bi.tvResendMagicLink.apply {
            text = "✓ Email sent successfully!"
            setTextColor(ContextCompat.getColor(requireContext(), com.chat.myapplication.R.color.colorGreen))
            isEnabled = true
        }
    }

    private fun resetResendLinkText() {
        bi.tvResendMagicLink.apply {
            text = getString(com.chat.myapplication.R.string.resend_magic_link)
            setTextColor(ContextCompat.getColor(requireContext(), com.chat.myapplication.R.color.colorBlue))
        }
    }

    companion object {
        private const val ARG_EMAIL = "arg_email"

        fun newInstance(email: String) = YouGotMailFragment().apply {
            arguments = android.os.Bundle().apply {
                putString(ARG_EMAIL, email)
            }
        }
    }
}
