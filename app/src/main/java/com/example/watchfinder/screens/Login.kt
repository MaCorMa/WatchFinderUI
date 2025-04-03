package com.example.watchfinder.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun Login() {

    var userInput by remember { mutableStateOf("") }
    var passInput by remember { mutableStateOf("") }


    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("Bienvenido", style = MaterialTheme.typography.headlineLarge)
        TextField(
            value = userInput,
            onValueChange = { newText -> userInput = newText },
            label = { Text("Usuario") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(5.dp)
        )
        TextField(
            value = passInput,
            onValueChange = { newText -> passInput = newText },
            label = { Text("Contraseña") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(5.dp)
        )
        Text(
            "Olvidé mi contraseña", style = MaterialTheme.typography.bodySmall,
            modifier = Modifier
                .align(Alignment.Start)
                .padding(5.dp)
        )
        Button(modifier = Modifier
            .fillMaxWidth()
            .padding(10.dp), onClick = {}) { Text("Acceder") }

    }
}