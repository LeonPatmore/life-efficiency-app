package com.leon.patmore.life.efficiency

import com.leon.patmore.life.efficiency.client.LifeEfficiencyClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext

class AutoCompleteService(
    private val lifeEfficiencyClient: LifeEfficiencyClient,
) {
    fun getExistingItems(): List<String> =
        runBlocking {
            withContext(Dispatchers.Default) {
                val history = lifeEfficiencyClient.getHistory().map { it.name }
                val repeatingItems = lifeEfficiencyClient.getRepeatingItems()
                (history + repeatingItems).map { it.lowercase() }.toSet().toList()
            }
        }
}
