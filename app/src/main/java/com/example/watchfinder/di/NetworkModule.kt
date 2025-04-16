package com.example.watchfinder.di

import com.example.watchfinder.api.AInterceptor
import com.example.watchfinder.api.ApiService
import com.example.watchfinder.data.prefs.TokenManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory // O tu convertidor
import javax.inject.Singleton

@Module // <-- Le dice a Hilt que esto contiene recetas de creación
@InstallIn(SingletonComponent::class) // <-- Estas recetas son para toda la app (Singletons)
object NetworkModule {

    // Receta para crear AInterceptor
    @Provides
    @Singleton
    fun provideAuthInterceptor(tokenManager: TokenManager): AInterceptor {
        // Hilt buscará cómo crear TokenManager (ver AppModule) y lo pasará aquí
        return AInterceptor(tokenManager)
    }

    // Receta para crear OkHttpClient
    @Provides
    @Singleton
    fun provideOkHttpClient(aInterceptor: AInterceptor): OkHttpClient {
        // Hilt buscará cómo crear AInterceptor (la receta de arriba) y lo pasará aquí
        val logging = HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.BODY }
        return OkHttpClient.Builder()
            .addInterceptor(aInterceptor) // <-- Usamos el interceptor creado
            .addInterceptor(logging)
            .build()
    }

    // Receta para crear Retrofit
    @Provides
    @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit {
        // Hilt buscará cómo crear OkHttpClient (la receta de arriba) y lo pasará aquí
        return Retrofit.Builder()
            .baseUrl("http://10.0.2.2:8080/") // <-- TU URL BASE REAL (10.0.2.2 es localhost desde emulador)
            .client(okHttpClient) // <-- Usamos el OkHttpClient creado
            .addConverterFactory(GsonConverterFactory.create()) // O el tuyo
            .build()
    }

    // Receta para crear ApiService (tu interfaz)
    @Provides
    @Singleton
    fun provideApiService(retrofit: Retrofit): ApiService {
        // Hilt buscará cómo crear Retrofit (la receta de arriba) y lo pasará aquí
        return retrofit.create(ApiService::class.java)
    }
}