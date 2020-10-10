package com.example.life.efficiency;

import android.os.AsyncTask;
import android.os.Build;

import androidx.annotation.RequiresApi;

import com.example.life.efficiency.client.LifeEfficiencyClientConfiguration;

import java.net.MalformedURLException;

public class SendPurchaseTask extends AsyncTask<SendPurchaseTask.SendPurchaseTaskDomain, Void, Void> {

    public static class SendPurchaseTaskDomain {

        private final String name;
        private final int quantity;

        public SendPurchaseTaskDomain(String name, int quantity) {
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
    protected Void doInBackground(SendPurchaseTaskDomain... domains) {
        for (SendPurchaseTaskDomain sendPurchaseTaskDomain : domains) {
            try {
                LifeEfficiencyClientConfiguration
                        .getLifeEfficiencyClientInstance()
                        .addPurchase(sendPurchaseTaskDomain.getName(),
                                sendPurchaseTaskDomain.getQuantity());
            } catch (MalformedURLException e) {
                throw new RuntimeException(e);
            }
        }
        return null;
    }

}
