package com.example.life.efficiency;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.example.life.efficiency.client.LifeEfficiencyClientConfiguration;
import com.example.life.efficiency.views.MultiViewManager;
import com.example.life.efficiency.views.ViewNotFoundException;

import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

public class MainActivity extends AppCompatActivity {

    private static final String ADD_PURCHASE_VIEW_NAME = "add_purchase";
    private static final String TODAYS_ITEMS_VIEW_NAME = "todays_items";

    private MultiViewManager multiViewManager;

    private static ItemStateManager itemStateManager = new ItemStateManager();

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setupViewManager();
        setupButtons();
        setupViews();

        GetTodayItemsTask getTodayItemsTask = new GetTodayItemsTask();
        try {
            setupShoppingList(getTodayItemsTask.execute().get());
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
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
        try {
            new AddPurchaseViewManager((EditText) findViewById(R.id.PurchaseName),
                    (EditText) findViewById(R.id.PurchaseQuantity),
                    (Button) findViewById(R.id.AddPurchaseSendButton),
                    LifeEfficiencyClientConfiguration.getLifeEfficiencyClientInstance());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }

    private void setupShoppingList(String[] shoppingItems) {
        List<String> a = new ArrayList<>(Arrays.asList(shoppingItems));
        a.add("test-item");
        ListAdapter listAdapter = new ArrayAdapter<>(this,
                R.layout.list_item,
                R.id.textview,
                a.toArray(shoppingItems));

        ListView listView = findViewById(R.id.ShoppingItemList);
        listView.setAdapter(listAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String item = view.toString();
                System.out.println(item);
                boolean active = itemStateManager.flipItem(item);
                int colour = active ? Color.GREEN : Color.RED;
                view.setBackgroundColor(colour);
            }
        });
    }

}