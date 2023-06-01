package com.leon.patmore.life.efficiency.client.domain

data class ListItem(val name: String, val quantity: Int) {
    override fun toString(): String {
        return "$name X $quantity"
    }
}
