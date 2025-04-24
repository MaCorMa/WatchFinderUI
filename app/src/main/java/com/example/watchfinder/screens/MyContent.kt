package com.example.watchfinder.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun MyContent(
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),
        verticalArrangement = Arrangement.Center
    ) {
        Text("My content", style = MaterialTheme.typography.headlineMedium)

        Button(modifier = Modifier
            .fillMaxWidth()
            .padding(10.dp), onClick = {}) { Text("pelisfavs") }
        Button(modifier = Modifier
            .fillMaxWidth()
            .padding(10.dp), onClick = {}) { Text("seriesfavs") }
        Button(modifier = Modifier
            .fillMaxWidth()
            .padding(10.dp), onClick = {}) { Text("Mis colecciones") }
    }
}