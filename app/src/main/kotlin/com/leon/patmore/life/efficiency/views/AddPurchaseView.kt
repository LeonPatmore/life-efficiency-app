package com.leon.patmore.life.efficiency.views

import android.view.View
import android.widget.Button
import android.widget.EditText
import com.leon.patmore.life.efficiency.R
import com.leon.patmore.life.efficiency.client.LifeEfficiencyClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext

class AddPurchaseView(view: View,
                      button: Button,
                      private val lifeEfficiencyClient: LifeEfficiencyClient) : ActiveView(view, button) {

    private val purchaseName: EditText = view.findViewById(R.id.PurchaseName)
    private val purchaseQuantity: EditText = view.findViewById(R.id.PurchaseQuantity)
    private val sendButton: Button = view.findViewById(R.id.AddPurchaseSendButton)

    init {
        sendButton.setOnClickListener {
            runBlocking { withContext(Dispatchers.Default) {
                lifeEfficiencyClient.addPurchase(purchaseName.text.toString(),
                        purchaseQuantity.text.toString().toInt())
            } }
            purchaseName.resetText()
            purchaseQuantity.setText(DEFAULT_QUANTITY.toString())
        }
    }

    private fun EditText.resetText() = this.setText("")

    companion object {
        private const val DEFAULT_QUANTITY = 1
    }

}
