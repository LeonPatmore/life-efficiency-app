package com.leon.patmore.life.efficiency.tasks;

import android.os.AsyncTask;

import com.leon.patmore.life.efficiency.client.LifeEfficiencyClientConfiguration;

import java.util.List;

public class GetRepeatingItemsTask extends AsyncTask<Void, Void, String[]> {

    private static final String TAG = "GetTodayItemsTask";

    @Override
    protected String[] doInBackground(Void... voids) {
        List<String> repeatingItems = LifeEfficiencyClientConfiguration
                .getLifeEfficiencyClientInstance()
                .getRepeatingItems();
        return repeatingItems.toArray(new String[]{});
    }

}
