package com.arnis.dh.data

interface DiffUtilComparator {
    fun sameEntityAs(item: Any): Boolean = this == item
    fun sameContentsAs(item: Any): Boolean = this == item
}