package com.leon.patmore.life.efficiency.views.shopping

import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.EditText
import com.leon.patmore.life.efficiency.AutoCompleteService
import com.leon.patmore.life.efficiency.R
import com.leon.patmore.life.efficiency.ViewManager
import com.leon.patmore.life.efficiency.client.LifeEfficiencyClient
import com.leon.patmore.life.efficiency.resetText
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext

class ShoppingAddPurchaseViewManager(
    private val lifeEfficiencyClient: LifeEfficiencyClient,
    private val autoCompleteService: AutoCompleteService,
) : ViewManager() {
    override fun onActive() {
        view!!.findViewById<AutoCompleteTextView>(R.id.ShoppingAddPurchaseNameField).setAdapter(
            ArrayAdapter(
                view!!.context,
                android.R.layout.select_dialog_item,
                autoCompleteService.getExistingItems(),
            ),
        )
        setupAddPurchaseSendButton()
    }

    private fun setupAddPurchaseSendButton() {
        val purchaseName: AutoCompleteTextView = view!!.findViewById(R.id.ShoppingAddPurchaseNameField)
        val purchaseQuantity: EditText = view!!.findViewById(R.id.ShoppingAddPurchaseQuantityField)
        view!!.findViewById<Button>(R.id.ShoppingAddPurchaseSendButton).setOnClickListener {
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

    companion object {
        private const val DEFAULT_QUANTITY = 1
    }
}
