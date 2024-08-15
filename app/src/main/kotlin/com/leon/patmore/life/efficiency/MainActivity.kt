package com.leon.patmore.life.efficiency

import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import com.leon.patmore.life.efficiency.views.ShoppingListViewManager
import com.leon.patmore.life.efficiency.client.LifeEfficiencyClientConfiguration
import com.leon.patmore.life.efficiency.client.LifeEfficiencyClient



class MainActivity : AppCompatActivity() {

    private val lifeEfficiencyClient:LifeEfficiencyClient = LifeEfficiencyClientConfiguration.getLifeEfficiencyClientInstance()
    private val autoCompleteService:AutoCompleteService = AutoCompleteService(lifeEfficiencyClient)
    private val viewButtons= arrayOf(
        ViewData(R.id.ShoppingButton, R.layout.shopping_menu,
            subViews = listOf(ViewData(R.id.ShoppingListButton, R.layout.view_shopping_list,
                viewManager = ShoppingListViewManager(
                    lifeEfficiencyClient,
                    autoCompleteService)))))

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        DataBindingUtil.setContentView<ViewDataBinding>(this, R.layout.main_menu)
        setupButtons(*viewButtons)
    }

    private fun setupButtons(vararg viewDataList: ViewData) {
        viewDataList.forEach { viewData ->
            val button = findViewById<Button>(viewData.buttonId)!!
            button.setOnClickListener {
                val binding = DataBindingUtil.setContentView<ViewDataBinding>(this, viewData.layoutId)
                setupButtons(*viewData.subViews.toTypedArray())
                if (viewData.viewManager != null) {
                    viewData.viewManager.onActive(binding.root, applicationContext)
                }
            }
        }
    }

}

data class ViewData(val buttonId: Int,
                    val layoutId: Int,
                    val viewManager: ViewManager? = null,
                    val subViews: List<ViewData> = emptyList())

fun interface ViewManager {
    fun onActive(view: View, context: Context)
}
