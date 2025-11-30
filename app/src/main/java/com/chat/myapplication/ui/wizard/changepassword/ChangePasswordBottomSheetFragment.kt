package com.chat.myapplication.ui.wizard.changepassword

import android.content.Intent
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
import com.chat.myapplication.databinding.BottomSheetChangePasswordBinding
import com.chat.myapplication.databinding.LayoutWizardChangePasswordBinding
import com.chat.myapplication.databinding.LayoutWizardHeaderBinding
import com.chat.myapplication.databinding.LayoutWizardYouGotMailBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ChangePasswordBottomSheetFragment : BaseBottomSheetDialogFragment<BottomSheetChangePasswordBinding>(
    BottomSheetChangePasswordBinding::inflate
) {

    private val headerBinding: LayoutWizardHeaderBinding
        get() = LayoutWizardHeaderBinding.bind(bi.layoutHeader.root)

    private val changePasswordBinding: LayoutWizardChangePasswordBinding
        get() = LayoutWizardChangePasswordBinding.bind(bi.layoutChangePassword.root)

    private val youGotMailBinding: LayoutWizardYouGotMailBinding
        get() = LayoutWizardYouGotMailBinding.bind(bi.layoutYouGotMail.root)

    private val viewModel: ChangePasswordViewModel by viewModels()

    var onPasswordChanged: (() -> Unit)? = null

    override fun initUserInterface() {
        setupHeader()
        setupChangePasswordScreen()
        setupYouGotMailScreen()
        initObservers()
    }

    private fun setupHeader() {
        with(headerBinding) {
            tvTitle.text = getString(R.string.change_your_password)
            ivBack.visibility = View.GONE
            ivClose.visibility = View.VISIBLE
            ivClose.setOnClickListener {
                dismiss()
            }
        }
    }

    private fun setupChangePasswordScreen() {
        changePasswordBinding.btnChangePassword.setOnClickListener {
            viewModel.changePasswordRequest()
        }
    }

    private fun setupYouGotMailScreen() {
        val userName = preferenceManager.firstName
        youGotMailBinding.tvTitle.text = getString(R.string.you_got_mail_title, userName)

        youGotMailBinding.btnOpenMailApp.setOnClickListener {
            openEmailApp()
        }

        youGotMailBinding.tvResendMagicLink.setOnClickListener {
            viewModel.resendChangePasswordRequest()
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

    private fun showResendSuccess() {
        val tickDrawable = ContextCompat.getDrawable(requireContext(), R.drawable.ic_tick)
        youGotMailBinding.tvResendMagicLink.apply {
            setCompoundDrawablesWithIntrinsicBounds(tickDrawable, null, null, null)
            compoundDrawablePadding = resources.getDimensionPixelSize(R.dimen.new_dimen_8_dp)
        }
    }

    private fun goToYouGotMailScreen() {
        bi.viewFlipper.setInAnimation(requireContext(), R.anim.slide_in_right)
        bi.viewFlipper.setOutAnimation(requireContext(), R.anim.slide_out_left)
        bi.viewFlipper.displayedChild = 1

        // Hide header for second screen (title is in the layout)
        headerBinding.root.visibility = View.GONE
    }

    private fun initObservers() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.changePasswordResponse.collectLatest { state ->
                        when (state) {
                            is State.Loading -> showProgressBar()
                            is State.Success -> {
                                hideProgressBar()
                                goToYouGotMailScreen()
                                onPasswordChanged?.invoke()
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

                launch {
                    viewModel.resendSuccess.collectLatest { success ->
                        if (success) {
                            showResendSuccess()
                        }
                    }
                }
            }
        }
    }

    companion object {
        const val TAG = "ChangePasswordBottomSheetFragment"

        fun newInstance(): ChangePasswordBottomSheetFragment {
            return ChangePasswordBottomSheetFragment()
        }
    }
}
