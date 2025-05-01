package com.example.watchfinder.api

import com.example.watchfinder.data.prefs.TokenManager
import okhttp3.Interceptor
import okhttp3.Response

//Esto añade el token al header en cada petición, cuando un user se ha logeado.
class AInterceptor(private val tokenM: TokenManager): Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val original = chain.request()
        val requestUrl = original.url.toString() // Obtener la URL

        // NO añadir token si es una ruta de autenticación
        if (requestUrl.contains("/auth/login") || requestUrl.contains("/auth/register")) {
            return chain.proceed(original) // Envía la petición original SIN token
        }

        // Para todas las demás rutas, intenta añadir el token
        val token = tokenM.getToken() ?: return chain.proceed(original)
        val request = original.newBuilder()
            .header("Authorization", "Bearer $token")
            .build()
        return chain.proceed(request)
    }
}