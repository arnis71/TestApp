package com.arnis.dh.data

import androidx.room.Entity
import kotlinx.serialization.Serializable

@Entity(primaryKeys = ["current", "original"])
@Serializable
data class Price(
    val currency: String,
    val current: Double,
    val original: Double
)