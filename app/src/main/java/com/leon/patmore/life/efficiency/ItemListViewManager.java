package com.leon.patmore.life.efficiency;

import android.content.Context;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.leon.patmore.life.efficiency.tasks.GetListItemsTask;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class ItemListViewManager {

    private final ListView listView;
    private final Context context;

    public ItemListViewManager(ListView listView, Context context)
            throws ExecutionException, InterruptedException {
        this.listView = listView;
        this.context = context;
        refreshList();
    }

    public void refreshList() throws ExecutionException, InterruptedException {
        GetListItemsTask getListItemsTask = new GetListItemsTask();
        List<String> todayItems = new ArrayList<>(Arrays.asList(getListItemsTask.execute().get()));
        ListAdapter listAdapter = new ArrayAdapter<>(context,
                R.layout.list_item,
                R.id.textField,
                todayItems.toArray(new String[]{}));

        listView.setAdapter(listAdapter);
    }


}
