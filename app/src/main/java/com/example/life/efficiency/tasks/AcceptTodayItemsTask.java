package com.example.life.efficiency.tasks;

import android.os.AsyncTask;
import android.os.Build;

import androidx.annotation.RequiresApi;

import com.example.life.efficiency.client.LifeEfficiencyClientConfiguration;

import java.net.MalformedURLException;

public class AcceptTodayItemsTask extends AsyncTask<Void, Void, Void> {

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected Void doInBackground(Void... voids) {
        try {
            LifeEfficiencyClientConfiguration.getLifeEfficiencyClientInstance().acceptTodayItems();
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
        return null;
    }

}
