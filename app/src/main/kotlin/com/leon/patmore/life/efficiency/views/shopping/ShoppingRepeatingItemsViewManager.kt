package com.leon.patmore.life.efficiency.views.shopping

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
import com.leon.patmore.life.efficiency.ViewManager
import com.leon.patmore.life.efficiency.client.LifeEfficiencyClient
import com.leon.patmore.life.efficiency.client.domain.RepeatingItem
import com.leon.patmore.life.efficiency.resetText
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext

class ShoppingRepeatingItemsViewManager(
    private val lifeEfficiencyClient: LifeEfficiencyClient,
    private val autoCompleteService: AutoCompleteService,
) : ViewManager() {
    private var addItemText: AutoCompleteTextView? = null

    override fun onActive() {
        addItemText = view!!.findViewById(R.id.ShoppingRepeatingItemsAddItemField)
        addItemText!!.setAdapter(
            ArrayAdapter(
                view!!.context,
                android.R.layout.select_dialog_item,
                autoCompleteService.getExistingItems(),
            ),
        )
        setupAddButton()
        refreshItems()
    }

    private fun setupAddButton() {
        view!!.findViewById<Button>(R.id.ShoppingRepeatingItemsAddButton).setOnClickListener {
            runBlocking {
                withContext(Dispatchers.Default) {
                    lifeEfficiencyClient.addRepeatingItem(addItemText!!.text.toString())
                }
            }
            addItemText!!.resetText()
            refreshItems()
        }
    }

    private fun refreshItems() {
        val repeatingItems =
            runBlocking {
                withContext(Dispatchers.Default) {
                    lifeEfficiencyClient.getRepeatingItemsDetails().toList().map {
                        RepeatingItemData(it.first, it.second)
                    }
                }
            }

        val listAdapter =
            object : ArrayAdapter<RepeatingItemData>(context!!, R.layout.list_item_shopping_repeating_item, repeatingItems) {
                val inflater = LayoutInflater.from(context)

                override fun getView(
                    position: Int,
                    convertView: View?,
                    parent: ViewGroup,
                ): View {
                    val view = convertView ?: inflater.inflate(R.layout.list_item_shopping_repeating_item, parent, false)
                    val repeatingItem = this.getItem(position)!!

                    val nameField: TextView = view.findViewById(R.id.repeatingItemName)
                    nameField.text = repeatingItem.name

                    val averageGapDaysField: TextView = view.findViewById(R.id.repeatingItemAverageGapDays)
                    averageGapDaysField.text = "Avg gap: ${repeatingItem.repeatingItem.averageGapDays}"

                    val daysSinceLastBoughtField: TextView = view.findViewById(R.id.repeatingItemDaysSinceLastBought)
                    daysSinceLastBoughtField.text = "Days since last: ${repeatingItem.repeatingItem.daysSinceLastBought}"

                    return view
                }
            }
        view!!.findViewById<ListView>(R.id.ShoppingRepeatingItemsList).adapter = listAdapter
    }

    data class RepeatingItemData(
        val name: String,
        val repeatingItem: RepeatingItem,
    )
}
