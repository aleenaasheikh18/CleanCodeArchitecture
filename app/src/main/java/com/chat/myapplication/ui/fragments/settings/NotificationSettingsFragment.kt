package com.chat.myapplication.ui.fragments.settings

import android.widget.CheckBox
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.chat.myapplication.base.BaseFragment
import com.chat.myapplication.core.domain.State
import com.chat.myapplication.databinding.FragmentNotificationSettingsBinding
import com.chat.myapplication.utility.AppConstants
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class NotificationSettingsFragment : BaseFragment<FragmentNotificationSettingsBinding>(FragmentNotificationSettingsBinding::inflate) {

    private val viewModel: NotificationSettingsViewModel by viewModels()

    private var iAmHungry = ""
    private var message = ""
    private var receipt = ""
    private var isProductAnnouncement = false
    private var isAccountActivity = false
    private var isUpdatingUI = false

    override fun initUserInterface() {
        loadPreferences()
        initApiObserver()
        updateUI()
        initClickListeners()
    }

    private fun loadPreferences() {
        isProductAnnouncement = preferenceManager.isProductAnnouncementOn
        isAccountActivity = preferenceManager.isNotificationOn
        iAmHungry = preferenceManager.iAmHungry
        message = preferenceManager.message
        receipt = preferenceManager.receipt
    }

    private fun initClickListeners() {
        bi.apply {
            cbHPush.setOnClickListener { onSettingChanged { iAmHungry = AppConstants.PUSH } }
            cbHEmail.setOnClickListener { onSettingChanged { iAmHungry = AppConstants.EMAIL } }
            cbHBoth.setOnClickListener { onSettingChanged { iAmHungry = AppConstants.BOTH } }

            cbRPush.setOnClickListener { onSettingChanged { receipt = AppConstants.PUSH } }
            cbREmail.setOnClickListener { onSettingChanged { receipt = AppConstants.EMAIL } }
            cbRBoth.setOnClickListener { onSettingChanged { receipt = AppConstants.BOTH } }

            cbMPush.setOnClickListener { onSettingChanged { message = AppConstants.PUSH } }
            cbMEmail.setOnClickListener { onSettingChanged { message = AppConstants.EMAIL } }
            cbMBoth.setOnClickListener { onSettingChanged { message = AppConstants.BOTH } }

            swAccountActivity.setOnToggledListener { _, isOn ->
                if (!isUpdatingUI) onSettingChanged { isAccountActivity = isOn }
            }
            swProductAnnouncement.setOnToggledListener { _, isOn ->
                if (!isUpdatingUI) onSettingChanged { isProductAnnouncement = isOn }
            }
        }
    }

    private fun onSettingChanged(update: () -> Unit) {
        update()
        updateUI()
        callUpdateApi()
    }

    private fun updateUI() {
        isUpdatingUI = true
        bi.apply {
            swProductAnnouncement.setOn(isProductAnnouncement)
            swAccountActivity.setOn(isAccountActivity)

            updateCheckBoxGroup(iAmHungry, cbHBoth, cbHPush, cbHEmail)
            updateCheckBoxGroup(receipt, cbRBoth, cbRPush, cbREmail)
            updateCheckBoxGroup(message, cbMBoth, cbMPush, cbMEmail)
        }
        isUpdatingUI = false
    }

    private fun updateCheckBoxGroup(value: String, cbBoth: CheckBox, cbPush: CheckBox, cbEmail: CheckBox) {
        cbBoth.isChecked = value == AppConstants.BOTH
        cbPush.isChecked = value == AppConstants.PUSH
        cbEmail.isChecked = value == AppConstants.EMAIL
    }

    private fun callUpdateApi() {
        viewModel.updateNotificationSettings(
            notification = isAccountActivity,
            iAmHungry = iAmHungry,
            newMessage = message,
            receipt = receipt,
            productAnnouncement = isProductAnnouncement
        )
    }

    private fun initApiObserver() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.updateSettingsResponse.collectLatest { state ->
                    when (state) {
                        is State.Loading -> showProgressBar()
                        is State.Success -> {
                            hideProgressBar()
                            savePreferences()
                        }
                        is State.Error -> {
                            hideProgressBar()
                            revertToSavedPreferences()
                            showInfoDialog(description = state.message)
                        }
                        else -> Unit
                    }
                }
            }
        }
    }

    private fun savePreferences() {
        preferenceManager.isProductAnnouncementOn = isProductAnnouncement
        preferenceManager.isNotificationOn = isAccountActivity
        preferenceManager.iAmHungry = iAmHungry
        preferenceManager.message = message
        preferenceManager.receipt = receipt
    }

    private fun revertToSavedPreferences() {
        loadPreferences()
        updateUI()
    }
}