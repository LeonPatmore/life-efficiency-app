package com.leon.patmore.life.efficiency.client;

import com.leon.patmore.life.efficiency.client.domain.ListItem;

import java.util.List;

public interface LifeEfficiencyClient {

    List<ListItem> getListItems() throws LifeEfficiencyException;

    List<String> getTodayItems() throws LifeEfficiencyException;

    void acceptTodayItems() throws LifeEfficiencyException;

    void addPurchase(String name, int quantity) throws LifeEfficiencyException;

    void addToList(String name, int quantity) throws LifeEfficiencyException;

    void completeItems(List<String> items) throws LifeEfficiencyException;

    List<String> getRepeatingItems() throws LifeEfficiencyException;

    void addRepeatingItem(String item) throws LifeEfficiencyException;

    void deleteListItem(String name, int quantity) throws LifeEfficiencyException;

    void completeItem(String name, int quantity) throws LifeEfficiencyException;

}
