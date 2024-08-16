package com.leon.patmore.life.efficiency.views

import android.view.View
import android.widget.Button

abstract class ActiveView(
    val view: View,
    val button: Button,
) {
    abstract fun onActive()
}
