package com.arnis.dh.service

import com.arnis.dh.data.ItemsList
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import okhttp3.OkHttpClient
import okhttp3.Request

class WebService(private val client: OkHttpClient) : WebApi {

    override suspend fun getItems(): ItemsList? {
        return client.newCall(Request.Builder().get().url(URL).build()).execute().body()?.string()?.let {
            Json.parse(ItemsList.serializer(), it)
        }
    }

    companion object {
        private const val URL = "na"
    }
}

interface WebApi {
    suspend fun getItems(): ItemsList?
}