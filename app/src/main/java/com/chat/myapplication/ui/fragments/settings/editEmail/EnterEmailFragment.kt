package com.chat.myapplication.ui.fragments.settings.editEmail

import com.chat.myapplication.base.BaseFragment
import com.chat.myapplication.databinding.FragmentEnterEmailBinding
import com.chat.myapplication.ui.fragments.settings.accountSecurity.StepActionListener
import com.chat.myapplication.utility.setOnSingleClickListener

class EnterEmailFragment(private val listener: StepActionListener): BaseFragment<FragmentEnterEmailBinding>(FragmentEnterEmailBinding::inflate) {

    override fun initUserInterface() {
        initClickListeners()
    }

    private fun initClickListeners(){
        bi.btnSend.setOnSingleClickListener {
            listener.onNextStep()
        }
    }

}