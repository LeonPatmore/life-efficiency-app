package com.leon.patmore.life.efficiency.views.shopping.todays.items

import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ListView
import android.widget.TextView
import androidx.core.view.get
import androidx.core.view.size
import com.leon.patmore.life.efficiency.R
import com.leon.patmore.life.efficiency.ViewManager
import com.leon.patmore.life.efficiency.client.LifeEfficiencyClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext

class ShoppingTodaysItemsViewManager(
    private val lifeEfficiencyClient: LifeEfficiencyClient,
) : ViewManager() {
    private val itemStateManager = ItemStateManager()
    private val tag = javaClass.name
    private var alreadyClicked = false
    private var listView: ListView? = null

    override fun onActive() {
        alreadyClicked = false
        listView = view!!.findViewById(R.id.ShoppingTodaysItemsList)
        setupConfirmButton()
        refreshList()
    }

    private fun refreshList() {
        val todaysItems =
            runBlocking {
                withContext(Dispatchers.Default) {
                    lifeEfficiencyClient.getTodayItems()
                }
            }
        itemStateManager.clear()
        val listAdapter =
            object : ArrayAdapter<String>(context!!, R.layout.list_item_simple, todaysItems) {
                val inflater = LayoutInflater.from(context)

                override fun getView(
                    position: Int,
                    convertView: View?,
                    parent: ViewGroup,
                ): View {
                    val view =
                        convertView ?: inflater.inflate(R.layout.list_item_simple, parent, false)
                    val thisItem = this.getItem(position)!!
                    val textField: TextView = view.findViewById(R.id.textField)
                    textField.textSize = 20f
                    textField.text = thisItem
                    setItemBackground(view, itemStateManager.itemActive(thisItem))
                    textField.setOnClickListener { onItemClicked(view, position) }
                    return view
                }
            }
        listView!!.adapter = listAdapter
    }

    private fun onItemClicked(
        view: View,
        position: Int,
    ) {
        val clickedItem = listView!!.getItemAtPosition(position) as String
        if (alreadyClicked) {
            Log.i(tag, "Flipping ${listView!!.getItemAtPosition(position)}")
            val active: Boolean = itemStateManager.flipItem(clickedItem)
            setItemBackground(view, active)
        } else {
            repeat(listView!!.size) {
                val thisItem = listView!!.getItemAtPosition(it) as String
                val thisView = listView!![it]
                val shouldBeActive = thisItem == clickedItem
                itemStateManager[thisItem] = shouldBeActive
                setItemBackground(thisView, shouldBeActive)
            }
            alreadyClicked = true
        }
    }

    private fun setItemBackground(
        view: View,
        isActive: Boolean,
    ) = view.setBackgroundColor(if (isActive) ACTIVE_COLOUR else INACTIVE_COLOUR)

    private fun setupConfirmButton() =
        view!!
            .findViewById<Button>(R.id.ShoppingTodaysItemsConfirmButton)
            .setOnClickListener { confirm() }

    private fun confirm() {
        Log.i(tag, "Accepting selected items!")
        runBlocking {
            withContext(Dispatchers.Default) {
                lifeEfficiencyClient.completeItems(itemStateManager.getActiveItems())
            }
        }
        refreshList()
    }

    companion object {
        private val INACTIVE_COLOUR = Color.argb(150, 250, 100, 100)
        private val ACTIVE_COLOUR = Color.argb(150, 100, 250, 100)
    }
}
