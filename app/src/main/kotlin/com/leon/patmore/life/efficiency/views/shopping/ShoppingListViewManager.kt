package com.leon.patmore.life.efficiency.views.shopping

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
import com.leon.patmore.life.efficiency.ViewManager
import com.leon.patmore.life.efficiency.client.LifeEfficiencyClient
import com.leon.patmore.life.efficiency.client.domain.ListItem
import com.leon.patmore.life.efficiency.resetText
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import java.lang.Exception

class ShoppingListViewManager(
    private val lifeEfficiencyClient: LifeEfficiencyClient,
    private val autoCompleteService: AutoCompleteService,
) : ViewManager() {
    override fun onActive() {
        refreshItems()
        val addItemText = view!!.findViewById<AutoCompleteTextView>(R.id.AddToShoppingListItemName)
        setupAddItemButton(addItemText)
        addItemText.setAdapter(
            ArrayAdapter(
                view!!.context,
                android.R.layout.select_dialog_item,
                autoCompleteService.getExistingItems(),
            ),
        )
    }

    private fun setupAddItemButton(addItemText: AutoCompleteTextView) {
        val addItemQuantity = view!!.findViewById<EditText>(R.id.AddToShoppingListItemQuantity)
        view!!.findViewById<Button>(R.id.ShoppingListAddButton).setOnClickListener {
            runBlocking {
                withContext(Dispatchers.Default) {
                    lifeEfficiencyClient.addToList(
                        addItemText.text.toString(),
                        addItemQuantity.text.toString().toInt(),
                    )
                }
            }
            addItemText.resetText()
            addItemQuantity.setText(DEFAULT_QUANTITY.toString())
            refreshItems()
        }
    }

    private fun refreshItems() {
        val listItems =
            runBlocking {
                withContext(Dispatchers.Default) {
                    lifeEfficiencyClient.getListItems()
                }
            }

        val listAdapter =
            object : ArrayAdapter<ListItem>(context!!, R.layout.list_item, listItems) {
                val inflater = LayoutInflater.from(context)

                override fun getView(
                    position: Int,
                    convertView: View?,
                    parent: ViewGroup,
                ): View {
                    val view = convertView ?: inflater.inflate(R.layout.list_item, parent, false)
                    val listItem = this.getItem(position)!!
                    val textField = view.findViewById(R.id.textField) as TextView
                    textField.text = listItem.toString()
                    val deleteButton = view.findViewById<Button>(R.id.deleteButton)
                    deleteButton.setOnClickListener {
                        try {
                            runBlocking {
                                launch {
                                    withContext(Dispatchers.Default) {
                                        lifeEfficiencyClient.deleteListItem(
                                            listItem.name,
                                            listItem.quantity,
                                        )
                                    }
                                }
                            }
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                        onActive()
                    }
                    val completeButton = view.findViewById<Button>(R.id.completeButton)
                    completeButton.setOnClickListener {
                        try {
                            runBlocking {
                                launch {
                                    withContext(Dispatchers.Default) {
                                        lifeEfficiencyClient.completeItem(
                                            listItem.name,
                                            listItem.quantity,
                                        )
                                    }
                                }
                            }
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                        onActive()
                    }
                    return view
                }
            }
        view!!.findViewById<ListView>(R.id.ShoppingList).adapter = listAdapter
    }

    companion object {
        private const val DEFAULT_QUANTITY = 1
    }
}
