package com.example.life.efficiency;

import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.life.efficiency.tasks.SendPurchaseTask;

public class AddPurchaseViewManager {

    private static int DEFAULT_QUANTITY = 1;

    private final EditText purchaseName;
    private final EditText quantity;
    private final Button send;

    public AddPurchaseViewManager(EditText purchaseName,
                                  EditText quantity,
                                  Button send) {
        this.purchaseName = purchaseName;
        this.quantity = quantity;
        this.send = send;
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

                purchaseName.setText("");
                quantity.setText(String.valueOf(DEFAULT_QUANTITY));
            }
        });
    }

}
