package com.leon.patmore.life.efficiency.tasks;

import android.os.AsyncTask;
import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;

import com.leon.patmore.life.efficiency.client.LifeEfficiencyClientConfiguration;
import com.leon.patmore.life.efficiency.client.LifeEfficiencyException;

import java.net.MalformedURLException;

public class SendPurchaseTask
        extends AsyncTask<SendPurchaseTask.SendPurchaseTaskDomain, Void, Void> {

    private static final String TAG = "SendPurchaseTask";

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
                Log.e(TAG, "Can not create life efficiency client!", e);
                throw new RuntimeException(e);
            } catch (LifeEfficiencyException e) {
                Log.w(TAG, "There was an issue adding the purchase!", e);
            }
        }
        return null;
    }

}
