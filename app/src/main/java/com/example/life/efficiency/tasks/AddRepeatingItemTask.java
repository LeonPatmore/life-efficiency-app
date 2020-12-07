package com.example.life.efficiency.tasks;

import android.os.AsyncTask;
import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;

import com.example.life.efficiency.client.LifeEfficiencyClientConfiguration;
import com.example.life.efficiency.client.LifeEfficiencyException;

import java.net.MalformedURLException;

public class AddRepeatingItemTask extends AsyncTask<String, Void, Void> {

    private static final String TAG = "RepeatingItemTask";

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected Void doInBackground(String... items) {
        for (String item : items) {
            try {
                LifeEfficiencyClientConfiguration
                        .getLifeEfficiencyClientInstance()
                        .addRepeatingItem(item);
            } catch (MalformedURLException e) {
                Log.e(TAG, "Can not create life efficiency client!", e);
                throw new RuntimeException(e);
            } catch (LifeEfficiencyException e) {
                Log.w(TAG, "There was an issue adding to the repeating item list!", e);
            }
        }
        return null;
    }

}
