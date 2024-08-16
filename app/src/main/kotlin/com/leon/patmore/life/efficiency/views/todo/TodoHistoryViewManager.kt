package com.leon.patmore.life.efficiency.views.todo

import android.widget.ArrayAdapter
import android.widget.ListView
import com.leon.patmore.life.efficiency.R
import com.leon.patmore.life.efficiency.ViewManager
import com.leon.patmore.life.efficiency.client.LifeEfficiencyClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext

class TodoHistoryViewManager(
    private val lifeEfficiencyClient: LifeEfficiencyClient,
) : ViewManager() {
    override fun onActive() {
        val todoItems =
            runBlocking {
                withContext(Dispatchers.Default) {
                    lifeEfficiencyClient.todoList("done", sorted = true)
                }
            }
        val listAdapter =
            ArrayAdapter(
                context!!,
                R.layout.list_item_simple,
                R.id.textField,
                todoItems.map { it.desc + " | " + it.date_done },
            )
        view!!.findViewById<ListView>(R.id.TodoHistoryList).adapter = listAdapter
    }
}
