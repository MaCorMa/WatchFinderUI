package com.example.watchfinder.di

import android.content.Context
import com.example.watchfinder.data.UserManager
import com.example.watchfinder.data.Utils
import com.example.watchfinder.data.prefs.TokenManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module // <-- Otro módulo con recetas
@InstallIn(SingletonComponent::class) // <-- También para toda la app
object AppModule {

    // Receta para crear TokenManager
    @Provides
    @Singleton
    fun provideTokenManager(@ApplicationContext context: Context): TokenManager {
        return TokenManager(context)
    }
    @Provides
    @Singleton // Debe coincidir con el scope de UserManager
    fun provideUserManager(@ApplicationContext appContext: Context): UserManager {
        // Hilt proporcionará el ApplicationContext
        // Devuelve una instancia de UserManager
        return UserManager(appContext)
    }
}