package com.chat.myapplication.base

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.Window
import android.view.WindowManager
import com.chat.myapplication.databinding.DialogLoaderBinding
import com.chat.myapplication.utility.TAG

class CustomProgressDialog(context: Context) : Dialog(context) {

    private val binding: DialogLoaderBinding
    private val handler = Handler(Looper.getMainLooper())
    private val autoDismissRunnable = Runnable {
        hideProgressDialog()
    }

    init {
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        binding = DialogLoaderBinding.inflate(LayoutInflater.from(context))
        setContentView(binding.root)
        window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        window?.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT)
        setCancelable(false)
    }

    override fun dismiss() {
        super.dismiss()
        handler.removeCallbacks(autoDismissRunnable) // Ensure the runnable is removed when dialog is dismissed
    }

    fun showProgressDialog() {
        try {
            if (!isShowing) {
                show()
                handler.postDelayed(autoDismissRunnable, 60000) // Schedule auto-dismiss after 60 seconds
            }
        } catch (exp: Exception) {
            Log.e(TAG, exp.message.orEmpty())
        }
    }

    fun hideProgressDialog() {
        try {
            if (isShowing) {
                dismiss()
                handler.removeCallbacks(autoDismissRunnable) // Cancel auto-dismiss if dialog is manually dismissed
            }
        } catch (exp: Exception) {
            Log.e(TAG, exp.message.orEmpty())
        }
    }
}
