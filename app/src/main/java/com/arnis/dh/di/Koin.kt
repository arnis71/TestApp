package com.arnis.dh.di

import android.app.Application
import androidx.lifecycle.ViewModelProvider
import androidx.room.Room
import com.arnis.dh.db.AppDatabase
import com.arnis.dh.service.WebApi
import com.arnis.dh.service.WebService
import com.arnis.dh.viewmodel.MainViewModel
import okhttp3.OkHttpClient
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import org.koin.dsl.module

fun Application.startDI() = startKoin {
    androidContext(applicationContext)
    modules(listOf(mainModule, networkModule))
}

val mainModule = module {
    // use manual VM instantiation to better coop with DI library (emulates same behaviour as default android impl)
    single { MainViewModel(get(), get()) }
    single { Room.databaseBuilder(androidContext(), AppDatabase::class.java, "db").build() }
}

val networkModule = module {
    single<WebApi> { WebService(get()) }
    single { OkHttpClient.Builder().build() }
}