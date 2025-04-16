package com.example.watchfinder

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class WatchFinderApp : Application() {

    override fun onCreate() {
        super.onCreate()

    }
}