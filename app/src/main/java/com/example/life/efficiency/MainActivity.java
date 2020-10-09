package com.example.life.efficiency;

import android.os.Build;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class MainActivity extends AppCompatActivity {

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        GetTodayItemsTask getTodayItemsTask = new GetTodayItemsTask();
        try {
            setupShoppingList(getTodayItemsTask.execute().get());
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
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

        ListView listView = findViewById(R.id.shopping_item_list);
        listView.setAdapter(listAdapter);
    }

}