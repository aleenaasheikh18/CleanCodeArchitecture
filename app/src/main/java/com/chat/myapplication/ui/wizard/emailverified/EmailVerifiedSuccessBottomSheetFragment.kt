package com.chat.myapplication.ui.wizard.emailverified

import com.chat.myapplication.R
import com.chat.myapplication.base.BaseBottomSheetDialogFragment
import com.chat.myapplication.databinding.BottomSheetEmailVerifiedSuccessBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class EmailVerifiedSuccessBottomSheetFragment : BaseBottomSheetDialogFragment<BottomSheetEmailVerifiedSuccessBinding>(
    BottomSheetEmailVerifiedSuccessBinding::inflate
) {

    var onContinueClick: (() -> Unit)? = null

    override fun initUserInterface() {
        bi.apply {
            tvEmailDetail.text = getString(R.string.email_success_detail,preferenceManager.email)
            btnContinue.setOnClickListener {
                onContinueClick?.invoke()
                dismiss()
            }
        }
    }

    companion object {
        const val TAG = "EmailVerifiedSuccessBottomSheetFragment"

        fun newInstance(): EmailVerifiedSuccessBottomSheetFragment {
            return EmailVerifiedSuccessBottomSheetFragment()
        }
    }
}
