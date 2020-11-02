package com.example.life.efficiency.views;

import android.view.View;

import java.util.Map;

public class MultiViewManager {

    private final Map<String, ActiveView> viewToLayoutId;

    public MultiViewManager(Map<String, ActiveView> viewToLayoutId) {
        this.viewToLayoutId = viewToLayoutId;
    }

    public void changeView(String viewName) throws ViewNotFoundException {
        if (!viewToLayoutId.containsKey(viewName)) {
            throw new ViewNotFoundException(viewName);
        }

        for (ActiveView activeView : viewToLayoutId.values()) {
            if (activeView != viewToLayoutId.get(viewName)) activeView.getView().setVisibility(View.GONE);
            else {
                activeView.onActive();
                activeView.getView().setVisibility(View.VISIBLE);
            }
        }
    }

}
