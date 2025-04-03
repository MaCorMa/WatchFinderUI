package com.example.watchfinder


import android.os.Bundle
import android.widget.DatePicker
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.watchfinder.ui.theme.WatchFinderTheme
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.IntOffset
import com.example.watchfinder.api.RetroFitClient
import com.example.watchfinder.data.dto.LoginRequest
import com.example.watchfinder.data.prefs.TokenManager
import com.example.watchfinder.repository.AuthRepository
import com.example.watchfinder.screens.Achievements
import com.example.watchfinder.screens.Discover
import com.example.watchfinder.screens.MyContent
import com.example.watchfinder.screens.Profile
import com.example.watchfinder.screens.Register
import com.example.watchfinder.screens.Search
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Calendar
import kotlin.math.abs
import kotlin.math.roundToInt


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        println("Iniciando...")
        RetroFitClient.initialize(this)

        val apiService = RetroFitClient.instance
        val tokenManager = TokenManager(this)
        val authRepository = AuthRepository(apiService, tokenManager)
        testApiLogin(authRepository)


        /*setContent {
            WatchFinderTheme {
                MainScreen()
            }
        }*/
    }
}

private fun testApiLogin(authRepo: AuthRepository) {

    CoroutineScope(Dispatchers.IO).launch {
        val result = authRepo.login("testii", "asdf")
        withContext(Dispatchers.Main) {
            when {
                result.isSuccess -> {
                    val loginResponse = result.getOrNull()
                    println("Exito: ${loginResponse?.token}")
                }
                result.isFailure -> {
                    println("Fallo en el login")
                }
            }
        }
    }
}

private fun testApiRegister(authRepo: AuthRepository) {

    CoroutineScope(Dispatchers.IO).launch {
        val result = authRepo.register("taaas", "testii", "asdf", "aaa@")
        withContext(Dispatchers.Main) {
            when {
                result.isSuccess -> {
                    val response = result.getOrNull() // Obtiene el String si es exitoso
                    println("Éxito: $response")
                }
                result.isFailure -> {
                    val exception = result.exceptionOrNull() // Obtiene la excepción si falla
                    println("Fallo en el registro: ${exception?.message}")
                }
            }
        }
    }
}

private const val nextCard = 0.9f

@Composable
fun MainScreen() {
    var current by remember { mutableStateOf("Discover") }
    Scaffold(
        bottomBar = {
            BottomBar(
                current = current,
                click = { newSection -> current = newSection }
            )
        }
    ) { paddingValues ->
        Column(modifier = Modifier.padding(paddingValues))
        {
            when (current) {
                "Search" -> Search()
                "My Content" -> MyContent()
                "Discover" -> Discover()
                "Achievements" -> Achievements()
                "Profile" -> Profile()
            }
        }
    }
}


@Composable
fun BottomBar(current: String, click: (String) -> Unit) {
    val items = listOf("Search", "My Content", "Discover", "Achievements", "Profile")
    NavigationBar {

        items.forEach { sectionName ->
            NavigationBarItem(
                selected = (sectionName == current),
                onClick = { click(sectionName) },
                icon = {
                    when (sectionName) {
                        "Search" -> Icon(Icons.Filled.Search, contentDescription = sectionName)
                        "My Content" -> Icon(
                            Icons.Filled.Favorite,
                            contentDescription = sectionName
                        )

                        "Discover" -> Icon(Icons.Filled.Star, contentDescription = sectionName)
                        "Achievements" -> Icon(
                            Icons.Filled.Warning,
                            contentDescription = sectionName
                        )

                        "Profile" -> Icon(Icons.Filled.Person, contentDescription = sectionName)
                    }
                }, label = { Text(sectionName) }
            )
        }
    }

}


@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    WatchFinderTheme {
        Register()
    }
}