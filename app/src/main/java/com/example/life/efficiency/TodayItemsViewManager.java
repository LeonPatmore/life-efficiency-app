package com.example.life.efficiency;

import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;

import androidx.annotation.RequiresApi;

import com.example.life.efficiency.tasks.AcceptTodayItemsTask;
import com.example.life.efficiency.tasks.CompleteItemsTask;
import com.example.life.efficiency.tasks.GetTodayItemsTask;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class TodayItemsViewManager {

    private static final String TAG = "TodayItemsViewManager";

    private static final int INACTIVE_COLOUR = Color.RED;
    private static final int ACTIVE_COLOUR = Color.WHITE;

    private final ListView listView;
    private final Context context;
    private final Button confirmButton;

    private final ItemStateManager itemStateManager = new ItemStateManager();

    public TodayItemsViewManager(ListView listView,
                                 Context context,
                                 Button confirmButton)
            throws ExecutionException, InterruptedException {
        this.listView = listView;
        this.context = context;
        this.confirmButton = confirmButton;
        refreshList();
        setupConfirmButton();
    }

    public void refreshList() throws ExecutionException, InterruptedException {
        GetTodayItemsTask getTodayItemsTask = new GetTodayItemsTask();
        List<String> todayItems = new ArrayList<>(Arrays.asList(getTodayItemsTask.execute().get()));
        ListAdapter listAdapter = new ArrayAdapter<>(context,
                R.layout.list_item,
                R.id.textview,
                todayItems.toArray(new String[]{}));

        itemStateManager.clear();
        for (String item : todayItems) {
            itemStateManager.itemActive(item);
        }

        listView.setAdapter(listAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.i(TAG, "Flipping " + listView.getItemAtPosition(position));
                boolean active = itemStateManager.flipItem((String) listView.getItemAtPosition(position));
                int colour = active ? ACTIVE_COLOUR : INACTIVE_COLOUR;
                view.setBackgroundColor(colour);
            }
        });
    }

    private void setupConfirmButton() {
        this.confirmButton.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.R)
            @Override
            public void onClick(View v) {
                confirm();
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.R)
    private void confirm() {
        if (itemStateManager.allActive()) {
            Log.i(TAG, "Accepting all of today's items!");
            new AcceptTodayItemsTask().execute();
        } else {
            Log.i(TAG,"Accepting a subset of today's items!");
            CompleteItemsTask completeItemsTask = new CompleteItemsTask();
            completeItemsTask.execute(itemStateManager.getActiveItems());
        }
        try {
            refreshList();
        } catch (ExecutionException | InterruptedException e) {
            Log.w(TAG, "Could not reset the list after confirmation!");
            e.printStackTrace();
        }
    }

}
