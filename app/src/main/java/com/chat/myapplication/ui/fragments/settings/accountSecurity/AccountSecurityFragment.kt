package com.chat.myapplication.ui.fragments.settings.accountSecurity

import androidx.fragment.app.viewModels
import com.chat.myapplication.base.BaseFragment
import com.chat.myapplication.base.StepsBottomSheet
import com.chat.myapplication.core.data.settings.SettingItem
import com.chat.myapplication.databinding.FragmentSettingsBinding
import com.chat.myapplication.ui.fragments.settings.SettingType
import com.chat.myapplication.ui.fragments.settings.SettingsAdapter
import com.chat.myapplication.ui.fragments.settings.editEmail.EmailVerifiedSuccessFragment
import com.chat.myapplication.ui.fragments.settings.editEmail.EnterEmailFragment
import com.chat.myapplication.ui.fragments.settings.editEmail.VerifyEmailFragment
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

            }

            SettingType.VERIFIED_EMAIL -> {
                val sheet = StepsBottomSheet(
                    onFinishClick = {
                    }
                )

                sheet.show(parentFragmentManager, "StepsBottomSheet")
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

    override fun onDestroyView() {
        super.onDestroyView()
    }

}

