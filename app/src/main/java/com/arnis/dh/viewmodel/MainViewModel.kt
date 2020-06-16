package com.arnis.dh.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.arnis.dh.data.Item
import com.arnis.dh.db.AppDatabase
import com.arnis.dh.service.WebApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlin.coroutines.CoroutineContext

class MainViewModel(private val webApi: WebApi, private val db: AppDatabase) : ViewModel() {
    private val items = MutableLiveData<List<Item>>()
    private val previewItem = MutableLiveData<Item>()
    private val isRefreshing = MutableLiveData<Boolean>(false)
    private var updateJob: Job? = null

    init {
        updateData()
    }

    fun getItems(): LiveData<List<Item>> = items
    fun getPreviewItem(): LiveData<Item> = previewItem
    fun getRefreshing(): LiveData<Boolean> = isRefreshing

    fun updateData() {
        updateJob?.cancel()
        updateJob = viewModelScope.launch(Dispatchers.IO) {
            isRefreshing.postValue(true)
            try {
                db.itemDao().insertAll(webApi.getItems()?.items.orEmpty())
            } catch (e: Exception) {
            }
            items.postValue(db.itemDao().getAll().prepare())
            isRefreshing.postValue(false)
        }
    }

    fun preview(item: Item) {
        previewItem.value = item
    }

    fun previewDeepLink(url: String) {
        url.split("/").lastOrNull()?.let { id ->
            viewModelScope.launch {
                updateJob?.join()
                items.value.orEmpty().firstOrNull { it.id == id }?.let(this@MainViewModel::preview)
            }
        }
    }

    private inline fun List<Item>.prepare() = filter { it.isActive }.sortedBy(Item::name)
}
