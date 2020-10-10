package com.example.life.efficiency.client;

import java.util.List;

public interface LifeEfficiencyClient {

    List<String> getTodayItems();

    void acceptTodayItems();

    void addPurchase(String name, int quantity);

}
