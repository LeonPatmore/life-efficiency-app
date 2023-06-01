package com.leon.patmore.life.efficiency;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;

public class ItemStateManager {

    private final Map<String, Boolean> itemStates = new HashMap<>();

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

    public boolean flipItem(String item) {
        itemStates.put(item, !itemActive(item));
        return Optional.ofNullable(itemStates.get(item)).orElse(Boolean.TRUE);
    }

    public boolean allActive() {
        return itemStates.values().stream().allMatch(new Predicate<Boolean>() {
            @Override
            public boolean test(Boolean ifActive) {
                return ifActive;
            }
        });
    }

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
