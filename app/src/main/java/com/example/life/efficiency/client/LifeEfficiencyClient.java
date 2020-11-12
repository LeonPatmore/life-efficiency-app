package com.example.life.efficiency.client;

import java.util.List;

public interface LifeEfficiencyClient {

    List<String> getTodayItems() throws LifeEfficiencyException;

    void acceptTodayItems() throws LifeEfficiencyException;

    void addPurchase(String name, int quantity) throws LifeEfficiencyException;

    void addToList(String name, int quantity) throws LifeEfficiencyException;

    void completeItems(List<String> items) throws LifeEfficiencyException;

}
