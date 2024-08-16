package com.leon.patmore.life.efficiency.views.shopping

import android.widget.ArrayAdapter
import android.widget.ListView
import com.leon.patmore.life.efficiency.R
import com.leon.patmore.life.efficiency.ViewManager
import com.leon.patmore.life.efficiency.client.LifeEfficiencyClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext

class ShoppingHistoryViewManager(
    private val lifeEfficiencyClient: LifeEfficiencyClient,
) : ViewManager() {
    override fun onActive() {
        val items =
            runBlocking {
                withContext(Dispatchers.Default) {
                    lifeEfficiencyClient.getHistory()
                }
            }
        val listAdapter =
            ArrayAdapter(
                context!!,
                R.layout.list_item_simple,
                R.id.textField,
                items.map { it.toString() },
            )
        val historyList: ListView = view!!.findViewById(R.id.ShoppingHistoryList)
        historyList.adapter = listAdapter
    }
}
