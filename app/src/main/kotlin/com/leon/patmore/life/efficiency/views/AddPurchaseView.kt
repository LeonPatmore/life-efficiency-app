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

class AddPurchaseView(
    view: View,
    button: Button,
    private val lifeEfficiencyClient: LifeEfficiencyClient,
    private val autoCompleteService: AutoCompleteService,
) : ActiveView(view, button) {
    private val purchaseName: AutoCompleteTextView = view.findViewById(R.id.PurchaseName)
    private val purchaseQuantity: EditText = view.findViewById(R.id.PurchaseQuantity)
    private val sendButton: Button = view.findViewById(R.id.AddPurchaseSendButton)

    init {
        sendButton.setOnClickListener {
            runBlocking {
                withContext(Dispatchers.Default) {
                    lifeEfficiencyClient.addPurchase(
                        purchaseName.text.toString(),
                        purchaseQuantity.text.toString().toInt(),
                    )
                }
            }
            purchaseName.resetText()
            purchaseQuantity.setText(DEFAULT_QUANTITY.toString())
        }
    }

    override fun onActive() {
        purchaseName.setAdapter(
            ArrayAdapter(
                view.context,
                android.R.layout.select_dialog_item,
                autoCompleteService.getExistingItems(),
            ),
        )
    }

    companion object {
        private const val DEFAULT_QUANTITY = 1
    }
}
