package com.chat.myapplication.ui.wizard.resetpassword

import android.view.View
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.chat.myapplication.R
import com.chat.myapplication.base.BaseBottomSheetDialogFragment
import com.chat.myapplication.core.domain.State
import com.chat.myapplication.databinding.BottomSheetResetPasswordBinding
import com.chat.myapplication.databinding.LayoutWizardHeaderBinding
import com.chat.myapplication.databinding.LayoutWizardPhoneSuccessBinding
import com.chat.myapplication.databinding.LayoutWizardResetPasswordBinding
import com.chat.myapplication.utility.SimpleTextWatcher
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ResetPasswordBottomSheetFragment : BaseBottomSheetDialogFragment<BottomSheetResetPasswordBinding>(
    BottomSheetResetPasswordBinding::inflate
) {

    private val headerBinding: LayoutWizardHeaderBinding
        get() = LayoutWizardHeaderBinding.bind(bi.layoutHeader.root)

    private val resetPasswordBinding: LayoutWizardResetPasswordBinding
        get() = LayoutWizardResetPasswordBinding.bind(bi.layoutResetPassword.root)

    private val successBinding: LayoutWizardPhoneSuccessBinding
        get() = LayoutWizardPhoneSuccessBinding.bind(bi.layoutSuccess.root)

    private val viewModel: ResetPasswordViewModel by viewModels()

    var onPasswordReset: (() -> Unit)? = null

    override fun initUserInterface() {
        setupHeader()
        setupPasswordInputs()
        setupButton()
        setupSuccessScreen()
        initObservers()
    }

    private fun setupHeader() {
        with(headerBinding) {
            tvTitle.text = getString(R.string.reset_password)
            ivBack.visibility = View.GONE
            ivClose.visibility = View.VISIBLE
            ivClose.setOnClickListener {
                dismiss()
            }
        }
    }

    private fun setupPasswordInputs() {
        resetPasswordBinding.etNewPassword.addTextChangedListener(SimpleTextWatcher { text ->
            viewModel.setNewPassword(text)
        })

        resetPasswordBinding.etConfirmPassword.addTextChangedListener(SimpleTextWatcher { text ->
            viewModel.setConfirmPassword(text)
        })
    }

    private fun setupButton() {
        resetPasswordBinding.btnResetPassword.setOnClickListener {
            viewModel.resetPassword()
        }
    }

    private fun setupSuccessScreen() {
        successBinding.tvSuccessTitle.text = getString(R.string.password_reset_success_title)
        successBinding.tvSuccessDescription.text = getString(R.string.password_reset_success_description)

        successBinding.btnDone.setOnClickListener {
            onPasswordReset?.invoke()
            dismiss()
        }
    }

    private fun updateValidationIcon(hasCondition: Boolean, iconResId: Int) {
        val imageView = when (iconResId) {
            R.id.ivMinLength -> resetPasswordBinding.ivMinLength
            R.id.ivNumber -> resetPasswordBinding.ivNumber
            R.id.ivCapital -> resetPasswordBinding.ivCapital
            R.id.ivSymbol -> resetPasswordBinding.ivSymbol
            R.id.ivPasswordsMatch -> resetPasswordBinding.ivPasswordsMatch
            else -> return
        }

        if (hasCondition) {
            imageView.setImageResource(R.drawable.ic_tick)
            imageView.imageTintList = ContextCompat.getColorStateList(requireContext(), R.color.colorGreen)
        } else {
            imageView.setImageResource(R.drawable.ic_close)
            imageView.imageTintList = ContextCompat.getColorStateList(requireContext(), R.color.defaultColorPrimary)
        }
    }

    private fun goToSuccessScreen() {
        bi.viewFlipper.setInAnimation(requireContext(), R.anim.slide_in_right)
        bi.viewFlipper.setOutAnimation(requireContext(), R.anim.slide_out_left)
        bi.viewFlipper.displayedChild = 1

        // Hide header for success screen
        headerBinding.root.visibility = View.GONE
    }

    private fun initObservers() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.hasMinLength.collectLatest { hasMinLength ->
                        updateValidationIcon(hasMinLength, R.id.ivMinLength)
                    }
                }

                launch {
                    viewModel.hasNumber.collectLatest { hasNumber ->
                        updateValidationIcon(hasNumber, R.id.ivNumber)
                    }
                }

                launch {
                    viewModel.hasCapital.collectLatest { hasCapital ->
                        updateValidationIcon(hasCapital, R.id.ivCapital)
                    }
                }

                launch {
                    viewModel.hasSymbol.collectLatest { hasSymbol ->
                        updateValidationIcon(hasSymbol, R.id.ivSymbol)
                    }
                }

                launch {
                    viewModel.passwordsMatch.collectLatest { passwordsMatch ->
                        updateValidationIcon(passwordsMatch, R.id.ivPasswordsMatch)
                    }
                }

                launch {
                    viewModel.isFormValid.collectLatest { isValid ->
                        resetPasswordBinding.btnResetPassword.isEnabled = isValid
                        resetPasswordBinding.btnResetPassword.alpha = if (isValid) 1f else 0.5f
                    }
                }

                launch {
                    viewModel.resetPasswordResponse.collectLatest { state ->
                        when (state) {
                            is State.Loading -> showProgressBar()
                            is State.Success -> {
                                hideProgressBar()
                                goToSuccessScreen()
                            }
                            is State.Error -> {
                                hideProgressBar()
                                Toast.makeText(
                                    requireContext(),
                                    state.message,
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                            else -> Unit
                        }
                    }
                }
            }
        }
    }

    companion object {
        const val TAG = "ResetPasswordBottomSheetFragment"

        fun newInstance(): ResetPasswordBottomSheetFragment {
            return ResetPasswordBottomSheetFragment()
        }
    }
}
