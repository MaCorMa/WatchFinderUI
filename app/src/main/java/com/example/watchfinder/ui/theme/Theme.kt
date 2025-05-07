package com.example.watchfinder.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = AccentRed,           // Botones/acciones principales serán beige claro
    onPrimary = LightBeige,           // Texto sobre esos botones (oscuro)
    secondary = AccentRedDark,
    tertiary = Pink80,
    onSecondary = DarkRedTextOnContainer, // Texto oscuro sobre rojo brillante
    secondaryContainer = DarkRedContainer, // Contenedor rojo oscuro (ej: óvalo nav)
    onSecondaryContainer = AccentRedDark,  // Texto/Icono rojo brillante sobre contenedor oscuro
    background = DarkBackground,      // Fondo de pantallas (oscuro)
    onBackground = LightContent,      // Texto sobre fondo (claro)
    surface = DarkBackground,         // Fondo de Cards, BottomBar, etc. (oscuro)
    onSurface = LightContent,         // Texto sobre surfaces (claro)
    outline = LightOutline,           // Bordes (gris claro)
    onSurfaceVariant = LightOutline   // Texto/iconos apagados (gris claro)
)
private val LightColorScheme = lightColorScheme(
    primary = DarkText,             // Color principal (ej: botones) será texto/borde oscuro
    onPrimary = LightRedContainer,         // Texto sobre botones primarios (Beige)
    secondary = AccentRed,          // Color secundario = Rojo (ej: icono nav seleccionado)
    onSecondary = WhiteText,        // Texto sobre Rojo (Blanco)
    secondaryContainer = LightRedContainer, // Contenedor secundario (ej: óvalo nav seleccionado)
    onSecondaryContainer = DarkRedTextOnContainer, // Texto sobre contenedor secundario
    background = LightBeige,        // Fondo de pantallas
    onBackground = DarkText,        // Texto sobre fondo
    surface = LightBeige,           // Fondo de Cards, BottomBar, etc.
    onSurface = DarkText,           // Texto sobre surfaces
    outline = MediumGray,           // Bordes (ej: FilterChip no seleccionado)
    onSurfaceVariant = MediumGray   // Texto/iconos apagados (ej: nav no seleccionado)
)

@Composable
fun WatchFinderTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}