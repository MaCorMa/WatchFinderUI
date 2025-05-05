package com.example.watchfinder.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.watchfinder.ui.theme.WatchFinderTheme
import com.example.watchfinder.viewmodels.DiscoverMoviesVM
import com.example.watchfinder.viewmodels.DiscoverSeriesVM

@Composable
fun MovieOrSeries(    onMoviesClicked: () -> Unit,
                      onSeriesClicked: () -> Unit) {

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
        //El título en la pantalla
    ) {
        Button(
            modifier = Modifier.fillMaxWidth().padding(10.dp).height(50.dp),
            onClick = onMoviesClicked) { Text("Quiero ver Pelis") }

        Button(
            modifier = Modifier.fillMaxWidth().padding(10.dp).height(50.dp),
            onClick = onSeriesClicked ) { Text("Quiero ver Series") }


    }
}

@Preview(showBackground = true)
@Composable
fun MovieOrSeriesPreview() { // Cambiado el nombre para seguir convención
    WatchFinderTheme {
        MovieOrSeries(
            onMoviesClicked = {}, // <-- Lambda vacía para la acción de pelis
            onSeriesClicked = {}  // <-- Lambda vacía para la acción de series
        )
    }
}