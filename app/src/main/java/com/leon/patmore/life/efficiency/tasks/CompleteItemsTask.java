package com.leon.patmore.life.efficiency.tasks;

import android.os.AsyncTask;
import android.util.Log;

import com.leon.patmore.life.efficiency.client.LifeEfficiencyClientConfiguration;
import com.leon.patmore.life.efficiency.client.LifeEfficiencyException;

import java.net.MalformedURLException;
import java.util.List;

public class CompleteItemsTask extends AsyncTask<List<String>, Void, Void> {

    private static final String TAG = "AddItemTask";

    @SafeVarargs
    @Override
    protected final Void doInBackground(List<String>... listOfItems) {
        for (List<String> items : listOfItems) {
            try {
                LifeEfficiencyClientConfiguration
                        .getLifeEfficiencyClientInstance()
                        .completeItems(items);
            } catch (MalformedURLException e) {
                Log.e(TAG, "Can not create life efficiency client!", e);
                throw new RuntimeException(e);
            } catch (LifeEfficiencyException e) {
                Log.w(TAG, "There was an issue completing the list of items!", e);
            }
        }
        return null;
    }

}
