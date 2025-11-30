package com.chat.myapplication.ui.wizard

import androidx.annotation.StringRes

data class HeaderConfig(
    @StringRes val titleRes: Int = 0,
    val showBack: Boolean = false,
    val showClose: Boolean = true,
    val showHeader: Boolean = true
)
