package com.example.life.efficiency;

import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.life.efficiency.client.LifeEfficiencyClient;

public class AddPurchaseViewManager {

    private final EditText purchaseName;
    private final EditText quantity;
    private final Button send;
    private final LifeEfficiencyClient lifeEfficiencyClient;

    public AddPurchaseViewManager(EditText purchaseName,
                                  EditText quantity,
                                  Button send,
                                  LifeEfficiencyClient lifeEfficiencyClient1) {
        this.purchaseName = purchaseName;
        this.quantity = quantity;
        this.send = send;
        this.lifeEfficiencyClient = lifeEfficiencyClient1;
        initView();
    }

    private void initView() {
        this.send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                SendPurchaseTask sendPurchaseTask = new SendPurchaseTask();
                sendPurchaseTask.execute(new SendPurchaseTask.SendPurchaseTaskDomain(
                        purchaseName.getText().toString(),
                        Integer.parseInt(quantity.getText().toString())));
            }
        });
    }

}
