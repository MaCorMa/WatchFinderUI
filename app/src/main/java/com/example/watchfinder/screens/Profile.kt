package com.example.watchfinder.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp


@Composable
fun Profile() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("imgprofile")
            Spacer(modifier = Modifier.width(15.dp))
            Column {
                Text("Nick")
                Text("Nombre")
            }
        }
        Spacer(modifier = Modifier.height(10.dp))

        Row(
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Email")
            Spacer(modifier = Modifier.width(5.dp))
            Icon(Icons.Filled.Edit, contentDescription = "Edit")
        }
        Spacer(modifier = Modifier.height(10.dp))
        Row(
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Pass")
            Spacer(modifier = Modifier.width(5.dp))
            Icon(Icons.Filled.Edit, contentDescription = "Edit")
        }
        Spacer(modifier = Modifier.height(10.dp))
        Row(
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("2FA?")
            Spacer(modifier = Modifier.width(5.dp))
            Icon(Icons.Filled.Edit, contentDescription = "Edit")
        }
        Spacer(modifier = Modifier.height(10.dp))
        Text("Dark mode?")
        Button(modifier = Modifier
            .fillMaxWidth()
            .padding(10.dp), onClick = {}) { Text("Guardar") }
    }
}
