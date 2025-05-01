package com.example.watchfinder

import android.app.Application
// --- Imports correctos para Coil 3 y la Factory ---
import coil3.ImageLoader
import coil3.SingletonImageLoader // Importar SingletonImageLoader para acceder a la Factory
import coil3.PlatformContext // Usar PlatformContext o Context
// -----------------------------------------------
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject
import javax.inject.Provider

@HiltAndroidApp
// --- Implementar SingletonImageLoader.Factory ---
class WatchFinderApp : Application(), SingletonImageLoader.Factory {

    @Inject
    lateinit var imageLoaderProvider: Provider<ImageLoader> // Hilt provee el ImageLoader de CoilModule

    override fun onCreate() {
        super.onCreate()
    }

    override fun newImageLoader(context: PlatformContext): ImageLoader {
        return imageLoaderProvider.get()
    }
}