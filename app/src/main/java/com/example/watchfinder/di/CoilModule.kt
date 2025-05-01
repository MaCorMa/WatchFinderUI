package com.example.watchfinder.di

// --- Imports para Coil 3 ---
import android.content.Context // Sigue siendo Context en Android
import coil3.ImageLoader
import coil3.network.okhttp.OkHttpNetworkFetcherFactory // Si usas okhttp
import coil3.request.crossfade
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object CoilModule {

    @Provides
    @Singleton
    fun provideImageLoader(
        // Usar PlatformContext o directamente Context
        @ApplicationContext context: Context,
        okHttpClient: OkHttpClient // El OkHttpClient de Hilt
    ): ImageLoader {
        return ImageLoader.Builder(context)
            // --- Usar el OkHttpClient de Hilt con Coil 3 ---
            .components {
                add(OkHttpNetworkFetcherFactory(okHttpClient)) // Configura el fetcher de red con tu OkHttpClient
            }
            // ---------------------------------------------
            .crossfade(true)
            // .logger(DebugLogger()) // DebugLogger de Coil 3
            // .components { // Si necesitas SVG con Coil 3
            //     add(SvgDecoder.Factory())
            // }
            .build()
    }
}