package com.androidauto.navigatetointenttester

import android.content.Context
import android.content.Intent

interface NavigateToScreenCallback {
    fun context(): Context
    fun invalidateList()
    fun startCarApp(intent: Intent)
    fun showToast(s: String)
}
