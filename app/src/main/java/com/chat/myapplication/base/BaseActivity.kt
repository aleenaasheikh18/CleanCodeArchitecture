package com.chat.myapplication.base

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.viewbinding.ViewBinding
import com.chat.myapplication.R
import com.chat.myapplication.components.DialogManager
import com.chat.myapplication.utility.PreferenceManager
import javax.inject.Inject

abstract class BaseActivity<VB : ViewBinding>(private val inflate: Inflate<VB>) :
    AppCompatActivity() {

    private var _binding: ViewBinding? = null

    private var progressDialog: CustomProgressDialog? = null
    open fun showProgressBar() = progressDialog?.showProgressDialog()

    open fun hideProgressBar() = progressDialog?.hideProgressDialog()

    @Inject
    lateinit var preferenceManager: PreferenceManager
    @Inject
    lateinit var dialogManager: DialogManager

    @Suppress("UNCHECKED_CAST")
    protected val bi: VB
        get() = _binding as VB

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = inflate.invoke(layoutInflater)
        setContentView(requireNotNull(_binding).root)

        progressDialog = CustomProgressDialog(this)
        /*_binding?.apply {
            setContentView(root)
            if ((this@BaseActivity is HomeActivity).not())
                root.configureEdgeToEdgePadding()
        }*/
        initUserInterface()
    }

    open fun showInfoDialog(
        titleMsg: String = getString(R.string.str_alert),
        description: String,
        buttonResource: Int = R.string.str_ok,
        extraButtonResource: Int = 0,
        onPositiveButtonClick: () -> Unit = {}
    ) {
        hideProgressBar()
       dialogManager.showDialog(
            this,
            positiveButtonStringResource = buttonResource,
            extraButtonStringResource = extraButtonResource,
            titleStringResource = titleMsg,
            descriptionStringResource = description,
            onPositiveButtonClick = {
                onPositiveButtonClick.invoke()
            }
        )
    }


    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    protected abstract fun initUserInterface()

    override fun recreate() {
        finish()
        intent.action = null
        startActivity(intent)
    }
}