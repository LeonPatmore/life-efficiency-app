package com.leon.patmore.life.efficiency.views

import android.view.View
import android.widget.Button
import android.widget.EditText
import com.leon.patmore.life.efficiency.R
import com.leon.patmore.life.efficiency.client.LifeEfficiencyClient
import com.leon.patmore.life.efficiency.resetText
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext

class AddRepeatingItemView(view: View,
                           button: Button,
                           private val lifeEfficiencyClient: LifeEfficiencyClient) : ActiveView(view, button) {

    private val repeatingItemName: EditText = view.findViewById(R.id.AddRepeatingItemItem)
    private val sendButton: Button = view.findViewById(R.id.AddRepatingItemButton)

    init {
        sendButton.setOnClickListener {
            runBlocking { withContext(Dispatchers.Default) {
                lifeEfficiencyClient.addRepeatingItem(repeatingItemName.text.toString())
            } }
            repeatingItemName.resetText()
        }
    }

}
