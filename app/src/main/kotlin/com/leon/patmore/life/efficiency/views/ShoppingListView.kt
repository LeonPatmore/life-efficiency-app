package com.leon.patmore.life.efficiency.views

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.EditText
import android.widget.ListView
import android.widget.TextView
import com.leon.patmore.life.efficiency.AutoCompleteService
import com.leon.patmore.life.efficiency.R
import com.leon.patmore.life.efficiency.client.LifeEfficiencyClient
import com.leon.patmore.life.efficiency.client.domain.ListItem
import com.leon.patmore.life.efficiency.resetText
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import java.lang.Exception

class ShoppingListView(view: View,
                       button: Button,
                       private val addItemText: AutoCompleteTextView,
                       private val addItemQuantity: EditText,
                       addItemButton: Button,
                       private val listView: ListView,
                       private val context: Context,
                       private val lifeEfficiencyClient: LifeEfficiencyClient,
                       private val autoCompleteService: AutoCompleteService) :
        ActiveView(view, button) {

    init {
        addItemButton.setOnClickListener {
            runBlocking { withContext(Dispatchers.Default) {
                lifeEfficiencyClient.addToList(addItemText.text.toString(),
                        addItemQuantity.text.toString().toInt())
            } }
            addItemText.resetText()
            addItemQuantity.setText(DEFAULT_QUANTITY.toString())
            refreshItems()
        }
    }

    override fun onActive() {
        refreshItems()
        addItemText.setAdapter(ArrayAdapter(view.context,
                android.R.layout.select_dialog_item,
                autoCompleteService.getExistingItems()))
    }

    private fun refreshItems() {
        val listItems = runBlocking {
            withContext(Dispatchers.Default) {
                lifeEfficiencyClient.getListItems()
            }
        }

        val listAdapter = object: ArrayAdapter<ListItem>(context, R.layout.list_item, listItems) {
            val inflater = LayoutInflater.from(context)
            override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
                val view = convertView ?: inflater.inflate(R.layout.list_item, parent, false)
                val listItem = this.getItem(position)!!
                val textField = view.findViewById(R.id.textField) as TextView
                textField.text = listItem.toString()
                val deleteButton = view.findViewById<Button>(R.id.deleteButton)
                deleteButton.setOnClickListener {
                    try { runBlocking { launch { withContext(Dispatchers.Default) { lifeEfficiencyClient.deleteListItem(listItem.name, listItem.quantity) } } } } catch (e: Exception) { e.printStackTrace() }
                    onActive()
                }
                val completeButton = view.findViewById<Button>(R.id.completeButton)
                completeButton.setOnClickListener {
                    try { runBlocking { launch { withContext(Dispatchers.Default) { lifeEfficiencyClient.completeItem(listItem.name, listItem.quantity) } } } } catch (e: Exception) { e.printStackTrace() }
                    onActive()
                }
                return view
            }
        }
        listView.adapter = listAdapter
    }

    companion object {
        private const val DEFAULT_QUANTITY = 1
    }

}
