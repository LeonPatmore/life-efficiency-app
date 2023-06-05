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
