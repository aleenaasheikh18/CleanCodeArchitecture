package com.chat.myapplication.ui.fragments.settings.editEmail

import com.chat.myapplication.base.BaseFragment
import com.chat.myapplication.databinding.FragmentVerifyEmailBinding
import com.chat.myapplication.ui.fragments.settings.accountSecurity.StepActionListener
import com.chat.myapplication.utility.setOnSingleClickListener


class VerifyEmailFragment(private val listener: StepActionListener): BaseFragment<FragmentVerifyEmailBinding>(FragmentVerifyEmailBinding::inflate) {

    override fun initUserInterface() {
       initClickListeners()
    }

    private fun initClickListeners(){
        bi.btnOpenMailApp.setOnSingleClickListener {
            listener.onFinishFlow()
        }
    }

}