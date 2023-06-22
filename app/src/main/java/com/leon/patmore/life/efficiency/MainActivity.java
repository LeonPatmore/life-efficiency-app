package com.leon.patmore.life.efficiency;

import android.os.Bundle;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;

import com.leon.patmore.life.efficiency.client.LifeEfficiencyClient;
import com.leon.patmore.life.efficiency.client.LifeEfficiencyClientConfiguration;
import com.leon.patmore.life.efficiency.views.ActiveView;
import com.leon.patmore.life.efficiency.views.AddPurchaseView;
import com.leon.patmore.life.efficiency.views.AddRepeatingItemView;
import com.leon.patmore.life.efficiency.views.AddToListView;
import com.leon.patmore.life.efficiency.views.HistoryView;
import com.leon.patmore.life.efficiency.views.ItemListView;
import com.leon.patmore.life.efficiency.views.MultiViewManager;
import com.leon.patmore.life.efficiency.views.TodoHistoryView;
import com.leon.patmore.life.efficiency.views.TodoListView;
import com.leon.patmore.life.efficiency.views.WeeklyTodoView;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

public class MainActivity extends AppCompatActivity {

    private static final String ADD_PURCHASE_VIEW_NAME = "add_purchase";
    private static final String ITEM_LIST_VIEW_NAME = "item_list";
    private static final String TODAYS_ITEMS_VIEW_NAME = "todays_items";
    private static final String ADD_TO_LIST_VIEW_NAME = "add_to_list";
    private static final String REPEATING_ITEMS_VIEW_NAME = "repeating_items";
    private static final String ADD_REPEATING_ITEMS_VIEW_NAME = "add_repeating_items";
    private static final String HISTORY_VIEW_NAME = "history";

    private RepeatingItemsViewManager repeatingItemsViewManager;
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
        LinearLayout repeatingItemsLayout = findViewById(R.id.RepeatingItemsLayout);
        Map<String, ActiveView> viewManagerMap = new HashMap<>();
        viewManagerMap.put(ITEM_LIST_VIEW_NAME, new ItemListView(findViewById(R.id.ItemListLayout),
                findViewById(R.id.itemListButton),
                findViewById(R.id.ItemList),
                getApplicationContext(),
                lifeEfficiencyClient));
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
        viewManagerMap.put(REPEATING_ITEMS_VIEW_NAME, new ActiveView(repeatingItemsLayout, findViewById(R.id.RepeatingItemsButton)) {
            @Override
            public void onActive() {
                try {
                    repeatingItemsViewManager.setupList();
                } catch (ExecutionException | InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        viewManagerMap.put(HISTORY_VIEW_NAME, new HistoryView(findViewById(R.id.HistoryLayout),
                findViewById(R.id.historyViewButton),
                lifeEfficiencyClient,
                getApplicationContext()));
        viewManagerMap.put(ADD_PURCHASE_VIEW_NAME, new AddPurchaseView(findViewById(R.id.AddPurchaseLayout),
                findViewById(R.id.AddPurchaseButton),
                lifeEfficiencyClient,
                autoCompleteService));
        viewManagerMap.put(ADD_TO_LIST_VIEW_NAME, new AddToListView(findViewById(R.id.AddToListLayout),
                findViewById(R.id.AddItemToListButton),
                lifeEfficiencyClient,
                autoCompleteService));
        viewManagerMap.put(ADD_REPEATING_ITEMS_VIEW_NAME, new AddRepeatingItemView(findViewById(R.id.AddRepeatingItemLayout),
                findViewById(R.id.AddRepeatingItemViewButton),
                lifeEfficiencyClient,
                autoCompleteService));
        viewManagerMap.put("weeklyTodo", new WeeklyTodoView(findViewById(R.id.WeeklyTodoLayout),
                findViewById(R.id.WeeklyTodoButton),
                findViewById(R.id.WeeklyTodoList),
                getApplicationContext(),
                lifeEfficiencyClient));
        new MultiViewManager(viewManagerMap);
    }

    private void setupViews() {
        try {
            repeatingItemsViewManager =  new RepeatingItemsViewManager(
                    findViewById(R.id.RepeatingItemsList),
                    getApplicationContext());
        } catch (ExecutionException | InterruptedException e) {
            System.err.println("Could not create repeating items view manager!");
            e.printStackTrace();
        }

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
