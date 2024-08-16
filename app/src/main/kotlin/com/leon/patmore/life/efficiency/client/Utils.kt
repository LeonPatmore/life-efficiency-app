package com.leon.patmore.life.efficiency.client

object Utils {
    fun String?.toIntIgnoreNull(): Int? {
        if (this == "null") return null
        return this?.toInt()
    }
}
