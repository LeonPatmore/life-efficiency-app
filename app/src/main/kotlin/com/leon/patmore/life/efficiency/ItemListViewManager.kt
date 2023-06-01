package com.leon.patmore.life.efficiency

import android.content.Context
import android.widget.ArrayAdapter
import android.widget.ListAdapter
import android.widget.ListView
import com.leon.patmore.life.efficiency.client.LifeEfficiencyClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext

class ItemListViewManager(private val listView: ListView,
                          private val context: Context,
                          private val lifeEfficiencyClient: LifeEfficiencyClient) {

    fun refreshList() {
        val listItems = runBlocking {
            withContext(Dispatchers.Default) {
                lifeEfficiencyClient.listItems
            }
        }
        val listAdapter: ListAdapter = ArrayAdapter(context,
                R.layout.list_item,
                R.id.textField,listItems.map { it.toString() })
        listView.adapter = listAdapter
    }


}
