package com.example.life.efficiency.views;

import android.view.View;

public class ActiveView {

    private final View view;

    public ActiveView(View view) {
        this.view = view;
    }

    public View getView() {
        return view;
    }

    public void onActive() {
        // Do nothing.
    }

}
