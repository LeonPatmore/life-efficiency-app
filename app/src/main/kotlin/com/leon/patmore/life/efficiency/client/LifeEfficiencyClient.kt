package com.leon.patmore.life.efficiency.client;

import com.leon.patmore.life.efficiency.client.domain.Goal
import com.leon.patmore.life.efficiency.client.domain.HistoryItem
import com.leon.patmore.life.efficiency.client.domain.ListItem
import com.leon.patmore.life.efficiency.client.domain.RepeatingItem
import com.leon.patmore.life.efficiency.client.domain.TodoItem
import com.leon.patmore.life.efficiency.client.domain.WeeklyItem

interface LifeEfficiencyClient {

    fun getListItems(): List<ListItem>

    fun getTodayItems(): List<String>

    fun addPurchase(name: String, quantity: Int)

    fun addToList(name: String, quantity: Int)

    fun completeItems(items: List<String>)

    fun getRepeatingItems(): List<String>

    fun getRepeatingItemsDetails(): Map<String, RepeatingItem>

    fun addRepeatingItem(item: String)

    fun deleteListItem(name: String, quantity: Int)

    fun completeItem(name: String, quantity: Int)

    fun getHistory() : List<HistoryItem>

    fun todoNonCompleted() : List<TodoItem>

    fun todoList(status: String? = null, sorted: Boolean = false) : List<TodoItem>

    fun updateTodoItemStatus(id: Int, status: String)

    fun addTodo(desc: String)

    fun getWeekly(): List<WeeklyItem>

    fun completeWeeklyItem(id: Int)

    fun getGoals(): Map<String, Map<String, List<Goal>>>

}
