package com.example.watchfinder.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
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
fun Search() {
    //Lo que escribe el usuario lo guardamos aquí, es un estado que debemos conservar, por eso lo ponemos
    //con mutableStateOf
    var userInput by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(10.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
        //El título en la pantalla
    ) {
        Text("Busqueda", style = MaterialTheme.typography.headlineLarge)
        //El campo de búsqueda, tenemos que pasarle un value, que es lo que introduce el user
        //empieza en "" y cuando ese valor cambia, se asigna a userInput
        TextField(
            value = userInput,
            onValueChange = { newText -> userInput = newText },
            label = { Text("Introduce el nombre de la peli o serie") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(5.dp)
        )
        //El row donde van los botones, ojito, Alignment = alineación respecto al contenedor padre.
        //Arrangement = Alineación de los hijos.
        Row(
            modifier = Modifier.fillMaxWidth(),
            Arrangement.Center
        ) {
            Button(
                onClick = {}) { Text("Buscar") }

            Spacer(
                modifier = Modifier.width(15.dp)
            )

            Button(onClick = {}) { Text("Reset") }
        }
    }
}