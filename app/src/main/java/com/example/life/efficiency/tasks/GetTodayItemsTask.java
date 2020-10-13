package com.example.life.efficiency.tasks;

import android.os.AsyncTask;
import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;

import com.example.life.efficiency.client.LifeEfficiencyClientConfiguration;
import com.example.life.efficiency.client.LifeEfficiencyException;

import java.net.MalformedURLException;
import java.util.List;

public class GetTodayItemsTask extends AsyncTask<Void, Void, String[]> {

    private static final String TAG = "GetTodayItemsTask";

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected String[] doInBackground(Void... voids) {
        List<String> todayItems;
        try {
            todayItems = LifeEfficiencyClientConfiguration
                    .getLifeEfficiencyClientInstance()
                    .getTodayItems();
        } catch (MalformedURLException e) {
            Log.e(TAG, "Can not create life efficiency client!", e);
            throw new RuntimeException(e);
        } catch (LifeEfficiencyException e) {
            Log.w(TAG, "There was an issue getting today's items!", e);
            return new String[]{};
        }
        return todayItems.toArray(new String[]{});
    }

}
