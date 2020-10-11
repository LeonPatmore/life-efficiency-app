package com.example.life.efficiency;

import android.os.Build;

import androidx.annotation.RequiresApi;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class ItemStateManager {

    private Map<String, Boolean> itemStates = new HashMap<>();

    public boolean itemActive(String item) {
        if (itemStates.containsKey(item)) {
            return itemStates.get(item);
        } else {
            itemStates.put(item, Boolean.TRUE);
            return true;
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public boolean flipItem(String item) {
        itemStates.put(item, !itemActive(item));
        return Optional.ofNullable(itemStates.get(item)).orElse(Boolean.TRUE);
    }

}
