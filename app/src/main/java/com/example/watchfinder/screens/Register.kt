package com.example.watchfinder.screens

import android.app.DatePickerDialog // Importa DatePickerDialog
import android.widget.DatePicker // Importa DatePicker
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.watchfinder.ui.theme.WatchFinderTheme
import com.example.watchfinder.viewmodels.RegisterVM
// Importa tu RegisterUiState
// import com.example.watchfinder.data.UiState.RegisterUiState
import java.util.Calendar
import java.util.Locale // Para formatear la fecha

@Composable
fun Register(
    viewModel: RegisterVM = hiltViewModel(),
    onRegisterSuccess: () -> Unit, // Lambda para navegar (probablemente a Login)
    onNavigateToLogin: () -> Unit // Lambda para volver a la pantalla de Login
) {
    val uiState by viewModel.uiState.collectAsState()

    // Efecto para navegar cuando el registro sea exitoso
    LaunchedEffect(uiState.registrationSuccess) {
        if (uiState.registrationSuccess) {
            onRegisterSuccess() // Navega (ej. a Login)
            viewModel.onRegistrationNavigated() // Resetea el estado
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp, vertical = 10.dp) // Ajusta padding
            .imePadding(), // Añade padding para el teclado
        verticalArrangement = Arrangement.Center // Centra verticalmente
    ) {
        Text(
            "Registro",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )
        Spacer(modifier = Modifier.height(20.dp))

        // Campos de Texto (usando OutlinedTextField para consistencia)
        OutlinedTextField(
            value = uiState.nameInput,
            onValueChange = viewModel::onNameChange,
            label = { Text("Nombre completo") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
            singleLine = true,
            isError = uiState.registrationError?.contains("nombre", ignoreCase = true) == true
        )

        OutlinedTextField(
            value = uiState.nickInput,
            onValueChange = viewModel::onNickChange,
            label = { Text("Nickname (para login)") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
            singleLine = true,
            isError = uiState.registrationError?.contains("nick", ignoreCase = true) == true
                    || uiState.registrationError?.contains(
                "usuario",
                ignoreCase = true
            ) == true // Si el backend devuelve error de usuario
        )

        OutlinedTextField(
            value = uiState.emailInput,
            onValueChange = viewModel::onEmailChange,
            label = { Text("Email") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
            singleLine = true,
            isError = uiState.registrationError?.contains("email", ignoreCase = true) == true
        )

        OutlinedTextField(
            value = uiState.passwordInput,
            onValueChange = viewModel::onPasswordChange,
            label = { Text("Contraseña") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
            singleLine = true,
            visualTransformation = if (uiState.isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            trailingIcon = {
                val image = if (uiState.isPasswordVisible) Icons.Filled.Info else Icons.Filled.Done
                val description =
                    if (uiState.isPasswordVisible) "Ocultar contraseña" else "Mostrar contraseña"
                IconButton(onClick = viewModel::togglePasswordVisibility) {
                    Icon(imageVector = image, contentDescription = description)
                }
            },
            isError = uiState.registrationError?.contains("contraseña", ignoreCase = true) == true
        )

        OutlinedTextField(
            value = uiState.repeatPasswordInput,
            onValueChange = viewModel::onRepeatPasswordChange,
            label = { Text("Repetir contraseña") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
            singleLine = true,
            visualTransformation = if (uiState.isRepeatPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            trailingIcon = {
                val image =
                    if (uiState.isRepeatPasswordVisible) Icons.Filled.Info else Icons.Filled.Done
                val description =
                    if (uiState.isRepeatPasswordVisible) "Ocultar contraseña" else "Mostrar contraseña"
                IconButton(onClick = viewModel::toggleRepeatPasswordVisibility) {
                    Icon(imageVector = image, contentDescription = description)
                }
            },
            isError = uiState.registrationError?.contains(
                "contraseña",
                ignoreCase = true
            ) == true // Marca si el error es de contraseña
        )

        Spacer(modifier = Modifier.height(10.dp))

        // Selector de Fecha de Nacimiento
        AgeChoose(
            selectedDate = uiState.birthDateInput, // Muestra la fecha guardada en el state
            onDateSelected = viewModel::onBirthDateChange // Llama al VM cuando se selecciona
        )

        // Muestra el error general si existe
        if (uiState.registrationError != null) {
            Text(
                text = uiState.registrationError!!,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier
                    .padding(top = 8.dp)
                    .align(Alignment.CenterHorizontally)
            )
        } else {
            Spacer(modifier = Modifier.height(24.dp)) // Espacio para consistencia
        }


        // Botón Registrar
        Button(
            onClick = viewModel::attemptRegistration,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 10.dp)
                .height(50.dp),
            enabled = !uiState.isLoading
        ) {
            if (uiState.isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    color = MaterialTheme.colorScheme.onPrimary,
                    strokeWidth = 2.dp
                )
            } else {
                Text("Registrar usuario")
            }
        }

        // Enlace para volver a Login
        Text(
            text = "¿Ya tienes cuenta? Inicia sesión",
            modifier = Modifier
                .padding(top = 10.dp)
                .align(Alignment.CenterHorizontally)
                .clickable { onNavigateToLogin() }, // Llama a la lambda para navegar a Login
            color = MaterialTheme.colorScheme.primary
        )
    }
}

@Composable
fun AgeChoose(
    selectedDate: String, // La fecha actual seleccionada (formato DD/MM/YYYY o YYYY-MM-DD)
    onDateSelected: (String) -> Unit // Función para notificar la fecha seleccionada
) {
    val context = LocalContext.current
    val calendar = Calendar.getInstance()

    // Intentar parsear la fecha seleccionada para inicializar el diálogo
    // Si no hay fecha seleccionada, usa la fecha actual
    var initialYear = calendar.get(Calendar.YEAR)
    var initialMonth = calendar.get(Calendar.MONTH)
    var initialDay = calendar.get(Calendar.DAY_OF_MONTH)

    if (selectedDate.isNotEmpty()) {
        try {
            // Asume formato DD/MM/YYYY, ajusta si usas otro
            val parts = selectedDate.split("/")
            if (parts.size == 3) {
                initialDay = parts[0].toInt()
                initialMonth = parts[1].toInt() - 1 // Calendar.MONTH es 0-indexed
                initialYear = parts[2].toInt()
            } else {
                // Intenta formato YYYY-MM-DD
                val partsIso = selectedDate.split("-")
                if (partsIso.size == 3) {
                    initialYear = partsIso[0].toInt()
                    initialMonth = partsIso[1].toInt() - 1
                    initialDay = partsIso[2].toInt()
                }
            }
        } catch (e: Exception) {
            // Error al parsear, usa la fecha actual
            println("Error parsing date $selectedDate: ${e.message}")
        }
    }


    val datePickerDialog = remember { // Evita recrear el diálogo en cada recomposición
        DatePickerDialog(
            context,
            { _, year, month, dayOfMonth ->
                // Formatea la fecha consistentemente (DD/MM/YYYY o YYYY-MM-DD)
                // Ejemplo: DD/MM/YYYY
                val formattedDate =
                    String.format(Locale.getDefault(), "%02d/%02d/%d", dayOfMonth, month + 1, year)
                // Ejemplo: YYYY-MM-DD (mejor para enviar a API)
                // val formattedDate = String.format(Locale.getDefault(), "%d-%02d-%02d", year, month + 1, dayOfMonth)
                onDateSelected(formattedDate) // Notifica la fecha seleccionada
            },
            initialYear,
            initialMonth,
            initialDay
        ).apply {
            // Opcional: poner límite máximo (hoy) para que no elijan fechas futuras
            datePicker.maxDate = calendar.timeInMillis
        }
    }

    // Botón que muestra la fecha seleccionada y abre el diálogo
    OutlinedButton(
        onClick = { datePickerDialog.show() },
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        Text(if (selectedDate.isBlank()) "Seleccionar fecha de nacimiento" else "Nacido el: $selectedDate")
    }
}

