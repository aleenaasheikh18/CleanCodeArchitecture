package com.chat.myapplication.ui.fragments.settings.legalStuff

import android.os.Bundle
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.chat.myapplication.R
import com.chat.myapplication.base.BaseFragment
import com.chat.myapplication.core.data.settings.SettingItem
import com.chat.myapplication.databinding.FragmentLegalStuffBinding
import com.chat.myapplication.ui.fragments.settings.SettingType
import com.chat.myapplication.ui.fragments.settings.SettingsAdapter
import com.chat.myapplication.utility.AppConstants.SCREEN_TYPE
import dagger.hilt.android.AndroidEntryPoint
import kotlin.getValue

@AndroidEntryPoint
class LegalStuffFragment :
    BaseFragment<FragmentLegalStuffBinding>(FragmentLegalStuffBinding::inflate) {

    private val viewModel: LegalStuffDataViewModel by viewModels()

    override fun initUserInterface() {
        viewModel.settings.observe(viewLifecycleOwner) { list ->
            val adapter = SettingsAdapter(list) { item ->
                handleItemClick(item)
            }
            bi.rvLegalStuff.adapter = adapter
        }
    }

    private fun handleItemClick(item: SettingItem) {
        when (item.id) {
            SettingType.TERMS_SERVICE -> {
                val bundle = Bundle().apply {
                    putString(SCREEN_TYPE, item.id.name)
                }
                findNavController().navigate(R.id.moveToLegalDocuments, bundle)
            }

            SettingType.PRIVACY_POLICY -> {
                val bundle = Bundle().apply {
                    putString(SCREEN_TYPE, item.id.name)
                }
                findNavController().navigate(R.id.moveToLegalDocuments, bundle)
            }

            else -> {}
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
    }

}