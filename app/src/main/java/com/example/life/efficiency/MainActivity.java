package com.example.life.efficiency;

import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.example.life.efficiency.views.ActiveView;
import com.example.life.efficiency.views.MultiViewManager;
import com.example.life.efficiency.views.ViewNotFoundException;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

public class MainActivity extends AppCompatActivity {

    private static final String ADD_PURCHASE_VIEW_NAME = "add_purchase";
    private static final String TODAYS_ITEMS_VIEW_NAME = "todays_items";
    private static final String ADD_TO_LIST_VIEW_NAME = "add_to_list";

    private MultiViewManager multiViewManager;

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TodayItemsViewManager todayItemsViewManager = setupViews();
        setupViewManager(todayItemsViewManager);
        setupButtons();
    }

    private void setupViewManager(final TodayItemsViewManager todayItemsViewManager) {
        LinearLayout addPurchaseLayout = findViewById(R.id.AddPurchaseLayout);
        LinearLayout todaysItemLayout = findViewById(R.id.TodaysItemsLayout);
        LinearLayout addToListLayout = findViewById(R.id.AddToListLayout);
        Map<String, ActiveView> viewManagerMap = new HashMap<>();
        viewManagerMap.put(ADD_PURCHASE_VIEW_NAME, new ActiveView(addPurchaseLayout));
        viewManagerMap.put(TODAYS_ITEMS_VIEW_NAME, new ActiveView(todaysItemLayout) {
            @Override
            public void onActive() {
                try {
                    todayItemsViewManager.refreshList();
                } catch (ExecutionException | InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        viewManagerMap.put(ADD_TO_LIST_VIEW_NAME, new ActiveView(addToListLayout));
        this.multiViewManager = new MultiViewManager(viewManagerMap);
    }

    private void setupButtons() {
        Button addPurchaseButton = findViewById(R.id.AddPurchaseButton);
        addPurchaseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    multiViewManager.changeView(ADD_PURCHASE_VIEW_NAME);
                } catch (ViewNotFoundException e) {
                    e.printStackTrace();
                }
            }
        });

        Button todaysItemButton = findViewById(R.id.TodaysItemsButton);
        todaysItemButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    multiViewManager.changeView(TODAYS_ITEMS_VIEW_NAME);
                } catch (ViewNotFoundException e) {
                    e.printStackTrace();
                }
            }
        });

        Button addToListButton = findViewById(R.id.AddItemToListButton);
        addToListButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    multiViewManager.changeView(ADD_TO_LIST_VIEW_NAME);
                } catch (ViewNotFoundException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private TodayItemsViewManager setupViews() {
        new AddPurchaseViewManager((EditText) findViewById(R.id.PurchaseName),
                (EditText) findViewById(R.id.PurchaseQuantity),
                (Button) findViewById(R.id.AddPurchaseSendButton));
        new AddToListViewManager((EditText) findViewById(R.id.AddToListPurchaseName),
                (EditText) findViewById(R.id.AddToListPurchaseQuantity),
                (Button) findViewById(R.id.AddToListSendButton));

        try {
            return new TodayItemsViewManager((ListView) findViewById(R.id.ShoppingItemList),
                    getApplicationContext(),
                    (Button) findViewById(R.id.ConfirmTodayButton));
        } catch (ExecutionException | InterruptedException e) {
            System.err.println("Could not create today's item view manager!");
            e.printStackTrace();
        }
        return null;
    }

}