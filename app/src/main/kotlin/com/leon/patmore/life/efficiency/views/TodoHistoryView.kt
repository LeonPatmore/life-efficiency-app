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

class TodoHistoryView(
    view: View,
    button: Button,
    private val listView: ListView,
    private val lifeEfficiencyClient: LifeEfficiencyClient,
    private val context: Context,
) : ActiveView(view, button) {
    override fun onActive() {
        val todoItems =
            runBlocking {
                withContext(Dispatchers.Default) {
                    lifeEfficiencyClient.todoList("done", sorted = true)
                }
            }
        val listAdapter =
            ArrayAdapter(
                context,
                R.layout.simple_list_item,
                R.id.textField,
                todoItems.map { it.desc + " | " + it.date_done },
            )
        listView.adapter = listAdapter
    }
}
