package com.leon.patmore.life.efficiency;

import android.os.Bundle;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;

import com.leon.patmore.life.efficiency.client.LifeEfficiencyClient;
import com.leon.patmore.life.efficiency.client.LifeEfficiencyClientConfiguration;
import com.leon.patmore.life.efficiency.views.ActiveView;
import com.leon.patmore.life.efficiency.views.AddPurchaseView;
import com.leon.patmore.life.efficiency.views.GoalsView;
import com.leon.patmore.life.efficiency.views.HistoryView;
import com.leon.patmore.life.efficiency.views.MultiViewManager;
import com.leon.patmore.life.efficiency.views.RepeatingItemView;
import com.leon.patmore.life.efficiency.views.ShoppingListView;
import com.leon.patmore.life.efficiency.views.TodoHistoryView;
import com.leon.patmore.life.efficiency.views.TodoListView;
import com.leon.patmore.life.efficiency.views.WeeklyTodoView;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

public class MainActivity extends AppCompatActivity {

    private static final String ADD_PURCHASE_VIEW_NAME = "add_purchase";
    private static final String SHOPPING_LIST_VIEW_NAME = "shopping_list";
    private static final String TODAYS_ITEMS_VIEW_NAME = "todays_items";
    private static final String REPEATING_ITEMS_VIEW_NAME = "repeating_items";
    private static final String HISTORY_VIEW_NAME = "history";

    private TodayItemsViewManager todayItemsViewManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setupViews();
        setupViewManager();
    }

    private void setupViewManager() {
        LifeEfficiencyClient lifeEfficiencyClient = LifeEfficiencyClientConfiguration.getLifeEfficiencyClientInstance();
        AutoCompleteService autoCompleteService = new AutoCompleteService(lifeEfficiencyClient);
        LinearLayout todaysItemLayout = findViewById(R.id.TodaysItemsLayout);
        Map<String, ActiveView> viewManagerMap = new HashMap<>();
        viewManagerMap.put(SHOPPING_LIST_VIEW_NAME, new ShoppingListView(findViewById(R.id.ShoppingListLayout),
                findViewById(R.id.ShoppingListButton),
                findViewById(R.id.AddToShoppingListItemName),
                findViewById(R.id.AddToShoppingListItemQuantity),
                findViewById(R.id.AddToShoppingListButton),
                findViewById(R.id.ShoppingList),
                getApplicationContext(),
                lifeEfficiencyClient,
                autoCompleteService));
        viewManagerMap.put("todoListView", new TodoListView(findViewById(R.id.TodoListLayout),
                findViewById(R.id.TodoListButton),
                findViewById(R.id.AddTodoButton),
                findViewById(R.id.AddTodoText),
                findViewById(R.id.TodoList),
                getApplicationContext(),
                lifeEfficiencyClient));
        viewManagerMap.put("todoHistoryView", new TodoHistoryView(findViewById(R.id.TodoHistoryLayout),
                findViewById(R.id.TodoHistoryButton),
                findViewById(R.id.TodoHistoryList),
                lifeEfficiencyClient,
                getApplicationContext()));
        viewManagerMap.put(TODAYS_ITEMS_VIEW_NAME, new ActiveView(todaysItemLayout, findViewById(R.id.TodaysItemsButton)) {
            @Override
            public void onActive() {
                try {
                    todayItemsViewManager.refreshList();
                } catch (ExecutionException | InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        viewManagerMap.put(REPEATING_ITEMS_VIEW_NAME, new RepeatingItemView(findViewById(R.id.RepeatingItemsLayout),
                findViewById(R.id.RepeatingItemsButton),
                findViewById(R.id.AddRepatingItemButton),
                findViewById(R.id.AddRepeatingItemItem),
                findViewById(R.id.RepeatingItemsList),
                lifeEfficiencyClient,
                autoCompleteService,
                getApplicationContext()));
        viewManagerMap.put(HISTORY_VIEW_NAME, new HistoryView(findViewById(R.id.HistoryLayout),
                findViewById(R.id.historyViewButton),
                lifeEfficiencyClient,
                getApplicationContext()));
        viewManagerMap.put(ADD_PURCHASE_VIEW_NAME, new AddPurchaseView(findViewById(R.id.AddPurchaseLayout),
                findViewById(R.id.AddPurchaseButton),
                lifeEfficiencyClient,
                autoCompleteService));
        viewManagerMap.put("weeklyTodo", new WeeklyTodoView(findViewById(R.id.WeeklyTodoLayout),
                findViewById(R.id.WeeklyTodoButton),
                findViewById(R.id.WeeklyTodoList),
                getApplicationContext(),
                lifeEfficiencyClient));
        viewManagerMap.put("goals", new GoalsView(findViewById(R.id.GoalsLayout),
                findViewById(R.id.GoalsButton),
                lifeEfficiencyClient,
                getApplicationContext(),
                findViewById(R.id.GoalsList)));
        new MultiViewManager(findViewById(R.id.ButtonMenuLayout),
                findViewById(R.id.BackButton),
                viewManagerMap);
    }

    private void setupViews() {
        try {
            todayItemsViewManager = new TodayItemsViewManager(findViewById(R.id.ShoppingItemList),
                    getApplicationContext(),
                    findViewById(R.id.ConfirmTodayButton));
        } catch (ExecutionException | InterruptedException e) {
            System.err.println("Could not create today's item view manager!");
            e.printStackTrace();
        }
    }

}
