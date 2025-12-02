package com.chat.myapplication.ui.wizard.onboarding

import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.chat.myapplication.R
import com.chat.myapplication.base.BaseBottomSheetDialogFragment
import com.chat.myapplication.core.data.auth.model.SignInData
import com.chat.myapplication.databinding.BottomSheetWizardBinding
import com.chat.myapplication.databinding.LayoutWizardEmailBinding
import com.chat.myapplication.databinding.LayoutWizardHeaderBinding
import com.chat.myapplication.databinding.LayoutWizardPasswordBinding
import com.chat.myapplication.databinding.LayoutWizardSelectionBinding
import com.chat.myapplication.utility.ClickableText
import com.chat.myapplication.utility.SimpleTextWatcher
import com.chat.myapplication.utility.setClickableText
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class WizardBottomSheetFragment : BaseBottomSheetDialogFragment<BottomSheetWizardBinding>(
    BottomSheetWizardBinding::inflate
) {

    private val headerBinding: LayoutWizardHeaderBinding get() =
        LayoutWizardHeaderBinding.bind(bi.layoutHeader.root)
    private val selectionBinding: LayoutWizardSelectionBinding get() =
        LayoutWizardSelectionBinding.bind(bi.layoutSelection.root)
    private val emailBinding: LayoutWizardEmailBinding get() =
        LayoutWizardEmailBinding.bind(bi.layoutEmail.root)
    private val passwordBinding: LayoutWizardPasswordBinding get() =
        LayoutWizardPasswordBinding.bind(bi.layoutPassword.root)

    private val viewModel: WizardViewModel by viewModels()

    private var isPasswordVisible = false

    var onWizardComplete: ((email: String, password: String) -> Unit)? = null
    var onWizardCancelled: (() -> Unit)? = null
    var onTermsClick: (() -> Unit)? = null
    var onPrivacyClick: (() -> Unit)? = null
    var onPasskeyLoginSuccess: ((SignInData) -> Unit)? = null

    override fun initUserInterface() {
        setupHeader()
        setupSelectionScreen()
        setupEmailScreen()
        setupPasswordScreen()
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

    private fun updateHeader(step: WizardStep) {
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

    private fun setupSelectionScreen() {
        with(selectionBinding) {
            llGoogle.setOnClickListener {
                viewModel.setSelectedOption(1)
                // Handle Google sign in
            }
            llPasskey.setOnClickListener {
                viewModel.setSelectedOption(2)
                viewModel.authenticateWithPasskey(requireActivity())
            }
            btnLogin.setOnClickListener {
                viewModel.setSelectedOption(3)
                viewModel.goToEmailStep()
            }
            setupTermsText()
        }
    }

    private fun setupTermsText() {
        val termsText = getString(R.string.terms)
        val privacyText = getString(R.string.policy)
        val fullText = "${getString(R.string.by_continuing)} $termsText ${getString(R.string.and)} $privacyText"
        val linkColor = ContextCompat.getColor(requireContext(), R.color.defaultColorPrimary)

        selectionBinding.tvTerms.setClickableText(
            fullText = fullText,
            clickableTexts = listOf(
                ClickableText(
                    text = termsText,
                    color = linkColor,
                    underline = true,
                    onClick = { onTermsClick?.invoke() }
                ),
                ClickableText(
                    text = privacyText,
                    color = linkColor,
                    underline = true,
                    onClick = { onPrivacyClick?.invoke() }
                )
            )
        )
    }

    private fun setupEmailScreen() {
        with(emailBinding) {
            etEmail.addTextChangedListener(SimpleTextWatcher { text ->
                viewModel.setEmail(text)
                ivClearEmail.isVisible = text.isNotEmpty()
            })

            ivClearEmail.setOnClickListener {
                etEmail.text?.clear()
                viewModel.clearEmail()
            }

            btnContinueEmail.setOnClickListener {
                viewModel.goToPasswordStep()
            }
        }
    }

    private fun setupPasswordScreen() {
        with(passwordBinding) {
            etPassword.addTextChangedListener(SimpleTextWatcher { text ->
                viewModel.setPassword(text)
            })

            ivTogglePassword.setOnClickListener {
                isPasswordVisible = !isPasswordVisible
                if (isPasswordVisible) {
                    etPassword.transformationMethod = HideReturnsTransformationMethod.getInstance()
                    ivTogglePassword.setImageResource(R.drawable.ic_visibility)
                } else {
                    etPassword.transformationMethod = PasswordTransformationMethod.getInstance()
                    ivTogglePassword.setImageResource(R.drawable.ic_visibility_off)
                }
                etPassword.setSelection(etPassword.text?.length ?: 0)
            }

            btnSubmit.setOnClickListener {
                if (viewModel.submit()) {
                    onWizardComplete?.invoke(
                        viewModel.email.value,
                        viewModel.password.value
                    )
                    dismiss()
                }
            }
        }
    }

    private fun initObservers() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.currentStep.collectLatest { step ->
                        updateHeader(step)
                        animateToStep(step)
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
                    viewModel.passwordError.collectLatest { error ->
                        with(passwordBinding) {
                            tvPasswordError.isVisible = error != null
                            tvPasswordError.text = error
                            etPassword.setBackgroundResource(
                                if (error != null) R.drawable.bg_input_field_error
                                else R.drawable.bg_input_field
                            )
                        }
                    }
                }

                launch {
                    viewModel.passkeyLoginState.collectLatest { state ->
                        handlePasskeyLoginState(state)
                    }
                }
            }
        }
    }

    private fun handlePasskeyLoginState(state: PasskeyLoginState) {
        when (state) {
            is PasskeyLoginState.Idle -> {
                selectionBinding.llPasskey.isEnabled = true
            }
            is PasskeyLoginState.Loading -> {
                selectionBinding.llPasskey.isEnabled = false
            }
            is PasskeyLoginState.Success -> {
                selectionBinding.llPasskey.isEnabled = true
                onPasskeyLoginSuccess?.invoke(state.data)
                dismiss()
            }
            is PasskeyLoginState.Error -> {
                selectionBinding.llPasskey.isEnabled = true
                Toast.makeText(requireContext(), state.message, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun animateToStep(step: WizardStep) {
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
    }

    companion object {
        const val TAG = "WizardBottomSheetFragment"

        fun newInstance(): WizardBottomSheetFragment {
            return WizardBottomSheetFragment()
        }
    }
}
