package com.chat.myapplication.ui.wizard.editphone

import android.widget.EditText
import android.widget.TextView
import androidx.annotation.StringRes
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.chat.myapplication.R
import com.chat.myapplication.base.BaseBottomSheetDialogFragment
import com.chat.myapplication.core.data.auth.model.CountryArea
import com.chat.myapplication.databinding.BottomSheetEditPhoneBinding
import com.chat.myapplication.databinding.LayoutWizardHeaderBinding
import com.chat.myapplication.databinding.LayoutWizardPhoneBinding
import com.chat.myapplication.databinding.LayoutWizardPhoneSuccessBinding
import com.chat.myapplication.utility.SimpleTextWatcher
import com.chat.myapplication.utility.glide.loadSvg
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class EditPhoneBottomSheetFragment : BaseBottomSheetDialogFragment<BottomSheetEditPhoneBinding>(
    BottomSheetEditPhoneBinding::inflate
) {

    private val headerBinding: LayoutWizardHeaderBinding
        get() = LayoutWizardHeaderBinding.bind(bi.layoutHeader.root)

    private val phoneBinding: LayoutWizardPhoneBinding
        get() = LayoutWizardPhoneBinding.bind(bi.layoutPhone.root)

    private val successBinding: LayoutWizardPhoneSuccessBinding
        get() = LayoutWizardPhoneSuccessBinding.bind(bi.layoutSuccess.root)

    private val viewModel: EditPhoneViewModel by viewModels()

    var onPhoneVerified: (() -> Unit)? = null
    var onWizardCancelled: (() -> Unit)? = null

    companion object {
        const val TAG = "EditPhoneBottomSheetFragment"
        private const val DISABLED_ALPHA = 0.5f

        fun newInstance(): EditPhoneBottomSheetFragment {
            return EditPhoneBottomSheetFragment()
        }
    }

    override fun initUserInterface() {
        setupHeader()
        setupPhoneScreen()
        setupSuccessScreen()
        initObservers()
    }

    private fun setupHeader() {
        with(headerBinding) {
            ivBack.setOnClickListener { dismissWithCallback() }
            ivClose.setOnClickListener { dismissWithCallback() }
        }
    }

    private fun dismissWithCallback() {
        onWizardCancelled?.invoke()
        dismiss()
    }

    private fun updateHeader(step: EditPhoneWizardStep) {
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

    private fun setupPhoneScreen() {
        with(phoneBinding) {
            layoutCountrySelector.setOnClickListener { showCountrySelector() }

            etPhoneNumber.addTextChangedListener(SimpleTextWatcher { text ->
                viewModel.setPhoneNumber(text)
                ivClearPhone.isVisible = text.isNotEmpty()
            })

            ivClearPhone.setOnClickListener {
                etPhoneNumber.text?.clear()
                viewModel.clearPhoneNumber()
            }

            etOtpCode.addTextChangedListener(SimpleTextWatcher { text ->
                viewModel.setOtpCode(text)
                ivClearOtp.isVisible = text.isNotEmpty()
            })

            ivClearOtp.setOnClickListener {
                etOtpCode.text?.clear()
                viewModel.clearOtpCode()
            }

            tvResendOtp.setOnClickListener { viewModel.resendOtp() }

            btnSubmit.setOnClickListener {
                val state = viewModel.uiState.value
                if (state.isOtpVisible) {
                    viewModel.verifyPhoneNumber()
                } else {
                    viewModel.addPhoneNumber()
                }
            }
        }
    }

    private fun setupSuccessScreen() {
        successBinding.btnDone.setOnClickListener {
            onPhoneVerified?.invoke()
            dismiss()
        }
    }

    private fun showCountrySelector() {
        val countries = viewModel.uiState.value.countries
        val countrySheet = CountrySelectionBottomSheetFragment.newInstance(countries)
        countrySheet.onCountrySelected = { country -> setSelectedCountry(country) }
        countrySheet.show(childFragmentManager, CountrySelectionBottomSheetFragment.TAG)
    }

    private fun setSelectedCountry(country: CountryArea) {
        viewModel.setSelectedCountry(country)
    }

    private fun showOtpSection(fullPhoneNumber: String) {
        with(phoneBinding) {
            tvTitle.text = getString(R.string.verify_phone_title)
            layoutOtpSection.isVisible = true
            tvOtpDescription.text = getString(R.string.otp_sent_description, fullPhoneNumber)
            btnSubmit.text = getString(R.string.verify)

            // Disable phone input
            setViewEnabled(layoutCountrySelector, false)
            setViewEnabled(layoutPhoneInput, false)
            etPhoneNumber.isEnabled = false

            // Focus OTP field
            etOtpCode.requestFocus()
        }
    }

    private fun setViewEnabled(view: android.view.View, enabled: Boolean) {
        view.isEnabled = enabled
        view.alpha = if (enabled) 1f else DISABLED_ALPHA
    }

    private fun updateFieldError(
        errorTextView: TextView,
        editText: EditText,
        @StringRes errorRes: Int?
    ) {
        errorTextView.isVisible = errorRes != null
        errorTextView.text = errorRes?.let { getString(it) }
        editText.setBackgroundResource(
            if (errorRes != null) R.drawable.bg_input_field_error
            else R.drawable.bg_input_field
        )
    }

    private fun updateResendButton(cooldown: Int) {
        with(phoneBinding.tvResendOtp) {
            if (cooldown > 0) {
                text = getString(R.string.resend_otp) + " ($cooldown)"
                isEnabled = false
                alpha = DISABLED_ALPHA
            } else {
                text = getString(R.string.resend_otp)
                isEnabled = true
                alpha = 1f
            }
        }
    }

    private fun showResendSuccess() {
        val tickDrawable = ContextCompat.getDrawable(requireContext(), R.drawable.ic_tick)
        phoneBinding.tvResendOtp.apply {
            setCompoundDrawablesWithIntrinsicBounds(tickDrawable, null, null, null)
            compoundDrawablePadding = resources.getDimensionPixelSize(R.dimen.new_dimen_8_dp)
        }
        Snackbar.make(bi.root, R.string.resend_otp_success, Snackbar.LENGTH_SHORT).show()
    }

    private fun showError(message: String) {
        Snackbar.make(bi.root, message, Snackbar.LENGTH_LONG).show()
    }

    private fun initObservers() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch { observeUiState() }
                launch { observeEvents() }
            }
        }
    }

    private suspend fun observeUiState() {
        viewModel.uiState.collectLatest { state ->
            // Update step
            updateHeader(state.currentStep)
            animateToStep(state.currentStep)

            // Update loading
            if (state.isLoading) showProgressBar() else hideProgressBar()

            // Update country loading
            updateCountryLoading(state.isCountriesLoading)

            // Update selected country
            state.selectedCountry?.let { updateCountryDisplay(it) }

            // Update phone error
            updateFieldError(
                phoneBinding.tvPhoneError,
                phoneBinding.etPhoneNumber,
                state.phoneError
            )

            // Update OTP error
            updateFieldError(
                phoneBinding.tvOtpError,
                phoneBinding.etOtpCode,
                state.otpError
            )

            // Update OTP visibility
            if (state.isOtpVisible && !phoneBinding.layoutOtpSection.isVisible) {
                showOtpSection(state.fullPhoneNumber)
            }

            // Update resend cooldown
            updateResendButton(state.resendCooldown)
        }
    }

    private fun updateCountryLoading(isLoading: Boolean) {
        with(phoneBinding) {
            progressCountry.isVisible = isLoading
            layoutCountryContent.isVisible = !isLoading
            layoutCountrySelector.isEnabled = !isLoading
        }
    }

    private fun updateCountryDisplay(country: CountryArea) {
        with(phoneBinding) {
            ivCountryFlag.loadSvg(country.flag)
            tvCountryCode.text = country.countryCode.orEmpty()
        }
    }

    private suspend fun observeEvents() {
        viewModel.events.collectLatest { event ->
            when (event) {
                is EditPhoneEvent.PhoneAdded -> { /* Handled by state */ }
                is EditPhoneEvent.PhoneVerified -> { /* Handled by state */ }
                is EditPhoneEvent.ResendSuccess -> showResendSuccess()
                is EditPhoneEvent.Error -> showError(event.message)
            }
        }
    }

    private fun animateToStep(step: EditPhoneWizardStep) {
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
}
