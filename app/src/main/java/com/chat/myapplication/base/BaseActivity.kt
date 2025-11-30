package com.chat.myapplication.base

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.viewbinding.ViewBinding
import com.chat.myapplication.R
import com.chat.myapplication.components.DialogManager
import com.chat.myapplication.core.domain.SessionManager
import com.chat.myapplication.ui.auth.LauncherScreenActivity
import com.chat.myapplication.utility.PreferenceManager
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
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
    @Inject
    lateinit var sessionManager: SessionManager

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
        observeSessionExpiry()
        initUserInterface()
    }

    private fun observeSessionExpiry() {
        if (this is LauncherScreenActivity) return

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                sessionManager.sessionExpired.collectLatest {
                    navigateToLogin()
                }
            }
        }
    }

    private fun navigateToLogin() {
        val intent = Intent(this, LauncherScreenActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        startActivity(intent)
        finish()
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