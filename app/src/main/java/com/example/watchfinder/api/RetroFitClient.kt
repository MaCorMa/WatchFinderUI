/*package com.example.watchfinder.api

import android.content.Context
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import com.example.watchfinder.data.prefs.TokenManager
import okhttp3.OkHttpClient

object RetroFitClient {
    private const val BASE_URL = "http://10.0.2.2:8080"
    private var apiService: ApiService? = null
    val instance: ApiService
        get() {
            if (apiService == null) {
                // Si es null, significa que no se ha inicializado, así que excepción
                throw IllegalStateException("RetroFitClient must be initialized with a Context first")
            }
            // Si no es null, devolvemos la instancia de ApiService
            return apiService as ApiService
        }

    //Esta es la base de Retrofit, necesita la URL base y nuestra interfaz (cuyos métodos tienen los endpoints).
    //Al inicializar tenemos que darle el Context, a su vez ese contexto también se lo pasamos al TokenManager.
    //(Estaría bien aprender como van Hilt o Koin para inyectar dependencias, pero por ahora nos vale)
    fun initialize(context: Context) {
        if (apiService == null) {
            val tokenManager = TokenManager(context)
            //Le vamos a meter el interceptor
            val client = OkHttpClient.Builder()
                //Tenemos que pasarle nuestro gestor del token para las sharedpreferences.
                .addInterceptor(AInterceptor(tokenManager))
                .build()


            val retrofit = Retrofit.Builder()
                .baseUrl(BASE_URL)
                //Le pasamos nuestro OkHttpClient
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build()

            apiService = retrofit.create(ApiService::class.java)
        }
    }
}*/