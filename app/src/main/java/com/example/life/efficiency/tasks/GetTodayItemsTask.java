package com.example.life.efficiency.tasks;

import android.os.AsyncTask;
import android.os.Build;

import androidx.annotation.RequiresApi;

import com.example.life.efficiency.client.LifeEfficiencyClientConfiguration;

import java.net.MalformedURLException;
import java.util.List;

public class GetTodayItemsTask extends AsyncTask<Void, Void, String[]> {

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected String[] doInBackground(Void... voids) {
        List<String> todayItems;
        try {
            todayItems = LifeEfficiencyClientConfiguration.getLifeEfficiencyClientInstance().getTodayItems();
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
        return todayItems.toArray(new String[]{});
    }

}
