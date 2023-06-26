package com.leon.patmore.life.efficiency.views

import android.view.View
import android.widget.Button

class MultiViewManager(private val menuView: View,
                       private val menuButton: Button,
                       private val viewToLayoutId: Map<String, ActiveView>) {

    init {
        setupButtons()
    }

    private fun setupButtons() {
        for (viewName in viewToLayoutId.keys) {
            val thisView = viewToLayoutId[viewName] ?: continue
            thisView.button.setOnClickListener { changeView(viewName) }
        }
        menuButton.setOnClickListener {
            for (activeView in viewToLayoutId.values) {
                activeView.view.visibility = View.GONE
            }
            menuView.visibility = View.VISIBLE
        }
    }

    private fun changeView(viewName: String) {
        if (!viewToLayoutId.containsKey(viewName)) {
            return
        }
        for (activeView in viewToLayoutId.values) {
            if (activeView !== viewToLayoutId[viewName]) activeView.view.visibility = View.GONE else {
                activeView.onActive()
                activeView.view.visibility = View.VISIBLE
            }
        }
        menuView.visibility = View.GONE
    }

}
