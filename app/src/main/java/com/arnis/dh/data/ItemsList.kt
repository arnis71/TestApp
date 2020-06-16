package com.arnis.dh.data

import kotlinx.serialization.Serializable

@Serializable
data class ItemsList(val items: List<Item>)