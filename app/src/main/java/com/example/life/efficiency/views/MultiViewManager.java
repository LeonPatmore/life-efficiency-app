package com.example.life.efficiency.views;

import android.view.View;

import java.util.Map;

public class MultiViewManager {

    private final Map<String, View> viewToLayoutId;

    public MultiViewManager(Map<String, View> viewToLayoutId) {
        this.viewToLayoutId = viewToLayoutId;
    }

    public void changeView(String viewName) throws ViewNotFoundException {
        if (!viewToLayoutId.containsKey(viewName)) {
            throw new ViewNotFoundException(viewName);
        }

        for (View view : viewToLayoutId.values()) {
            if (view != viewToLayoutId.get(viewName)) view.setVisibility(View.GONE);
            else view.setVisibility(View.VISIBLE);
        }
    }

}
