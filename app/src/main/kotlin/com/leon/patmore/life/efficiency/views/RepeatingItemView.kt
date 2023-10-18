package com.leon.patmore.life.efficiency.views

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.ListView
import android.widget.TextView
import com.leon.patmore.life.efficiency.AutoCompleteService
import com.leon.patmore.life.efficiency.R
import com.leon.patmore.life.efficiency.client.LifeEfficiencyClient
import com.leon.patmore.life.efficiency.client.domain.RepeatingItem
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
                lifeEfficiencyClient.getRepeatingItemsDetails().toList().map {
                    RepeatingItemData(it.first, it.second)
                }
            }
        }

        val listAdapter = object: ArrayAdapter<RepeatingItemData>(context, R.layout.repeating_item_list_item, repeatingItems) {
            val inflater = LayoutInflater.from(context)
            override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
                val view = convertView ?: inflater.inflate(R.layout.repeating_item_list_item, parent, false)
                val repeatingItem = this.getItem(position)!!

                val nameField = view.findViewById(R.id.repeatingItemName) as TextView
                nameField.text = repeatingItem.name

                val averageGapDaysField = view.findViewById(R.id.repeatingItemAverageGapDays) as TextView
                averageGapDaysField.text = "Avg gap: ${repeatingItem.repeatingItem.averageGapDays}"

                val daysSinceLastBoughtField = view.findViewById(R.id.repeatingItemDaysSinceLastBought) as TextView
                daysSinceLastBoughtField.text = "Days since last: ${repeatingItem.repeatingItem.daysSinceLastBought}"

                return view
            }
        }
        list.adapter = listAdapter
    }

    data class RepeatingItemData(val name: String, val repeatingItem: RepeatingItem)

}
