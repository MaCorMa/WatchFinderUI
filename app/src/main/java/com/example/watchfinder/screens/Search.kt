package com.example.watchfinder.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.watchfinder.ui.theme.WatchFinderTheme
import kotlinx.coroutines.selects.select

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun Search() {



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
        Spacer(
            modifier = Modifier.height(15.dp)
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            RadioButton(selected = false, onClick = null)
            Text("Pelis", style = MaterialTheme.typography.bodyMedium)
            Spacer(
                modifier = Modifier.width(15.dp)
            )
            RadioButton(selected = false, onClick = null)
            Text("Series", style = MaterialTheme.typography.bodyMedium)
            Spacer(
                modifier = Modifier.width(15.dp)
            )
            RadioButton(selected = false, onClick = null)
            Text("Ambos", style = MaterialTheme.typography.bodyMedium)
        }

        FlowRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally)
        ) {
            options.forEach { genre ->
                FilterChip(
                    selected = (selectedOption == genre),
                    onClick = {
                        selectedOption = genre
                    },
                    label = { Text(genre) },
                    leadingIcon = if (selectedOption == genre) { // Muestra icono si está seleccionado
                        { // Necesita ser un @Composable lambda
                            Icon(
                                imageVector = Icons.Filled.Done,
                                contentDescription = "Seleccionado",
                                modifier = Modifier.size(FilterChipDefaults.IconSize)
                            )
                        }
                    } else {
                        null // Sin icono si no está seleccionado
                    }
                )
            }
        }

        //El row donde van los botones, ojito, Alignment = alineación respecto al contenedor padre.
        //Arrangement = Alineación de los hijos.
        Spacer(
            modifier = Modifier.height(15.dp)
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            Arrangement.Center
        ) {
            Button(
                modifier = Modifier.width(180.dp),
                onClick = {}) { Text("Buscar") }

            Spacer(
                modifier = Modifier.width(15.dp)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SearchPreview() {
    // 1. Crea datos falsos (dummy) del tipo MovieCard (DTO)


    // 2. Llama a tu MovieCard real pasándole los datos falsos
    WatchFinderTheme { // Envuelve en tu tema si es necesario
        Search()
    }
}