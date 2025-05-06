package com.example.watchfinder.api

import com.example.watchfinder.data.prefs.TokenManager
import okhttp3.Interceptor
import okhttp3.Response

//Esto añade el token al header en cada petición, cuando un user se ha logeado.
class AInterceptor(private val tokenM: TokenManager): Interceptor {
    /*
    //Hay que implementar este método porque estamos implementando Interceptor
    override fun intercept(chain: Interceptor.Chain): Response {
        //Obtiene (intercepta) la solicitud que se iba a enviar
        val original = chain.request()
        //Intenta conseguir el token de los sharedpreferences (que previamente ya hemos creado).
        //Si da null, (operador elvis) te quedas sin token y entrarás donde puedas ir sin él.
        val token = tokenM.getToken() ?: return chain.proceed(original)
        //Si lo obtiene, construye una nueva solicitud con el header Authorization + el token.
        val request = original.newBuilder()
            .header("Authorization", "Bearer $token")
            .build()
        return chain.proceed(request)
    }
     */
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val url = request.url.toString()
        val isGcsUrl = url.contains("storage.googleapis.com") // Identifica las URLs de GCS
        val builder = request.newBuilder()

        if (!isGcsUrl) {
            val token = tokenM.getToken()
            if (!token.isNullOrBlank()) {
                builder.addHeader("Authorization", "Bearer $token")
            }
        }

        return chain.proceed(builder.build())
    }
}