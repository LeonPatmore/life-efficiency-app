package com.leon.patmore.life.efficiency.tasks;

import android.os.AsyncTask;

import com.leon.patmore.life.efficiency.client.LifeEfficiencyClientConfiguration;

import java.util.List;

public class CompleteItemsTask extends AsyncTask<List<String>, Void, Void> {

    @SafeVarargs
    @Override
    protected final Void doInBackground(List<String>... listOfItems) {
        for (List<String> items : listOfItems) {
            LifeEfficiencyClientConfiguration
                    .getLifeEfficiencyClientInstance()
                    .completeItems(items);
        }
        return null;
    }

}
