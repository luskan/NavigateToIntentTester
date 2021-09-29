package com.androidauto.navigatetointenttester

import android.content.Intent
import androidx.car.app.Session

class NavigateToSession: Session() {
    override fun onCreateScreen(intent: Intent) = NavigateToScreen(carContext)
}