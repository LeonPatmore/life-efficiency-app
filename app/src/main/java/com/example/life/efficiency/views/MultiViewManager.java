package com.example.life.efficiency.views;

import android.view.View;

import java.util.Map;

public class MultiViewManager {

    private final Map<String, ActiveView> viewToLayoutId;

    public MultiViewManager(Map<String, ActiveView> viewToLayoutId) {
        this.viewToLayoutId = viewToLayoutId;
        setupButtons();
    }

    public void setupButtons() {
        for (final String viewName : viewToLayoutId.keySet()) {
            ActiveView thisView = viewToLayoutId.get(viewName);
            if (thisView == null)
                continue;
            thisView.getButton().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        changeView(viewName);
                    } catch (ViewNotFoundException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
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
