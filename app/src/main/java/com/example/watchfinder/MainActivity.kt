package com.example.watchfinder


import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.example.watchfinder.data.UserManager
import com.example.watchfinder.ui.theme.WatchFinderTheme
import com.example.watchfinder.data.prefs.TokenManager
import com.example.watchfinder.repository.AuthRepository
import com.example.watchfinder.screens.Achievements
import com.example.watchfinder.screens.DetailScreen
import com.example.watchfinder.screens.DiscoverMovies
import com.example.watchfinder.screens.DiscoverSeries
import com.example.watchfinder.screens.Login
import com.example.watchfinder.screens.MovieOrSeries
import com.example.watchfinder.screens.MyContent
import com.example.watchfinder.screens.Profile
import com.example.watchfinder.screens.Register
import com.example.watchfinder.screens.Search
import dagger.hilt.android.AndroidEntryPoint

import javax.inject.Inject


@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var tokenManager: TokenManager

    @Inject
    lateinit var userManager: UserManager

    @Inject
    lateinit var authRepository: AuthRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        println("Iniciando...")

        //testApiRegister(authRepository)
        //testApiLogin(authRepository)

        setContent {
            WatchFinderTheme {
                AppNavigation(
                    tokenManager = tokenManager,
                    userManager = userManager,
                    authRepository = authRepository
                )
            }
        }
    }
}

enum class AuthState {
    LOADING, // Comprobando el token
    AUTHENTICATED,
    UNAUTHENTICATED
}

enum class ShowScreen {
    LOGIN,
    REGISTER
}

@Composable
fun AppNavigation(
    tokenManager: TokenManager,
    userManager: UserManager,
    authRepository: AuthRepository // Necesitas pasar el repositorio
) {

    var loginOrRegister by remember { mutableStateOf(ShowScreen.LOGIN) }
    var authState by remember { mutableStateOf(AuthState.LOADING) } // Empieza cargando
    var detailScreenInfo by remember { mutableStateOf<Pair<String, String>?>(null) }

    // Efecto que se ejecuta una vez al inicio para validar el token
    LaunchedEffect(Unit) {
        val token = tokenManager.getToken()
        if (token == null) {
            println("--> No token found. Unauthenticated.")
            authState = AuthState.UNAUTHENTICATED
            loginOrRegister = ShowScreen.LOGIN
        } else {
            println("--> Token found. Validating...")
            // Intenta validar el token con el backend
            val validationResult = authRepository.validate() // Llama al repo
            if (validationResult.isSuccess) {
                println("--> Token validation SUCCESS. Authenticated.")
                // Opcional: Podrías querer recargar datos del usuario aquí si es necesario
                authState = AuthState.AUTHENTICATED
            } else {
                println("--> Token validation FAILED. Unauthenticated. Error: ${validationResult.exceptionOrNull()?.message}")
                // El repo ya debería haber limpiado el token inválido si falló por 401/403
                authState = AuthState.UNAUTHENTICATED
                loginOrRegister = ShowScreen.LOGIN
            }
        }
    }

    if (detailScreenInfo != null) {
        val (itemType, itemId) = detailScreenInfo!! // Desempaqueta la info
        DetailScreen( // Muestra la pantalla de detalles
            itemType = itemType,
            itemId = itemId,
            onNavigateBack = { detailScreenInfo = null } // Lambda para volver (limpia el estado)
        )
    } else {
        // Muestra la UI según el estado de autenticación
        when (authState) {
            AuthState.LOADING -> {
                // Muestra un indicador de carga mientras se valida
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }

            AuthState.AUTHENTICATED -> {
                MainScreen(
                    onLogout = {
                        println("--> Logging out...")
                        tokenManager.clearToken()
                        userManager.clearCurrentUser()
                        authState = AuthState.UNAUTHENTICATED
                        loginOrRegister = ShowScreen.LOGIN
                    }
                )
            }

            AuthState.UNAUTHENTICATED -> {
                when (loginOrRegister) {
                    ShowScreen.LOGIN -> {
                        Login(
                            onLoginSuccess = {
                                println("--> Login Success! Setting state to Authenticated.")
                                // Tras login exitoso, asumimos que el token es válido
                                authState = AuthState.AUTHENTICATED
                            },
                            onNavigateToRegister = {
                                loginOrRegister = ShowScreen.REGISTER
                            }
                        )
                    }

                    ShowScreen.REGISTER -> {
                        Register(
                            onRegisterSuccess = { loginOrRegister = ShowScreen.LOGIN },
                            onNavigateToLogin = { loginOrRegister = ShowScreen.LOGIN }
                        )
                    }
                }
            }
        }
    }
}


enum class DiscoverState {
    SELECTION,
    MOVIES,
    SERIES
}

@Composable
fun MainScreen(onLogout: () -> Unit,
               onNavigateToDetail: (itemType: String, itemId: String) -> Unit) {
    var current by remember { mutableStateOf("Discover") }
    var discoverScreenState by remember { mutableStateOf(DiscoverState.SELECTION) }

    Scaffold(
        bottomBar = {
            BottomBar(
                current = current,
                click = { newSection ->
                    current = newSection
                    if (newSection == "Discover") {
                        discoverScreenState = DiscoverState.SELECTION
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(modifier = Modifier.padding(paddingValues))
        {
            when (current) {
                "Search" -> Search(onNavigateToDetail = onNavigateToDetail)
                "My Content" -> MyContent(onNavigateToDetail = onNavigateToDetail)
                "Discover" -> {
                    // Decide qué mostrar DENTRO de Discover
                    when (discoverScreenState) {
                        DiscoverState.SELECTION -> MovieOrSeries(
                            onMoviesClicked = { discoverScreenState = DiscoverState.MOVIES },
                            onSeriesClicked = { discoverScreenState = DiscoverState.SERIES }
                        )

                        DiscoverState.MOVIES -> DiscoverMovies()
                        DiscoverState.SERIES -> DiscoverSeries()
                    }
                }

                "Achievements" -> Achievements()
                "Profile" -> Profile(onLogoutClick = onLogout)
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