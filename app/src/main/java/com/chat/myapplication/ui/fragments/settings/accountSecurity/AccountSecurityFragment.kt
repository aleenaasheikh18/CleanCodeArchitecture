package com.chat.myapplication.ui.fragments.settings.accountSecurity

import androidx.fragment.app.viewModels
import com.chat.myapplication.base.BaseFragment
import com.chat.myapplication.core.data.settings.SettingItem
import com.chat.myapplication.databinding.FragmentSettingsBinding
import com.chat.myapplication.ui.fragments.settings.SettingType
import com.chat.myapplication.ui.fragments.settings.SettingsAdapter
import com.chat.myapplication.ui.wizard.editemail.EditEmailBottomSheetFragment
import com.chat.myapplication.ui.wizard.editphone.EditPhoneBottomSheetFragment
import dagger.hilt.android.AndroidEntryPoint

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

            }

            SettingType.PASSWORD -> {

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

    override fun onDestroyView() {
        super.onDestroyView()
    }
}

