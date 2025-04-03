package com.example.watchfinder.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun Achievements() {

    val achievs =
        listOf("logro0", "logro1", "logro2", "logro3", "logro4", "logro5", "logro6", "logro7")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(10.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("LOGROS", style = MaterialTheme.typography.headlineLarge)
        LazyColumn(
            modifier = Modifier.fillMaxWidth()
        ) {
            items(achievs) { achiev ->
                AchievementItem(achiev)

            }

        }
    }
}

@Composable
fun AchievementItem(title: String) {

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(5.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text("Icon")

        Spacer(modifier = Modifier.width(10.dp))

        Column(
        ) {
            Text(title)
            Text("descripcion")
        }
    }

}