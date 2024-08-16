package com.leon.patmore.life.efficiency.views

import android.content.Context
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ListView
import com.leon.patmore.life.efficiency.R
import com.leon.patmore.life.efficiency.client.LifeEfficiencyClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext

class HistoryView(
    view: View,
    button: Button,
    private val lifeEfficiencyClient: LifeEfficiencyClient,
    private val context: Context,
) : ActiveView(view, button) {
    override fun onActive() {
        val items =
            runBlocking {
                withContext(Dispatchers.Default) {
                    lifeEfficiencyClient.getHistory()
                }
            }
        val listAdapter =
            ArrayAdapter(
                context,
                R.layout.simple_list_item,
                R.id.textField,
                items.map { it.toString() },
            )
        val historyList = view.findViewById(R.id.HistoryList) as ListView
        historyList.adapter = listAdapter
    }
}
