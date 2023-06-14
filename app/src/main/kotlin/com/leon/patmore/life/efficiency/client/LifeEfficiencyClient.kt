package com.leon.patmore.life.efficiency.client;

import com.leon.patmore.life.efficiency.client.domain.ListItem
import com.leon.patmore.life.efficiency.client.domain.TodoItem

interface LifeEfficiencyClient {

    fun getListItems(): List<ListItem>

    fun getTodayItems(): List<String>

    fun acceptTodayItems()

    fun addPurchase(name: String, quantity: Int)

    fun addToList(name: String, quantity: Int)

    fun completeItems(items: List<String>)

    fun getRepeatingItems(): List<String>

    fun addRepeatingItem(item: String)

    fun deleteListItem(name: String, quantity: Int)

    fun completeItem(name: String, quantity: Int)

    fun getHistory() : List<ListItem>

    fun todoList() : List<TodoItem>

    fun updateTodoItemStatus(id: Int, status: String)

}
