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

import com.example.life.efficiency.views.MultiViewManager;
import com.example.life.efficiency.views.ViewNotFoundException;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

public class MainActivity extends AppCompatActivity {

    private static final String ADD_PURCHASE_VIEW_NAME = "add_purchase";
    private static final String TODAYS_ITEMS_VIEW_NAME = "todays_items";

    private MultiViewManager multiViewManager;

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setupViewManager();
        setupButtons();
        setupViews();
    }

    private void setupViewManager() {
        LinearLayout addPurchaseLayout = findViewById(R.id.AddPurchaseLayout);
        LinearLayout todaysItemLayout = findViewById(R.id.TodaysItemsLayout);
        Map<String, View> viewManagerMap = new HashMap<>();
        viewManagerMap.put(ADD_PURCHASE_VIEW_NAME, addPurchaseLayout);
        viewManagerMap.put(TODAYS_ITEMS_VIEW_NAME, todaysItemLayout);
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
    }

    private void setupViews() {
        new AddPurchaseViewManager((EditText) findViewById(R.id.PurchaseName),
                (EditText) findViewById(R.id.PurchaseQuantity),
                (Button) findViewById(R.id.AddPurchaseSendButton));

        try {
            new TodayItemsViewManager((ListView) findViewById(R.id.ShoppingItemList),
                    getApplicationContext(),
                    (Button) findViewById(R.id.ConfirmTodayButton));
        } catch (ExecutionException | InterruptedException e) {
            System.err.println("Could not create today's item view manager!");
            e.printStackTrace();
        }
    }

}