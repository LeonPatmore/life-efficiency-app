package com.leon.patmore.life.efficiency;

import android.os.Bundle;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import com.leon.patmore.life.efficiency.client.LifeEfficiencyClientConfiguration;
import com.leon.patmore.life.efficiency.views.ActiveView;
import com.leon.patmore.life.efficiency.views.AddPurchaseView;
import com.leon.patmore.life.efficiency.views.AddRepeatingItemView;
import com.leon.patmore.life.efficiency.views.AddToListView;
import com.leon.patmore.life.efficiency.views.HistoryView;
import com.leon.patmore.life.efficiency.views.MultiViewManager;

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
    private ItemListViewManager itemListViewManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setupViews();
        setupViewManager();
    }

    private void setupViewManager() {
        LinearLayout itemListLayout = findViewById(R.id.ItemListLayout);
        LinearLayout todaysItemLayout = findViewById(R.id.TodaysItemsLayout);
        LinearLayout repeatingItemsLayout = findViewById(R.id.RepeatingItemsLayout);
        Map<String, ActiveView> viewManagerMap = new HashMap<>();
        viewManagerMap.put(ITEM_LIST_VIEW_NAME, new ActiveView(itemListLayout, (Button) findViewById(R.id.itemListButton)) {
            @Override
            public void onActive() {
                try {
                    itemListViewManager.refreshList();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        viewManagerMap.put(TODAYS_ITEMS_VIEW_NAME, new ActiveView(todaysItemLayout, (Button) findViewById(R.id.TodaysItemsButton)) {
            @Override
            public void onActive() {
                try {
                    todayItemsViewManager.refreshList();
                } catch (ExecutionException | InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        viewManagerMap.put(REPEATING_ITEMS_VIEW_NAME, new ActiveView(repeatingItemsLayout, (Button) findViewById(R.id.RepeatingItemsButton)) {
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
                LifeEfficiencyClientConfiguration.getLifeEfficiencyClientInstance(),
                getApplicationContext()));
        viewManagerMap.put(ADD_PURCHASE_VIEW_NAME, new AddPurchaseView(findViewById(R.id.AddPurchaseLayout),
                findViewById(R.id.AddPurchaseButton),
                LifeEfficiencyClientConfiguration.getLifeEfficiencyClientInstance()));
        viewManagerMap.put(ADD_TO_LIST_VIEW_NAME, new AddToListView(findViewById(R.id.AddToListLayout),
                findViewById(R.id.AddItemToListButton),
                LifeEfficiencyClientConfiguration.getLifeEfficiencyClientInstance()));
        viewManagerMap.put(ADD_REPEATING_ITEMS_VIEW_NAME, new AddRepeatingItemView(findViewById(R.id.AddRepeatingItemLayout),
                findViewById(R.id.AddRepeatingItemViewButton),
                LifeEfficiencyClientConfiguration.getLifeEfficiencyClientInstance()));
        new MultiViewManager(viewManagerMap);
    }

    private void setupViews() {
        try {
            repeatingItemsViewManager =  new RepeatingItemsViewManager(
                    (ListView) findViewById(R.id.RepeatingItemsList),
                    getApplicationContext());
        } catch (ExecutionException | InterruptedException e) {
            System.err.println("Could not create repeating items view manager!");
            e.printStackTrace();
        }

        try {
            todayItemsViewManager = new TodayItemsViewManager((ListView) findViewById(R.id.ShoppingItemList),
                    getApplicationContext(),
                    (Button) findViewById(R.id.ConfirmTodayButton));
        } catch (ExecutionException | InterruptedException e) {
            System.err.println("Could not create today's item view manager!");
            e.printStackTrace();
        }

        itemListViewManager = new ItemListViewManager(
                (ListView) findViewById(R.id.ItemList),
                getApplicationContext(),
                LifeEfficiencyClientConfiguration.getLifeEfficiencyClientInstance());
    }

}
