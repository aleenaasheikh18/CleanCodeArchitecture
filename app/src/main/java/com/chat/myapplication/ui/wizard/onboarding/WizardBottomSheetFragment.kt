package com.chat.myapplication.ui.wizard.onboarding

import android.content.Intent
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
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
import com.chat.myapplication.databinding.LayoutWizardNameBinding
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
    private val nameBinding: LayoutWizardNameBinding get() =
        LayoutWizardNameBinding.bind(bi.layoutName.root)
    private val passwordBinding: LayoutWizardPasswordBinding get() =
        LayoutWizardPasswordBinding.bind(bi.layoutPassword.root)

    private val viewModel: WizardViewModel by viewModels()

    private var isPasswordVisible = false

    private lateinit var googleSignInLauncher: ActivityResultLauncher<Intent>

    var onWizardComplete: ((email: String, password: String) -> Unit)? = null
    var onWizardCancelled: (() -> Unit)? = null
    var onTermsClick: (() -> Unit)? = null
    var onPrivacyClick: (() -> Unit)? = null
    var onPasskeyLoginSuccess: ((SignInData) -> Unit)? = null
    var onGoogleLoginSuccess: ((SignInData) -> Unit)? = null
    var onRegistrationComplete: ((email: String) -> Unit)? = null
    var onShowYouGotMail: ((email: String) -> Unit)? = null
    var onLoginSuccess: (() -> Unit)? = null

    override fun onCreate(savedInstanceState: android.os.Bundle?) {
        super.onCreate(savedInstanceState)
        googleSignInLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            // Always pass the result to ViewModel, it will handle cancellation properly
            viewModel.handleGoogleSignInResult(result.data)
        }
    }

    override fun initUserInterface() {
        setupHeader()
        setupSelectionScreen()
        setupEmailScreen()
        setupNameScreen()
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
                // Suggestion 5: Disable button immediately for visual feedback
                llGoogle.isEnabled = false
                viewModel.prepareGoogleSignIn(requireActivity()) { signInIntent ->
                    googleSignInLauncher.launch(signInIntent)
                    // Re-enable will happen in state handler
                }
            }
            llPasskey.setOnClickListener {
                viewModel.authenticateWithPasskey(requireActivity())
            }
            btnLogin.setOnClickListener {
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

    private fun setupNameScreen() {
        with(nameBinding) {
            etFirstName.addTextChangedListener(SimpleTextWatcher { text ->
                viewModel.setFirstName(text)
                ivClearFirstName.isVisible = text.isNotEmpty()
            })

            etLastName.addTextChangedListener(SimpleTextWatcher { text ->
                viewModel.setLastName(text)
                ivClearLastName.isVisible = text.isNotEmpty()
            })

            ivClearFirstName.setOnClickListener {
                etFirstName.text?.clear()
                viewModel.setFirstName("")
            }

            ivClearLastName.setOnClickListener {
                etLastName.text?.clear()
                viewModel.setLastName("")
            }

            btnContinueName.setOnClickListener {
                viewModel.goToPasswordStepFromName()
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

                launch {
                    viewModel.firstNameError.collectLatest { error ->
                        with(nameBinding) {
                            tvFirstNameError.isVisible = error != null
                            tvFirstNameError.text = error
                            etFirstName.setBackgroundResource(
                                if (error != null) R.drawable.bg_input_field_error
                                else R.drawable.bg_input_field
                            )
                        }
                    }
                }

                launch {
                    viewModel.lastNameError.collectLatest { error ->
                        with(nameBinding) {
                            tvLastNameError.isVisible = error != null
                            tvLastNameError.text = error
                            etLastName.setBackgroundResource(
                                if (error != null) R.drawable.bg_input_field_error
                                else R.drawable.bg_input_field
                            )
                        }
                    }
                }

                launch {
                    viewModel.welcomeBackName.collectLatest { name ->
                        with(passwordBinding) {
                            tvTitle.text = if (name != null) {
                                "${getString(R.string.welcome_back)}, $name"
                            } else {
                                getString(R.string.wizard_title_enter_password)
                            }
                        }
                    }
                }

                launch {
                    viewModel.isLoading.collectLatest { isLoading ->
                        emailBinding.btnContinueEmail.isEnabled = !isLoading
                        emailBinding.btnContinueEmail.text = if (isLoading) "Loading..." else "Continue"

                        nameBinding.btnContinueName.isEnabled = !isLoading
                        nameBinding.btnContinueName.text = if (isLoading) "Loading..." else "Continue"
                    }
                }

                launch {
                    viewModel.googleLoginState.collectLatest { state ->
                        handleGoogleLoginState(state)
                    }
                }

                launch {
                    viewModel.registerState.collectLatest { state ->
                        when (state) {
                            is RegisterState.Success -> {
                                onRegistrationComplete?.invoke(state.email)
                                dismiss()
                            }
                            is RegisterState.Error -> {
                                Toast.makeText(requireContext(), state.message, Toast.LENGTH_LONG).show()
                            }
                        }
                    }
                }

                launch {
                    viewModel.emailValidationEvent.collectLatest { event ->
                        when (event) {
                            is EmailValidationEvent.ShowYouGotMail -> {
                                // Save firstName to PreferenceManager so YouGotMailFragment can display it
                                preferenceManager.firstName = event.firstName
                                onShowYouGotMail?.invoke(event.email)
                                dismiss()
                            }
                        }
                    }
                }
            }
        }
    }

    private fun handlePasskeyLoginState(state: PasskeyLoginState) {
        when (state) {
            is PasskeyLoginState.Idle -> {
                hideProgressBar()
                selectionBinding.llPasskey.isEnabled = true
            }
            is PasskeyLoginState.Loading -> {
                showProgressBar()
                selectionBinding.llPasskey.isEnabled = false
            }
            is PasskeyLoginState.Success -> {
                hideProgressBar()
                selectionBinding.llPasskey.isEnabled = true
                onPasskeyLoginSuccess?.invoke(state.data)
                dismiss()
            }
            is PasskeyLoginState.Error -> {
                hideProgressBar()
                selectionBinding.llPasskey.isEnabled = true
                // Show error dialog with retry option
                showInfoDialog(
                    title = getString(R.string.str_alert),
                    description = state.message,
                    buttonResource = R.string.str_ok,
                    extraButtonResource = R.string.str_retry,
                    onPositiveButtonClick = {
                        // Retry Passkey authentication
                        viewModel.authenticateWithPasskey(requireActivity())
                    }
                )
            }
        }
    }

    private fun handleGoogleLoginState(state: GoogleLoginState) {
        when (state) {
            is GoogleLoginState.Idle -> {
                hideProgressBar()
                selectionBinding.llGoogle.isEnabled = true
            }
            is GoogleLoginState.Loading -> {
                showProgressBar()
                selectionBinding.llGoogle.isEnabled = false
            }
            is GoogleLoginState.Success -> {
                hideProgressBar()
                selectionBinding.llGoogle.isEnabled = true
                onGoogleLoginSuccess?.invoke(state.data)
                dismiss()
            }
            is GoogleLoginState.Error -> {
                hideProgressBar()
                selectionBinding.llGoogle.isEnabled = true
                // Suggestion 4: Show error dialog with retry option
                showInfoDialog(
                    title = getString(R.string.str_alert),
                    description = state.message,
                    buttonResource = R.string.str_ok,
                    extraButtonResource = R.string.str_retry,
                    onPositiveButtonClick = {
                        // Retry Google Sign-In
                        selectionBinding.llGoogle.isEnabled = false
                        viewModel.prepareGoogleSignIn(requireActivity()) { signInIntent ->
                            googleSignInLauncher.launch(signInIntent)
                        }
                    }
                )
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

    fun setLoginError(error: String) {
        viewModel.setLoginError(error)
        setLoginLoading(false)
    }

    fun setLoginLoading(isLoading: Boolean) {
        passwordBinding.btnSubmit.isEnabled = !isLoading
        passwordBinding.btnSubmit.text = if (isLoading) "Loading..." else "Submit"
    }

    fun onLoginSuccessful() {
        onLoginSuccess?.invoke()
        dismiss()
    }

    companion object {
        const val TAG = "WizardBottomSheetFragment"

        fun newInstance(): WizardBottomSheetFragment {
            return WizardBottomSheetFragment()
        }
    }
}
