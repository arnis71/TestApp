package com.arnis.dh

import android.app.Application
import com.arnis.dh.di.startDI

class MainApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        startDI()
    }
}