package com.chat.myapplication.ui.wizard.editphone

import com.chat.myapplication.ui.wizard.HeaderConfig

sealed class EditPhoneWizardStep(
    val position: Int,
    val headerConfig: HeaderConfig
) {
    data object PhoneInput : EditPhoneWizardStep(
        position = 0,
        headerConfig = HeaderConfig()
    )

    data object Success : EditPhoneWizardStep(
        position = 1,
        headerConfig = HeaderConfig(showHeader = false)
    )
}
