package com.chat.myapplication.ui.wizard.onboarding

import com.chat.myapplication.R
import com.chat.myapplication.ui.wizard.HeaderConfig

sealed class WizardStep(
    val position: Int,
    val headerConfig: HeaderConfig
) {
    data object Selection : WizardStep(
        position = 0,
        headerConfig = HeaderConfig(
            titleRes = R.string.choose_option,
            showClose = true,
            showHeader = true
        )
    )

    data object Email : WizardStep(
        position = 1,
        headerConfig = HeaderConfig(
            titleRes = R.string.wizard_title_enter_email,
            showBack = true,
            showClose = true,
            showHeader = true
        )
    )

    data object Password : WizardStep(
        position = 2,
        headerConfig = HeaderConfig(
            titleRes = R.string.wizard_title_enter_password,
            showBack = true,
            showClose = true,
            showHeader = true
        )
    )

    companion object {
        val entries: List<WizardStep> = listOf(Selection, Email, Password)

        fun fromPosition(position: Int): WizardStep =
            entries.getOrElse(position) { Selection }
    }
}
