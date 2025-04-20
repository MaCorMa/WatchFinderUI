package com.example.watchfinder.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.watchfinder.viewmodels.LoginViewModel

@Composable
fun Login(
    viewModel: LoginViewModel = hiltViewModel(),
    onLoginSuccess: () -> Unit,
    onForgotPasswordClick: () -> Unit
) {

    val uiState by viewModel.uiState.collectAsState()


    LaunchedEffect(uiState.loginSuccess) {
        if (uiState.loginSuccess) {
            onLoginSuccess()
            viewModel.onLoginNavigated()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("Bienvenido", style = MaterialTheme.typography.headlineLarge)
        TextField(
            value = uiState.usernameInput,
            onValueChange = viewModel::onUsernameChange,
            label = { Text("Usuario") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(5.dp),
            isError = uiState.loginError != null, // Marcar si hay error
            singleLine = true
        )
        TextField(
            value = uiState.passwordInput,
            onValueChange = viewModel::onPasswordChange,
            label = { Text("Contraseña") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(5.dp),
            visualTransformation = PasswordVisualTransformation(),
            isError = uiState.loginError != null, // Marcar si hay error
            singleLine = true
        )
        TextButton(
            onClick = onForgotPasswordClick,
            modifier = Modifier.align(Alignment.End)
        )
        {
            Text(
                "Olvidé mi contraseña", style = MaterialTheme.typography.bodySmall,
                modifier = Modifier
                    //.align(Alignment.Start)
                    .padding(5.dp)
            )
        }

        Button(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp), onClick = viewModel::attemptLogin,
            enabled = !uiState.isLoading
        ) {
            if (uiState.isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    color = MaterialTheme.colorScheme.onPrimary,
                    strokeWidth = 2.dp
                )
            } else {
                Text("Acceder")
            }
        }

    }
}