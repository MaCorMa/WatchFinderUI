// WF/watchfinderAndroid/MainActivity.kt
package com.example.watchfinder


import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.watchfinder.data.UserManager
import com.example.watchfinder.ui.theme.WatchFinderTheme
import com.example.watchfinder.data.prefs.TokenManager
import com.example.watchfinder.repository.AuthRepository
import com.example.watchfinder.screens.DiscoverMovies
import com.example.watchfinder.screens.DiscoverSeries
import com.example.watchfinder.screens.ForgotPassword
import com.example.watchfinder.screens.Login
import com.example.watchfinder.screens.MovieOrSeries
import com.example.watchfinder.screens.MyContent
import com.example.watchfinder.screens.Profile
import dagger.hilt.android.AndroidEntryPoint
import com.example.watchfinder.screens.Register
import com.example.watchfinder.data.dto.MovieCard
import com.example.watchfinder.data.dto.SeriesCard
import com.example.watchfinder.screens.*
import javax.inject.Inject
import androidx.lifecycle.lifecycleScope
import com.example.watchfinder.screens.ResetPassword
import kotlinx.coroutines.launch

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

        //deep linking para reset contraseña
        handleIntent(intent)

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

    //Intents nuevos con la app ya en marcha
    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        handleIntent(intent)
    }
    // saca token al clickar el deep link
    private fun handleIntent(intent: Intent?) {
        val uri = intent?.data
        if (uri != null) {
            // saca el token de la URI
            val token = uri.getQueryParameter("token")
            if (token != null) {
                Log.d("DeepLink", "Token para reset recibido: $token")
                // guarda token
                userManager.setResetToken(token)
            }
        }
    }
}

enum class AuthState {
    LOADING, // Comprobando el token
    AUTHENTICATED,
    UNAUTHENTICATED,
    PASSWORD_RESET,
    FORGOT_PASSWORD
}

enum class ShowScreen { LOGIN, REGISTER }

// Estado para la pantalla principal
enum class MainAppScreen {
    DISCOVER, SEARCH, MY_CONTENT, PROFILE, SEARCH_RESULTS, DETAIL // Añadido SEARCH_RESULTS
}

enum class DiscoverState {
    SELECTION,
    MOVIES,
    SERIES
}

@Composable
fun MainScreen(
    // Ahora recibe MainAppScreen en lugar de String
    currentScreen: MainAppScreen,
    onScreenChange: (MainAppScreen) -> Unit,
    onLogout: () -> Unit,
    onAccountDeleted: () -> Unit,
    onNavigateToDetail: (itemType: String, itemId: String) -> Unit,
    // Añadido: Lambda para navegar a resultados
    onNavigateToResults: (movies: List<MovieCard>, series: List<SeriesCard>) -> Unit
) {
    var discoverScreenState by remember { mutableStateOf(DiscoverState.SELECTION) }

    Scaffold(
        topBar = { TopHeader() },
        bottomBar = {
            BottomBar(
                current = currentScreen, // Pasa el estado MainAppScreen
                click = { newScreen ->
                    onScreenChange(newScreen) // Llama a la lambda con MainAppScreen
                    if (newScreen == MainAppScreen.DISCOVER) {
                        // Resetear Discover si se vuelve a él
                        discoverScreenState = DiscoverState.SELECTION
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(modifier = Modifier.padding(paddingValues)) {
            when (currentScreen) {
                // Pasa la lambda onNavigateToResults a Search
                MainAppScreen.SEARCH -> Search(
                    onNavigateToDetail = onNavigateToDetail,
                    onNavigateToResults = onNavigateToResults // <--- PASAR LAMBDA
                )

                MainAppScreen.MY_CONTENT -> MyContent(onNavigateToDetail = onNavigateToDetail)
                MainAppScreen.DISCOVER -> {
                    when (discoverScreenState) {
                        DiscoverState.SELECTION -> MovieOrSeries(
                            onMoviesClicked = { discoverScreenState = DiscoverState.MOVIES },
                            onSeriesClicked = { discoverScreenState = DiscoverState.SERIES }
                        )

                        DiscoverState.MOVIES -> DiscoverMovies(/* onNavigateToDetail si es necesario */) // Pasar onNavigateToDetail si DiscoverMovies lo necesita
                        DiscoverState.SERIES -> DiscoverSeries(/* onNavigateToDetail si es necesario */)   // Pasar onNavigateToDetail si DiscoverSeries lo necesita
                        // Nota: Actualmente DiscoverMovies/Series no llaman a onNavigateToDetail
                        // Si quisieras ir a detalles desde ahí, tendrías que pasarles la lambda
                    }
                }

                MainAppScreen.PROFILE -> Profile(
                    onLogoutClick = onLogout,
                    onAccountDeleted = onAccountDeleted
                )
                // Estos casos no deberían ocurrir aquí porque se manejan fuera de MainScreen
                MainAppScreen.SEARCH_RESULTS -> {}
                MainAppScreen.DETAIL -> {}
            }
        }
    }
}

@Composable
fun BottomBar(
    current: MainAppScreen, // Recibe MainAppScreen
    click: (MainAppScreen) -> Unit // Espera MainAppScreen
) {
    // Mapea los estados a la info necesaria para la barra
    val items = listOf(
        MainAppScreen.SEARCH to Icons.Filled.Search,
        MainAppScreen.MY_CONTENT to Icons.Filled.Favorite,
        MainAppScreen.DISCOVER to Icons.Filled.Star,
        MainAppScreen.PROFILE to Icons.Filled.Person
    )

    NavigationBar {
        items.forEach { (screen, icon) ->
            // Convierte el enum a String legible para la etiqueta si es necesario
            val label = when (screen) {
                MainAppScreen.SEARCH -> "Buscar"
                MainAppScreen.MY_CONTENT -> "Mi Cont." // Abreviado o completo
                MainAppScreen.DISCOVER -> "Descubrir"
                MainAppScreen.PROFILE -> "Perfil"
                else -> "" // No debería pasar para estos items
            }
            NavigationBarItem(
                selected = (screen == current),
                onClick = { click(screen) }, // Llama con el MainAppScreen
                icon = { Icon(icon, contentDescription = label) },
                label = { Text(label) }
            )
        }
    }
}

// TopHeader (sin cambios)
@Composable
fun TopHeader() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
        , // Ajusta altura si es necesario
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    )
    {
        Image(
            painter = painterResource(id = R.drawable.logocarta),
            contentDescription = "App Logo",
            modifier = Modifier.height(100.dp) // Ajusta tamaño logo si es necesario
        )
    }
}

@Composable
fun AppNavigation(
    tokenManager: TokenManager,
    userManager: UserManager,
    authRepository: AuthRepository
) {
    val context = LocalContext.current

    var loginOrRegister by remember { mutableStateOf(ShowScreen.LOGIN) }
    var authState by remember { mutableStateOf(AuthState.LOADING) }

    var previousMainScreen by remember { mutableStateOf(MainAppScreen.DISCOVER) }
    var currentMainScreen by remember { mutableStateOf(MainAppScreen.DISCOVER) }

    // --- Estado para guardar los resultados de búsqueda temporalmente ---
    var searchResultsMoviesState by remember { mutableStateOf<List<MovieCard>>(emptyList()) }
    var searchResultsSeriesState by remember { mutableStateOf<List<SeriesCard>>(emptyList()) }

    // --- Estado para manejar la pantalla de detalles (sin cambios) ---
    var detailScreenInfo by remember { mutableStateOf<Pair<String, String>?>(null) }


    // Efecto de validación de token (sin cambios)
    LaunchedEffect(Unit) {
        //si hay token de reseteo, ir directamente a reset password
        if (userManager.resetToken != null) {
            authState = AuthState.PASSWORD_RESET
            return@LaunchedEffect
        }
        val token = tokenManager.getToken()
        if (token == null) {
            authState = AuthState.UNAUTHENTICATED
            loginOrRegister = ShowScreen.LOGIN
        } else {
            val validationResult = authRepository.validate()
            if (validationResult.isSuccess) {
                authState = AuthState.AUTHENTICATED
                // Asegurarse de empezar en Discover al autenticarse
                currentMainScreen = MainAppScreen.DISCOVER
            } else {
                authState = AuthState.UNAUTHENTICATED
                loginOrRegister = ShowScreen.LOGIN
            }
        }
    }

    // --- Lógica de renderizado basada en el estado ---

    when (authState) {
        AuthState.LOADING -> {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        }
        AuthState.AUTHENTICATED -> {
            // Si estamos autenticados, decidimos qué pantalla principal mostrar
            // (Discover, Search, Results, Detail, etc.)
            when (currentMainScreen) {
                // Muestra Detail si detailScreenInfo tiene datos
                MainAppScreen.DETAIL -> {
                    val (itemType, itemId) = detailScreenInfo!!
                    DetailScreen(
                        itemType = itemType,
                        itemId = itemId,
                        onNavigateBack = {
                            detailScreenInfo = null // Limpia la info de detalle
                            currentMainScreen =
                                previousMainScreen // Vuelve a resultados (o a Search?)
                        }
                    )
                }
                // Muestra SearchResults si currentMainScreen es SEARCH_RESULTS
                MainAppScreen.SEARCH_RESULTS -> {
                    SearchResultsScreen(
                        resultsMovies = searchResultsMoviesState,
                        resultsSeries = searchResultsSeriesState,
                        onNavigateBack = {
                            currentMainScreen = MainAppScreen.SEARCH
                        }, // Vuelve a Search
                        onNavigateToDetail = { type, id ->
                            previousMainScreen = currentMainScreen
                            detailScreenInfo = Pair(type, id) // Guarda info para detalle
                            currentMainScreen = MainAppScreen.DETAIL // Cambia a pantalla de detalle
                        }
                    )
                }
                // Si no es Detail ni SearchResults, muestra MainScreen (con BottomBar)
                else -> {
                    MainScreen(
                        currentScreen = currentMainScreen, // Pasa el estado actual
                        onScreenChange = { newScreen ->
                            currentMainScreen =
                                newScreen // Actualiza el estado al cambiar de sección
                        },
                        onLogout = {
                            tokenManager.clearToken()
                            authState = AuthState.UNAUTHENTICATED
                            loginOrRegister = ShowScreen.LOGIN
                        },
                        // Lambda para navegar a Detail desde CUALQUIER pantalla dentro de MainScreen
                        onNavigateToDetail = { type, id ->
                            previousMainScreen = currentMainScreen
                            detailScreenInfo = Pair(type, id)
                            currentMainScreen = MainAppScreen.DETAIL
                        },
                        // NUEVO: Lambda para navegar a Resultados DESDE Search
                        onNavigateToResults = { movies, series ->
                            searchResultsMoviesState = movies // Guarda los resultados
                            searchResultsSeriesState = series
                            currentMainScreen =
                                MainAppScreen.SEARCH_RESULTS // Cambia a la pantalla de resultados
                        },
                        onAccountDeleted = {
                            Log.d("AppNavigation", "Account deleted callback invoked")
                            authState = AuthState.UNAUTHENTICATED
                            Log.d("AppNavigation", "authState set to: $authState")
                            loginOrRegister = ShowScreen.LOGIN
                            Log.d("AppNavigation", "loginOrRegister set to: $loginOrRegister")
                        }
                    )
                }
            }
        }
        AuthState.UNAUTHENTICATED -> {
            // Lógica de Login/Register (sin cambios)
            when (loginOrRegister) {
                ShowScreen.LOGIN -> Login(
                    onLoginSuccess = {
                        authState = AuthState.AUTHENTICATED
                        currentMainScreen = MainAppScreen.DISCOVER // Empieza en Discover
                    },
                    onForgotPasswordClick = {
                        println("--> Redirecting to pass reset screen.")
                        // Pasa a la pantalla para indicar correo y resetear pass
                        authState = AuthState.FORGOT_PASSWORD
                    },
                    onNavigateToRegister = { loginOrRegister = ShowScreen.REGISTER }
                )
                ShowScreen.REGISTER -> Register(
                    onRegisterSuccess = { loginOrRegister = ShowScreen.LOGIN },
                    onNavigateToLogin = { loginOrRegister = ShowScreen.LOGIN }
                )
            }
        }
        AuthState.PASSWORD_RESET -> {
            ResetPassword(
                token = userManager.resetToken ?:"",
                onSuccess = {
                    userManager.setResetToken(null)
                    authState = AuthState.UNAUTHENTICATED
                    loginOrRegister = ShowScreen.LOGIN
                },
                onCancel = {
                    userManager.setResetToken(null)
                    authState = AuthState.UNAUTHENTICATED
                    loginOrRegister = ShowScreen.LOGIN
                }
            )
        }
        AuthState.FORGOT_PASSWORD -> {
            ForgotPassword(
                onNavigateBack = {
                    authState = AuthState.UNAUTHENTICATED
                }
            )
        }
    }
}



/*
    // Previews (opcionalmente actualizarlas para usar MainAppScreen)
    @Preview(showBackground = true)
    @Composable
    fun MainScreenPreview() {
        WatchFinderTheme {
            MainScreen(
                currentScreen = MainAppScreen.DISCOVER, // Usa el enum
                onScreenChange = {},
                onLogout = {},
                onNavigateToDetail = { _, _ -> },
                onNavigateToResults = { _, _ -> } // Añade la nueva lambda
            )
        }
    }
*/
