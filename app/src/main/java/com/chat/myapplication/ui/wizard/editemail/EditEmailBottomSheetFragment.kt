package com.chat.myapplication.ui.wizard.editemail

import android.content.Intent
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.chat.myapplication.R
import com.chat.myapplication.base.BaseBottomSheetDialogFragment
import com.chat.myapplication.databinding.BottomSheetEditEmailBinding
import com.chat.myapplication.databinding.LayoutWizardEmailBinding
import com.chat.myapplication.databinding.LayoutWizardHeaderBinding
import com.chat.myapplication.databinding.LayoutWizardYouGotMailBinding
import com.chat.myapplication.core.domain.State
import com.chat.myapplication.utility.SimpleTextWatcher
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class EditEmailBottomSheetFragment : BaseBottomSheetDialogFragment<BottomSheetEditEmailBinding>(
    BottomSheetEditEmailBinding::inflate
) {

    private val headerBinding: LayoutWizardHeaderBinding
        get() =
            LayoutWizardHeaderBinding.bind(bi.layoutHeader.root)
    private val emailBinding: LayoutWizardEmailBinding
        get() =
            LayoutWizardEmailBinding.bind(bi.layoutEmail.root)
    private val youGotMailBinding: LayoutWizardYouGotMailBinding
        get() =
            LayoutWizardYouGotMailBinding.bind(bi.layoutYouGotMail.root)

    private val viewModel: EditEmailViewModel by viewModels()

    var onEmailSubmit: ((email: String) -> Unit)? = null
    var onResendEmail: ((email: String) -> Unit)? = null
    var onWizardCancelled: (() -> Unit)? = null

    override fun initUserInterface() {
        setupHeader()
        setupEmailScreen()
        setupYouGotMailScreen()
        initObservers()
    }

    private fun setupHeader() {
        with(headerBinding) {
            ivBack.setOnClickListener {
                viewModel.goBack()
            }
            ivClose.setOnClickListener {
                onWizardCancelled?.invoke()
                dismiss()
            }
        }
    }

    private fun updateHeader(step: EditEmailWizardStep) {
        val config = step.headerConfig
        with(headerBinding) {
            root.isVisible = config.showHeader
            if (config.titleRes != 0) {
                tvTitle.text = getString(config.titleRes)
            }
            ivBack.isVisible = config.showBack
            ivClose.isVisible = config.showClose
        }
        bi.viewDivider.isVisible = config.showHeader
    }

    private fun setupEmailScreen() {
        with(emailBinding) {
            // Pre-populate with current email from SharedPreferences
            val currentEmail = preferenceManager.email
            if (currentEmail.isNotEmpty()) {
                etEmail.setText(currentEmail)
                viewModel.setEmail(currentEmail)
                ivClearEmail.isVisible = true
            }

            etEmail.addTextChangedListener(SimpleTextWatcher { text ->
                viewModel.setEmail(text)
                ivClearEmail.isVisible = text.isNotEmpty()
            })

            ivClearEmail.setOnClickListener {
                etEmail.text?.clear()
                viewModel.clearEmail()
            }

            btnContinueEmail.setOnClickListener {
                viewModel.changeEmail()
            }
        }
    }

    private fun setupYouGotMailScreen() {
        with(youGotMailBinding) {
            btnOpenMailApp.setOnClickListener {
                openEmailApp()
            }

            tvResendMagicLink.setOnClickListener {
                viewModel.resendEmail()
                onResendEmail?.invoke(viewModel.email.value)
            }
        }
    }

    private fun updateYouGotMailContent(email: String) {
        // Ensure we have a valid email before updating
        if (email.isEmpty()) return

        with(youGotMailBinding) {
            val userName = preferenceManager.firstName
            tvTitle.text = getString(R.string.you_got_mail_title, userName)
            tvEmailDetail.text = getString(R.string.email_verification_detail, email)
        }
    }

    private fun showResendSuccess() {
        val tickDrawable = ContextCompat.getDrawable(requireContext(), R.drawable.ic_tick)
        youGotMailBinding.tvResendMagicLink.apply {
            setCompoundDrawablesWithIntrinsicBounds(tickDrawable, null, null, null)
            compoundDrawablePadding = resources.getDimensionPixelSize(R.dimen.new_dimen_8_dp)
        }
    }

    private fun openEmailApp() {
        val intent = Intent(Intent.ACTION_MAIN).apply {
            addCategory(Intent.CATEGORY_APP_EMAIL)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        try {
            startActivity(intent)
        } catch (e: Exception) {
            // No email app found
        }
    }

    fun goToYouGotMailStep() {
        viewModel.goToYouGotMailStep()
    }

    private fun showEmailError(message: String) {
        with(emailBinding) {
            tvEmailError.isVisible = true
            tvEmailError.text = message
            etEmail.setBackgroundResource(R.drawable.bg_input_field_error)
        }
    }

    private fun initObservers() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.currentStep.collectLatest { step ->
                        updateHeader(step)
                        animateToStep(step)
                        // Update You Got Mail screen content when navigating to it
                        if (step is EditEmailWizardStep.YouGotMail) {
                            updateYouGotMailContent(viewModel.email.value)
                        }
                    }
                }

                launch {
                    viewModel.email.collectLatest { email ->
                        // Update You Got Mail screen content whenever email changes
                        if (viewModel.currentStep.value is EditEmailWizardStep.YouGotMail) {
                            updateYouGotMailContent(email)
                        }
                    }
                }

                launch {
                    viewModel.emailError.collectLatest { error ->
                        with(emailBinding) {
                            tvEmailError.isVisible = error != null
                            tvEmailError.text = error
                            etEmail.setBackgroundResource(
                                if (error != null) R.drawable.bg_input_field_error
                                else R.drawable.bg_input_field
                            )
                        }
                    }
                }

                launch {
                    viewModel.changeEmailResponse.collectLatest { state ->
                        when (state) {
                            is State.Loading -> showProgressBar()
                            is State.Success -> {
                                hideProgressBar()
                                onEmailSubmit?.invoke(viewModel.email.value)
                            }
                            is State.Error -> {
                                hideProgressBar()
                                showEmailError(state.message)
                            }
                            else -> Unit
                        }
                    }
                }

                launch {
                    viewModel.resendSuccess.collectLatest { success ->
                        if (success) {
                            showResendSuccess()
                        }
                    }
                }

                launch {
                    viewModel.isLoading.collectLatest { isLoading ->
                        if (isLoading) {
                            showProgressBar()
                        } else {
                            hideProgressBar()
                        }
                    }
                }
            }
        }
    }

    private fun animateToStep(step: EditEmailWizardStep) {
        val targetChild = step.position
        val currentChild = bi.viewFlipper.displayedChild

        if (targetChild == currentChild) return

        if (targetChild > currentChild) {
            bi.viewFlipper.setInAnimation(requireContext(), R.anim.slide_in_right)
            bi.viewFlipper.setOutAnimation(requireContext(), R.anim.slide_out_left)
        } else {
            bi.viewFlipper.setInAnimation(requireContext(), R.anim.slide_in_left)
            bi.viewFlipper.setOutAnimation(requireContext(), R.anim.slide_out_right)
        }

        bi.viewFlipper.displayedChild = targetChild

        // Update You Got Mail content after animation completes
        if (step is EditEmailWizardStep.YouGotMail) {
            bi.viewFlipper.post {
                val email = viewModel.email.value
                if (email.isNotEmpty()) {
                    updateYouGotMailContent(email)
                }
            }
        }
    }

    companion object {
        const val TAG = "EditEmailBottomSheetFragment"

        fun newInstance(): EditEmailBottomSheetFragment {
            return EditEmailBottomSheetFragment()
        }
    }
}
