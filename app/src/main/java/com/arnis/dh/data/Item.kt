package com.arnis.dh.data

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Entity
@Serializable
data class Item(
    val brand: String,
    @PrimaryKey val id: String,
    val image: String,
    @SerialName("is_active") val isActive: Boolean,
    val name: String,
    @Embedded val price: Price,
    @SerialName("published_at") val publishedAt: String,
    val sku: String
) : DiffUtilComparator {

    override fun sameEntityAs(item: Any): Boolean {
        return id == (item as? Item)?.id
    }
}