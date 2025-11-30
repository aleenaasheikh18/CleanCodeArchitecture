package com.chat.myapplication.ui.wizard.editemail

import com.chat.myapplication.ui.wizard.HeaderConfig

sealed class EditEmailWizardStep(
    val position: Int,
    val headerConfig: HeaderConfig
) {
    data object Email : EditEmailWizardStep(
        position = 0,
        headerConfig = HeaderConfig()
    )

    data object YouGotMail : EditEmailWizardStep(
        position = 1,
        headerConfig = HeaderConfig(showBack = true)
    )
}
