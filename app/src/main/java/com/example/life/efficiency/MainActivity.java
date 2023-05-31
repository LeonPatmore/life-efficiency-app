package com.example.life.efficiency;

import android.os.Build;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.example.life.efficiency.views.ActiveView;
import com.example.life.efficiency.views.MultiViewManager;

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

    private RepeatingItemsViewManager repeatingItemsViewManager;
    private TodayItemsViewManager todayItemsViewManager;
    private ItemListViewManager itemListViewManager;

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setupViews();
        setupViewManager();
    }

    private void setupViewManager() {
        LinearLayout addPurchaseLayout = findViewById(R.id.AddPurchaseLayout);
        LinearLayout itemListLayout = findViewById(R.id.ItemListLayout);
        LinearLayout todaysItemLayout = findViewById(R.id.TodaysItemsLayout);
        LinearLayout addToListLayout = findViewById(R.id.AddToListLayout);
        LinearLayout repeatingItemsLayout = findViewById(R.id.RepeatingItemsLayout);
        LinearLayout addRepeatingItemLayout = findViewById(R.id.AddRepeatingItemLayout);
        Map<String, ActiveView> viewManagerMap = new HashMap<>();
        viewManagerMap.put(ADD_PURCHASE_VIEW_NAME, new ActiveView(addPurchaseLayout, (Button) findViewById(R.id.AddPurchaseButton)));
        viewManagerMap.put(ITEM_LIST_VIEW_NAME, new ActiveView(itemListLayout, (Button) findViewById(R.id.itemListButton)) {
            @Override
            public void onActive() {
                try {
                    itemListViewManager.refreshList();
                } catch (ExecutionException | InterruptedException e) {
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
        viewManagerMap.put(ADD_TO_LIST_VIEW_NAME, new ActiveView(addToListLayout, (Button) findViewById(R.id.AddItemToListButton)));
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
        viewManagerMap.put(ADD_REPEATING_ITEMS_VIEW_NAME, new ActiveView(addRepeatingItemLayout, (Button) findViewById(R.id.AddRepeatingItemViewButton)));
        new MultiViewManager(viewManagerMap);
    }

    private void setupViews() {
        new AddPurchaseViewManager((EditText) findViewById(R.id.PurchaseName),
                (EditText) findViewById(R.id.PurchaseQuantity),
                (Button) findViewById(R.id.AddPurchaseSendButton));
        new AddToListViewManager((EditText) findViewById(R.id.AddToListPurchaseName),
                (EditText) findViewById(R.id.AddToListPurchaseQuantity),
                (Button) findViewById(R.id.AddToListSendButton));
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

        try {
            itemListViewManager = new ItemListViewManager(
                    (ListView) findViewById(R.id.ItemList),
                    getApplicationContext());
        } catch (ExecutionException | InterruptedException e) {
            System.err.println("Could not create item list view manager!");
            e.printStackTrace();
        }
    }

}