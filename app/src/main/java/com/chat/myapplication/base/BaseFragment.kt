package com.chat.myapplication.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.StringRes
import androidx.fragment.app.Fragment
import androidx.viewbinding.ViewBinding
import com.chat.myapplication.R
import com.chat.myapplication.components.DialogManager
import com.chat.myapplication.utility.PreferenceManager
import javax.inject.Inject

typealias InflateFragment<VB> = (LayoutInflater, ViewGroup?, Boolean) -> VB

abstract class BaseFragment<VB : ViewBinding>(
    private val inflate: InflateFragment<VB>
) : Fragment() {

    private var _binding: VB? = null
    protected val bi get() = _binding!!

    private var progressDialog: CustomProgressDialog? = null

    @Inject
    lateinit var dialogManager: DialogManager

    @Inject
    lateinit var preferenceManager: PreferenceManager

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
