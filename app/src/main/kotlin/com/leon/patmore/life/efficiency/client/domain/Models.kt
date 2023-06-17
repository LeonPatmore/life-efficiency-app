package com.leon.patmore.life.efficiency.client.domain

import java.lang.RuntimeException

data class ListItem(val name: String, val quantity: Int) {
    override fun toString(): String {
        return "$name X $quantity"
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
