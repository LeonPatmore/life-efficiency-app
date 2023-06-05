package com.leon.patmore.life.efficiency.tasks;

import android.os.AsyncTask;

import com.leon.patmore.life.efficiency.client.LifeEfficiencyClientConfiguration;

public class AddRepeatingItemTask extends AsyncTask<String, Void, Void> {

    @Override
    protected Void doInBackground(String... items) {
        for (String item : items) {
            LifeEfficiencyClientConfiguration
                    .getLifeEfficiencyClientInstance()
                    .addRepeatingItem(item);
        }
        return null;
    }

}
