package com.chat.myapplication.base

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.annotation.StringRes
import androidx.annotation.StyleRes
import androidx.viewbinding.ViewBinding
import com.chat.myapplication.R
import com.chat.myapplication.components.DialogManager
import com.chat.myapplication.utility.PreferenceManager
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import javax.inject.Inject

typealias InflateBottomSheet<VB> = (LayoutInflater, ViewGroup?, Boolean) -> VB

abstract class BaseBottomSheetDialogFragment<VB : ViewBinding>(
    private val inflate: InflateBottomSheet<VB>
) : BottomSheetDialogFragment() {

    private var _binding: VB? = null
    protected val bi get() = _binding!!

    private var progressDialog: CustomProgressDialog? = null

    @Inject
    lateinit var dialogManager: DialogManager

    @Inject
    lateinit var preferenceManager: PreferenceManager

    @StyleRes
    protected open fun getBottomSheetTheme(): Int = R.style.BaseBottomSheetDialog

    override fun getTheme(): Int = getBottomSheetTheme()

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState) as BottomSheetDialog
        dialog.setOnShowListener { dialogInterface ->
            val bottomSheetDialog = dialogInterface as BottomSheetDialog
            val bottomSheet = bottomSheetDialog.findViewById<FrameLayout>(
                com.google.android.material.R.id.design_bottom_sheet
            )
            bottomSheet?.let {
                val behavior = BottomSheetBehavior.from(it)
                setupBottomSheetBehavior(behavior)
            }
        }
        return dialog
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = inflate(inflater, container, false)
        progressDialog = CustomProgressDialog(requireContext())
        return bi.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        initUserInterface()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    protected open fun setupBottomSheetBehavior(behavior: BottomSheetBehavior<FrameLayout>) {
        behavior.skipCollapsed = true
        behavior.state = BottomSheetBehavior.STATE_EXPANDED
    }

    open fun showProgressBar() = progressDialog?.showProgressDialog()

    open fun hideProgressBar() = progressDialog?.hideProgressDialog()

    open fun showInfoDialog(
        title: String = getString(R.string.str_alert),
        description: String,
        @StringRes buttonResource: Int = R.string.str_ok,
        @StringRes extraButtonResource: Int = 0,
        onPositiveButtonClick: () -> Unit = {}
    ) {
        hideProgressBar()
        dialogManager.showDialog(
            context = requireContext(),
            positiveButtonStringResource = buttonResource,
            extraButtonStringResource = extraButtonResource,
            titleStringResource = title,
            descriptionStringResource = description,
            onPositiveButtonClick = { onPositiveButtonClick.invoke() }
        )
    }

    protected abstract fun initUserInterface()
}
