package com.leon.patmore.life.efficiency.views;

import android.view.View;
import android.widget.Button;


public class ActiveView {

    private final View view;
    private final Button button;

    public ActiveView(View view, Button button) {
        this.view = view;
        this.button = button;
    }

    public View getView() {
        return view;
    }

    public Button getButton() {
        return button;
    }

    public void onActive() {
        // Do nothing.
    }

}
