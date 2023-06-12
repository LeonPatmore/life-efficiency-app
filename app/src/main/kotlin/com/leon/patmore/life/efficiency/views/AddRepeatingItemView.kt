package com.leon.patmore.life.efficiency.views

import android.view.View
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Button
import com.leon.patmore.life.efficiency.AutoCompleteService
import com.leon.patmore.life.efficiency.R
import com.leon.patmore.life.efficiency.client.LifeEfficiencyClient
import com.leon.patmore.life.efficiency.resetText
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext

class AddRepeatingItemView(view: View,
                           button: Button,
                           private val lifeEfficiencyClient: LifeEfficiencyClient,
                           private val autoCompleteService: AutoCompleteService) : ActiveView(view, button) {

    private val repeatingItemName: AutoCompleteTextView = view.findViewById(R.id.AddRepeatingItemItem)
    private val sendButton: Button = view.findViewById(R.id.AddRepatingItemButton)

    init {
        sendButton.setOnClickListener {
            runBlocking { withContext(Dispatchers.Default) {
                lifeEfficiencyClient.addRepeatingItem(repeatingItemName.text.toString())
            } }
            repeatingItemName.resetText()
        }
    }

    override fun onActive() {
        repeatingItemName.setAdapter(ArrayAdapter(view.context,
                android.R.layout.select_dialog_item,
                autoCompleteService.getExistingItems()))
    }

}
