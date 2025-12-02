package com.chat.myapplication.ui.fragments.settings.accountSecurity

import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.chat.myapplication.base.BaseFragment
import com.chat.myapplication.core.data.settings.SettingItem
import com.chat.myapplication.databinding.FragmentSettingsBinding
import com.chat.myapplication.ui.fragments.settings.SettingType
import com.chat.myapplication.ui.fragments.settings.SettingsAdapter
import com.chat.myapplication.ui.wizard.changepassword.ChangePasswordBottomSheetFragment
import com.chat.myapplication.ui.wizard.editemail.EditEmailBottomSheetFragment
import com.chat.myapplication.ui.wizard.editphone.EditPhoneBottomSheetFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class AccountSecurityFragment :
    BaseFragment<FragmentSettingsBinding>(FragmentSettingsBinding::inflate) {

    private val viewModel: AccountSecurityViewModel by viewModels()

    override fun initUserInterface() {
        viewModel.settings.observe(viewLifecycleOwner) { list ->
            val adapter = SettingsAdapter(list) { item ->
                handleItemClick(item)
            }
            bi.rvSettings.adapter = adapter
        }

        initObservers()
    }

    private fun initObservers() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.passkeyRegisterState.collectLatest { state ->
                    handlePasskeyRegisterState(state)
                }
            }
        }
    }

    private fun handlePasskeyRegisterState(state: PasskeyRegisterState) {
        when (state) {
            is PasskeyRegisterState.Idle -> {
                // Do nothing
            }
            is PasskeyRegisterState.Loading -> {
                // Show loading if needed
            }
            is PasskeyRegisterState.Success -> {
                Toast.makeText(
                    requireContext(),
                    "Passkey registered successfully",
                    Toast.LENGTH_SHORT
                ).show()
            }
            is PasskeyRegisterState.Error -> {
                Toast.makeText(requireContext(), state.message, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun handleItemClick(item: SettingItem) {
        when (item.id) {
            SettingType.VERIFIED_MOBILE -> {
                showEditPhoneWizard()
            }

            SettingType.VERIFIED_EMAIL -> {
                showEditEmailWizard()
            }

            SettingType.FINGERPRINT_AUTH -> {

            }

            SettingType.PASSKEY -> {
                viewModel.registerPasskey(requireActivity())
            }

            SettingType.PASSWORD -> {
                showChangePasswordWizard()
            }

            else -> {

            }
        }
    }

    private fun showEditEmailWizard() {
        val wizard = EditEmailBottomSheetFragment.newInstance()
        wizard.onEmailSubmit = { email ->
            wizard.goToYouGotMailStep()
        }
        wizard.onResendEmail = { email ->

        }
        wizard.show(parentFragmentManager, EditEmailBottomSheetFragment.TAG)
    }

    private fun showEditPhoneWizard() {
        val wizard = EditPhoneBottomSheetFragment.newInstance()
        wizard.onPhoneVerified = {
            // Handle phone verified - refresh settings if needed
        }
        wizard.show(parentFragmentManager, EditPhoneBottomSheetFragment.TAG)
    }

    private fun showChangePasswordWizard() {
        val wizard = ChangePasswordBottomSheetFragment.newInstance()
        wizard.show(parentFragmentManager, ChangePasswordBottomSheetFragment.TAG)
    }

    override fun onDestroyView() {
        super.onDestroyView()
    }
}

