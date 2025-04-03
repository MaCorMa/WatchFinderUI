package com.example.watchfinder.screens

import android.widget.DatePicker
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import java.util.Calendar

@Composable
fun Register() {
    var userInput by remember { mutableStateOf("") }
    var nickInput by remember { mutableStateOf("") }
    var passInput by remember { mutableStateOf("") }
    var userInputRepeat by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(10.dp),
        verticalArrangement = Arrangement.Center
    ) {
        Text("Registro", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(20.dp))

        Text("Nombre")
        TextField(
            value = userInput,
            onValueChange = { newText -> userInput = newText },
            label = { Text("Introduce tu nombre") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(5.dp)
        )
        Text("Nick")
        TextField(
            value = userInput,
            onValueChange = { newText -> userInput = newText },
            label = { Text("Introduce tu nick") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(5.dp)
        )
        Text("Pass")
        TextField(
            value = userInput,
            onValueChange = { newText -> userInput = newText },
            label = { Text("Introduce tu pass") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(5.dp)
        )
        Spacer(modifier = Modifier.height(10.dp))
        Text("Fecha de nacimiento (toca el año para cambiarlo")
        Column(modifier = Modifier.align(Alignment.CenterHorizontally)) {
            AgeChoose()

            Spacer(modifier = Modifier.height(20.dp))
            Button(onClick = { }, modifier = Modifier.align(Alignment.CenterHorizontally)) {
                Text("Registrar usuario", style = MaterialTheme.typography.headlineMedium)
            }
        }
    }

}

@Composable
fun AgeChoose() {
    val context = LocalContext.current
    val calendar = Calendar.getInstance()
    val year = calendar.get(Calendar.YEAR)
    val month = calendar.get(Calendar.MONTH)
    val day = calendar.get(Calendar.DAY_OF_MONTH)

    val datePick = android.app.DatePickerDialog(
        context,
        { _: DatePicker, selectYear: Int, selectMonth: Int, selectDay: Int ->
            val selectDate = "$selectDay/${selectMonth + 1}/$selectYear"
            // Aquí puedes hacer algo con la fecha seleccionada
        },
        year,
        month,
        day
    )

    // Necesitas añadir un botón o algo para mostrar el diálogo
    Button(onClick = { datePick.show() }) {
        Text("Seleccionar fecha de nacimiento")
    }
}