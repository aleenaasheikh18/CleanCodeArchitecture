package com.chat.myapplication.ui.fragments.settings

import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.chat.myapplication.R
import com.chat.myapplication.base.BaseFragment
import com.chat.myapplication.core.data.settings.SettingItem
import com.chat.myapplication.databinding.FragmentSettingsBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SettingsFragment : BaseFragment<FragmentSettingsBinding>(FragmentSettingsBinding::inflate) {

    private val viewModel: SettingsViewModel by viewModels()

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
            SettingType.PAYMENT -> {

            }
            SettingType.ACCOUNT_SECURITY -> {
                findNavController().navigate(R.id.moveToAccountSecurity)
            }
            SettingType.NOTIFICATIONS -> {
                findNavController().navigate(R.id.moveToNotification)
            }
            SettingType.LEGAL_STUFF -> {
                findNavController().navigate(R.id.moveToLegalStuff)
            }

            else -> {}
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
    }

}