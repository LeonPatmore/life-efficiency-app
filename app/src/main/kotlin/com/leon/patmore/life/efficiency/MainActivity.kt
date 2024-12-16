package com.leon.patmore.life.efficiency

import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import com.leon.patmore.life.efficiency.client.LifeEfficiencyClient
import com.leon.patmore.life.efficiency.client.LifeEfficiencyClientConfiguration
import com.leon.patmore.life.efficiency.views.GoalsViewManager
import com.leon.patmore.life.efficiency.views.shopping.ShoppingAddPurchaseViewManager
import com.leon.patmore.life.efficiency.views.shopping.ShoppingHistoryViewManager
import com.leon.patmore.life.efficiency.views.shopping.ShoppingListViewManager
import com.leon.patmore.life.efficiency.views.shopping.ShoppingRepeatingItemsViewManager
import com.leon.patmore.life.efficiency.views.shopping.todays.items.ShoppingTodaysItemsViewManager
import com.leon.patmore.life.efficiency.views.todo.TodoHistoryViewManager
import com.leon.patmore.life.efficiency.views.todo.TodoListViewManager
import com.leon.patmore.life.efficiency.views.todo.TodoWeeklyViewManager

class MainActivity : AppCompatActivity() {
    private val lifeEfficiencyClient: LifeEfficiencyClient = LifeEfficiencyClientConfiguration.getLifeEfficiencyClientInstance()
    private val autoCompleteService: AutoCompleteService = AutoCompleteService(lifeEfficiencyClient)

    private val shoppingListViewData =
        ViewData(
            buttonId = R.id.ShoppingListButton,
            layoutId = R.layout.view_shopping_list,
            viewManager = ShoppingListViewManager(lifeEfficiencyClient, autoCompleteService),
        )
    private val shoppingTodaysItemsViewData =
        ViewData(
            buttonId = R.id.TodaysItemsButton,
            layoutId = R.layout.view_shopping_todays_items,
            viewManager = ShoppingTodaysItemsViewManager(lifeEfficiencyClient),
        )
    private val shoppingAddPurchaseViewData =
        ViewData(
            buttonId = R.id.AddPurchaseButton,
            layoutId = R.layout.view_shopping_add_purchase,
            viewManager = ShoppingAddPurchaseViewManager(lifeEfficiencyClient, autoCompleteService),
        )
    private val shoppingHistoryViewData =
        ViewData(
            buttonId = R.id.ShoppingHistoryButton,
            layoutId = R.layout.view_shopping_history,
            viewManager = ShoppingHistoryViewManager(lifeEfficiencyClient),
        )
    private val shoppingRepeatingItemsViewData =
        ViewData(
            buttonId = R.id.ShoppingRepeatingItemsButton,
            layoutId = R.layout.view_shopping_repeating_items,
            viewManager = ShoppingRepeatingItemsViewManager(lifeEfficiencyClient, autoCompleteService),
        )

    private val shoppingMenuSubViews =
        mutableListOf(
            shoppingListViewData,
            shoppingTodaysItemsViewData,
            shoppingAddPurchaseViewData,
            shoppingHistoryViewData,
            shoppingRepeatingItemsViewData,
        )

    private val todoListViewData =
        ViewData(
            buttonId = R.id.TodoListButton,
            layoutId = R.layout.view_todo_list,
            viewManager = TodoListViewManager(lifeEfficiencyClient),
        )
    private val todoWeeklyViewData =
        ViewData(
            buttonId = R.id.TodoWeeklyButton,
            layoutId = R.layout.view_todo_weekly,
            viewManager = TodoWeeklyViewManager(lifeEfficiencyClient),
        )
    private val todoHistoryViewData =
        ViewData(
            buttonId = R.id.TodoHistoryButton,
            layoutId = R.layout.view_todo_history,
            viewManager = TodoHistoryViewManager(lifeEfficiencyClient),
        )
    private val todoMenuSubViews = mutableListOf(todoListViewData, todoWeeklyViewData, todoHistoryViewData)

    private val goalSubView = ViewData(R.id.GoalsButton, R.layout.view_goals, viewManager = GoalsViewManager(lifeEfficiencyClient))

    private val viewButtons =
        mutableListOf(
            ViewData(R.id.ShoppingButton, R.layout.view_shopping_menu, subViews = shoppingMenuSubViews),
            ViewData(R.id.TodoButton, R.layout.view_todo_menu, subViews = todoMenuSubViews),
            goalSubView,
        )

    init {
        shoppingMenuSubViews.add(ViewData(R.id.ShoppingMenuBackButton, R.layout.view_main_menu, subViews = viewButtons))
        shoppingTodaysItemsViewData.subViews.add(
            ViewData(R.id.ShoppingTodaysItemsBackButton, R.layout.view_shopping_menu, subViews = shoppingMenuSubViews),
        )
        shoppingListViewData.subViews.add(
            ViewData(R.id.ShoppingListBackButton, R.layout.view_shopping_menu, subViews = shoppingMenuSubViews),
        )
        shoppingAddPurchaseViewData.subViews.add(
            ViewData(R.id.ShoppingAddPurchaseBackButton, R.layout.view_shopping_menu, subViews = shoppingMenuSubViews),
        )
        shoppingHistoryViewData.subViews.add(
            ViewData(R.id.ShoppingHistoryBackButton, R.layout.view_shopping_menu, subViews = shoppingMenuSubViews),
        )
        shoppingRepeatingItemsViewData.subViews.add(
            ViewData(R.id.ShoppingRepeatingItemsBackButton, R.layout.view_shopping_menu, subViews = shoppingMenuSubViews),
        )

        todoMenuSubViews.add(ViewData(R.id.TodoMenuBackButton, R.layout.view_main_menu, subViews = viewButtons))
        todoListViewData.subViews.add(ViewData(R.id.TodoListBackButton, R.layout.view_todo_menu, subViews = todoMenuSubViews))
        todoWeeklyViewData.subViews.add(ViewData(R.id.TodoWeeklyBackButton, R.layout.view_todo_menu, subViews = todoMenuSubViews))
        todoHistoryViewData.subViews.add(ViewData(R.id.TodoHistoryBackButton, R.layout.view_todo_menu, subViews = todoMenuSubViews))
        goalSubView.subViews.add(ViewData(R.id.GoalsBackButton, R.layout.view_main_menu, subViews = viewButtons))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        (todoWeeklyViewData.viewManager as TodoWeeklyViewManager).sharedPreferences =
            getPreferences(
                MODE_PRIVATE,
            )
        DataBindingUtil.setContentView<ViewDataBinding>(this, R.layout.view_main_menu)
        setupButtons(*viewButtons.toTypedArray())
    }

    private fun setupButtons(vararg viewDataList: ViewData) {
        viewDataList.forEach { viewData ->
            val view = findViewById<View>(viewData.buttonId)!!
            val button = if (view is Button) view else view.findViewById(R.id.Button)
            button.setOnClickListener {
                val binding = DataBindingUtil.setContentView<ViewDataBinding>(this, viewData.layoutId)
                setupButtons(*viewData.subViews.toTypedArray())
                if (viewData.viewManager != null) {
                    viewData.viewManager.view = binding.root
                    viewData.viewManager.context = applicationContext
                    viewData.viewManager.onActive()
                }
            }
        }
    }
}

data class ViewData(
    val buttonId: Int,
    val layoutId: Int,
    val viewManager: ViewManager? = null,
    val subViews: MutableList<ViewData> = mutableListOf(),
)

abstract class ViewManager {
    var view: View? = null
    var context: Context? = null

    abstract fun onActive()
}
