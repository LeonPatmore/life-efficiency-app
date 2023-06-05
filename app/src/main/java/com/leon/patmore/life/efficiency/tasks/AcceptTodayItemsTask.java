package com.leon.patmore.life.efficiency.tasks;

import android.os.AsyncTask;

import com.leon.patmore.life.efficiency.client.LifeEfficiencyClientConfiguration;

public class AcceptTodayItemsTask extends AsyncTask<Void, Void, Void> {

    @Override
    protected Void doInBackground(Void... voids) {
        LifeEfficiencyClientConfiguration.getLifeEfficiencyClientInstance().acceptTodayItems();
        return null;
    }

}
