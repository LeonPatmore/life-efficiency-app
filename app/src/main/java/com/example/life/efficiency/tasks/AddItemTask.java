package com.example.life.efficiency.tasks;

import android.os.AsyncTask;
import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;

import com.example.life.efficiency.client.LifeEfficiencyClientConfiguration;
import com.example.life.efficiency.client.LifeEfficiencyException;

import java.net.MalformedURLException;

public class AddItemTask
        extends AsyncTask<AddItemTask.AddItemTaskDomain, Void, Void> {

    private static final String TAG = "AddItemTask";

    public static class AddItemTaskDomain {

        private final String name;
        private final int quantity;

        public AddItemTaskDomain(String name, int quantity) {
            this.name = name;
            this.quantity = quantity;
        }

        public String getName() {
            return name;
        }

        public int getQuantity() {
            return quantity;
        }

    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected Void doInBackground(AddItemTaskDomain... domains) {
        for (AddItemTaskDomain addItemTaskDomain : domains) {
            try {
                LifeEfficiencyClientConfiguration
                        .getLifeEfficiencyClientInstance()
                        .addToList(addItemTaskDomain.getName(), addItemTaskDomain.getQuantity());
            } catch (MalformedURLException e) {
                Log.e(TAG, "Can not create life efficiency client!", e);
                throw new RuntimeException(e);
            } catch (LifeEfficiencyException e) {
                Log.w(TAG, "There was an issue adding the item to the shopping list!", e);
            }
        }
        return null;
    }

}
