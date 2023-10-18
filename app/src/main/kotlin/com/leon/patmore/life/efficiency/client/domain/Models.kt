package com.leon.patmore.life.efficiency.client.domain

import java.lang.RuntimeException
import java.time.DayOfWeek
import java.time.format.TextStyle
import java.util.Locale.ENGLISH

data class ListItem(val name: String, val quantity: Int) {
    override fun toString(): String {
        return "$name X $quantity"
    }
}

data class HistoryItem(val name: String, val quantity: Int, val date_purchased: String) {
    override fun toString(): String {
        return "$name X $quantity | $date_purchased"
    }
}

data class WeeklyItem(val id: Int, val day: Int, val desc: String, val complete: Boolean) {
    override fun toString(): String {
        val dayOfWeek = DayOfWeek.of(day).getDisplayName(TextStyle.SHORT, ENGLISH)
        return "$dayOfWeek: $desc"
    }
}

class LifeEfficiencyException : RuntimeException {
    constructor(message: String?) : super(message)
    constructor(message: String?, cause: Throwable?) : super(message, cause)
}

data class TodoItem(val id: Int,
                    val desc: String,
                    val status: String,
                    val date_added: String,
                    val date_done: String)

data class Goal(val name: String, val progress: String)

data class RepeatingItem(val averageGapDays: Int?, val daysSinceLastBought: Int?)
