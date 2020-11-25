package com.example.life.efficiency;

import android.content.Context;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.example.life.efficiency.tasks.GetRepeatingItemsTask;

import java.util.concurrent.ExecutionException;

public class RepeatingItemsViewManager {

    private final ListView listView;
    private final Context context;

    public RepeatingItemsViewManager(ListView listView, Context context)
            throws ExecutionException, InterruptedException {
        this.listView = listView;
        this.context = context;
        setupList();
    }

    public void setupList() throws ExecutionException, InterruptedException {
        GetRepeatingItemsTask getRepeatingItemsTask = new GetRepeatingItemsTask();
        String[] repeatedItems = getRepeatingItemsTask.execute().get();

        ListAdapter listAdapter = new ArrayAdapter<>(context,
                R.layout.list_item,
                R.id.textview,
                repeatedItems);
        listView.setAdapter(listAdapter);
    }

}
