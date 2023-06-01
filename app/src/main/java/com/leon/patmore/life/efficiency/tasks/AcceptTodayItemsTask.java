package com.leon.patmore.life.efficiency.tasks;

import android.os.AsyncTask;
import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;

import com.leon.patmore.life.efficiency.client.LifeEfficiencyClientConfiguration;
import com.leon.patmore.life.efficiency.client.LifeEfficiencyException;

import java.net.MalformedURLException;

public class AcceptTodayItemsTask extends AsyncTask<Void, Void, Void> {

    private static final String TAG = "AcceptTodayItemsTask";

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected Void doInBackground(Void... voids) {
        try {
            LifeEfficiencyClientConfiguration.getLifeEfficiencyClientInstance().acceptTodayItems();
        } catch (MalformedURLException e) {
            Log.e(TAG, "Can not create life efficiency client!", e);
            throw new RuntimeException(e);
        } catch (LifeEfficiencyException e) {
            Log.w(TAG, "There was an issue accepting today's items!", e);
        }
        return null;
    }

}
