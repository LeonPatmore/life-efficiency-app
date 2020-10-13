package com.example.life.efficiency;

import android.os.Build;

import androidx.annotation.RequiresApi;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;

public class ItemStateManager {

    private Map<String, Boolean> itemStates = new HashMap<>();

    public void clear() {
        itemStates.clear();
    }

    public boolean itemActive(String item) {
        if (itemStates.containsKey(item) && itemStates.get(item) != null) {
            Boolean itemBoolean = itemStates.get(item);
            if (itemBoolean != null)
                return itemBoolean;
        }
        itemStates.put(item, Boolean.TRUE);
        return true;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public boolean flipItem(String item) {
        itemStates.put(item, !itemActive(item));
        return Optional.ofNullable(itemStates.get(item)).orElse(Boolean.TRUE);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public boolean allActive() {
        return itemStates.values().stream().allMatch(new Predicate<Boolean>() {
            @Override
            public boolean test(Boolean ifActive) {
                return ifActive;
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.R)
    public List<String> getActiveItems() {
        List<String> activeList = new ArrayList<>();
        for(String item : itemStates.keySet()) {
            Boolean ifActive = itemStates.get(item);
            if (ifActive != null && ifActive) {
                activeList.add(item);
            }
        }
        return activeList;
    }

}
