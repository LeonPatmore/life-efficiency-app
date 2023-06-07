package com.leon.patmore.life.efficiency.views

import android.view.View
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.EditText
import com.leon.patmore.life.efficiency.AutoCompleteService
import com.leon.patmore.life.efficiency.R
import com.leon.patmore.life.efficiency.client.LifeEfficiencyClient
import com.leon.patmore.life.efficiency.resetText
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext

class AddToListView(view: View,
                    button: Button,
                    private val lifeEfficiencyClient: LifeEfficiencyClient,
                    autoCompleteService: AutoCompleteService) : ActiveView(view, button) {

    private val listItemName: AutoCompleteTextView = view.findViewById(R.id.AddToListPurchaseName)
    private val listItemQuantity: EditText = view.findViewById(R.id.AddToListPurchaseQuantity)
    private val sendButton: Button = view.findViewById(R.id.AddToListSendButton)

    init {
        sendButton.setOnClickListener {
            runBlocking { withContext(Dispatchers.Default) {
                lifeEfficiencyClient.addToList(listItemName.text.toString(),
                        listItemQuantity.text.toString().toInt())
            } }
            listItemName.resetText()
            listItemQuantity.setText(DEFAULT_QUANTITY.toString())
        }
        listItemName.setAdapter(ArrayAdapter(view.context,
                android.R.layout.select_dialog_item,
                autoCompleteService.getExistingItems()))
    }

    companion object {
        private const val DEFAULT_QUANTITY = 1
    }

}
