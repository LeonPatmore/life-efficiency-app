package com.leon.patmore.life.efficiency.tasks;

import android.os.AsyncTask;

import com.leon.patmore.life.efficiency.client.LifeEfficiencyClientConfiguration;

public class AddItemTask extends AsyncTask<AddItemTask.AddItemTaskDomain, Void, Void> {

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

    @Override
    protected Void doInBackground(AddItemTaskDomain... domains) {
        for (AddItemTaskDomain addItemTaskDomain : domains) {
            LifeEfficiencyClientConfiguration
                    .getLifeEfficiencyClientInstance()
                    .addToList(addItemTaskDomain.getName(), addItemTaskDomain.getQuantity());
        }
        return null;
    }

}
