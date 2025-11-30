package com.chat.myapplication

import android.app.Application
import dagger.hilt.android.HiltAndroidApp
import io.branch.referral.Branch

@HiltAndroidApp
class MyApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        if (BuildConfig.DEBUG) {
            Branch.enableLogging()
        }
        Branch.getAutoInstance(this)
    }
}