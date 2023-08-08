package com.leon.patmore.life.efficiency.views

import android.content.Context
import android.view.View
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.ListView
import com.leon.patmore.life.efficiency.AutoCompleteService
import com.leon.patmore.life.efficiency.R
import com.leon.patmore.life.efficiency.client.LifeEfficiencyClient
import com.leon.patmore.life.efficiency.resetText
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext

class RepeatingItemView(view: View,
                        button: Button,
                        addButton: Button,
                        private val addItemText: AutoCompleteTextView,
                        private val list: ListView,
                        private val lifeEfficiencyClient: LifeEfficiencyClient,
                        private val autoCompleteService: AutoCompleteService,
                        private val context: Context) : ActiveView(view, button) {

    init {
        addButton.setOnClickListener {
            runBlocking { withContext(Dispatchers.Default) {
                lifeEfficiencyClient.addRepeatingItem(addItemText.text.toString())
            } }
            addItemText.resetText()
            refreshItems()
        }
    }

    override fun onActive() {
        addItemText.setAdapter(ArrayAdapter(view.context,
                android.R.layout.select_dialog_item,
                autoCompleteService.getExistingItems()))

        refreshItems()
    }

    private fun refreshItems() {
        val repeatingItems = runBlocking {
            withContext(Dispatchers.Default) {
                lifeEfficiencyClient.getRepeatingItems()
            }
        }
        list.adapter = ArrayAdapter(context,
                R.layout.simple_list_item,
                R.id.textField,
                repeatingItems)
    }

}
