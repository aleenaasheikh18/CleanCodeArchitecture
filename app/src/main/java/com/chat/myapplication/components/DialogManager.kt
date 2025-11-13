package com.chat.myapplication.components

import android.content.Context
import android.graphics.Color
import android.text.Html
import android.text.method.LinkMovementMethod
import android.util.Log
import android.view.LayoutInflater
import androidx.core.graphics.drawable.toDrawable
import androidx.core.view.isVisible
import com.chat.myapplication.R
import com.chat.myapplication.databinding.DialogBaseBottomSheetBinding
import com.chat.myapplication.utility.TAG
import com.chat.myapplication.utility.hide
import com.chat.myapplication.utility.setOnSingleClickListener
import com.chat.myapplication.utility.show
import com.google.android.material.bottomsheet.BottomSheetDialog
import javax.inject.Inject

class DialogManager @Inject constructor() {

    private var bottomSheetDialog: BottomSheetDialog? = null

    fun showDialog(
        context: Context,
        extraButtonStringResource: Int = 0,
        positiveButtonStringResource: Int = R.string.str_ok,
        alertTopMainImageResource: Int = R.drawable.ic_info,
        titleStringResource: String,
        descriptionStringResource: String = context.getString(R.string.cp_something_went_wrong),
        titleColorResource: Int = context.getColor(R.color.defaultTextColor),
        cancellable: Boolean = true,
        onExtraButtonClick: () -> Unit = {},
        onPositiveButtonClick: () -> Unit = {},
        isShowTitle: Boolean = false
    ) {
        try {
            if (bottomSheetDialog?.isShowing == true)
                bottomSheetDialog?.dismiss()

            val binding = DialogBaseBottomSheetBinding.inflate(LayoutInflater.from(context))
            bottomSheetDialog = BottomSheetDialog(context)

            bottomSheetDialog?.apply {
                window?.setBackgroundDrawable(Color.TRANSPARENT.toDrawable())
                setContentView(binding.root)
                setTitle(null)
                setCancelable(cancellable)
                setCanceledOnTouchOutside(cancellable)
                setOnCancelListener(null)

                binding.apply {
                    ivClose.isVisible = cancellable
                    tvTitle.isVisible = isShowTitle

                    if (extraButtonStringResource == 0) {
                        btnCancel.hide()
                    } else {
                        btnCancel.show()
                        btnCancel.setText(extraButtonStringResource)
                    }

                    if (positiveButtonStringResource == 0) {
                        btnConfirm.hide()
                    } else {
                        btnConfirm.show()
                        btnConfirm.setText(positiveButtonStringResource)
                    }

                    tvTitle.text = titleStringResource

                    tvMessage.text =
                        Html.fromHtml(descriptionStringResource, Html.FROM_HTML_MODE_COMPACT)
                    tvMessage.movementMethod = LinkMovementMethod.getInstance()

                    ivIcon.setImageResource(alertTopMainImageResource)
                    tvTitle.setTextColor(titleColorResource)
                    btnCancel.setOnSingleClickListener {
                        onExtraButtonClick()
                        dismiss()
                    }

                    btnConfirm.setOnSingleClickListener {
                        onPositiveButtonClick()
                        dismiss()
                    }

                    ivClose.setOnClickListener {
                        dismiss()
                    }
                }
            }
            bottomSheetDialog?.show()
        } catch (exp: Exception) {
            Log.e(TAG, exp.message.toString())
        }
    }
}