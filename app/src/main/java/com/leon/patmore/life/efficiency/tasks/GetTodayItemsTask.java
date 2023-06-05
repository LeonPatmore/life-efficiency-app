package com.leon.patmore.life.efficiency.tasks;

import android.os.AsyncTask;

import com.leon.patmore.life.efficiency.client.LifeEfficiencyClientConfiguration;

import java.util.List;

public class GetTodayItemsTask extends AsyncTask<Void, Void, String[]> {

    @Override
    protected String[] doInBackground(Void... voids) {
        List<String> todayItems = LifeEfficiencyClientConfiguration
                .getLifeEfficiencyClientInstance()
                .getTodayItems();
        return todayItems.toArray(new String[]{});
    }

}
