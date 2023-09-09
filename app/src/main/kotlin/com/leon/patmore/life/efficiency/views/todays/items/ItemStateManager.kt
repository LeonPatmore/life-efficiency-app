package com.leon.patmore.life.efficiency.views.todays.items

class ItemStateManager {

    private val itemStates: MutableMap<String, Boolean> = mutableMapOf()

    fun clear() {
        itemStates.clear()
    }

    fun itemActive(item: String) = itemStates.computeIfAbsent(item) { true }

    fun flipItem(item: String) = itemStates.computeIfPresent(item) { _, b -> !b}!!

    fun getItems() = itemStates.keys

    fun getActiveItems(): List<String> = itemStates.filter { it.value }.map { it.key }

    operator fun set(item: String, value: Boolean) {
        itemStates[item] = value
    }

}
