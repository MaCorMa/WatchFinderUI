package com.example.watchfinder.screens

import android.annotation.SuppressLint
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.ImageLoader
import coil.compose.AsyncImagePainter
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.example.watchfinder.R
import com.example.watchfinder.ui.theme.WatchFinderTheme
import com.example.watchfinder.viewmodels.ProfileVM


@Composable
fun ProfileImagePicker(
    modifier: Modifier = Modifier,
    currentFullImageUrl: String? = null, // url de la imagen existente
    onImageSelected: (Uri?) -> Unit,
    imageLoader: ImageLoader
) {
    //Para track de la imagen seleccionada
    var imageUri by remember { mutableStateOf<Uri?>(null) }
    val context = LocalContext.current

    // ActivityResultLauncher para seleccionar imagen desde el dispositivo
    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = { uri: Uri? ->
            imageUri = uri
            onImageSelected(uri)
        }
    )
    // determina que imagen cargar
    val imageToLoad = imageUri ?: currentFullImageUrl ?: R.drawable.ic_default_avatar

    // crea ImageRequest
    val imageRequest = ImageRequest.Builder(context)
        .data(imageToLoad)
        .placeholder(R.drawable.ic_default_avatar)
        .error(R.drawable.ic_error_placeholder)
        .build()

    // rememberAsyncImagePainter para cargar la imagen
    val painter = rememberAsyncImagePainter(
        model = imageRequest,
        imageLoader = imageLoader,
        contentScale = ContentScale.Crop // Crop para enmarcar
    )

    // log del painter para debug
    when (painter.state) {
        is AsyncImagePainter.State.Loading -> {
            Log.d("ProfileImagePicker", "Image loading: $imageToLoad")
        }
        is AsyncImagePainter.State.Success -> {
            Log.d("ProfileImagePicker", "Image loaded successfully: $imageToLoad")
        }
        is AsyncImagePainter.State.Error -> {
            val errorState = painter.state as AsyncImagePainter.State.Error
            Log.e("ProfileImagePicker", "Image loading failed: $imageToLoad", errorState.result.throwable)
        }
        is AsyncImagePainter.State.Empty -> {
            Log.d("ProfileImagePicker", "Image request model is empty.")
        }
    }

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painter,
            contentDescription = "Imagen de perfil",
            modifier = Modifier
                .size(120.dp)
                .clip(CircleShape) // la hace ciruclar
                .border(2.dp, MaterialTheme.colorScheme.primary, CircleShape)
                .clickable {
                    imagePickerLauncher.launch("image/*")
                }
        )

        Spacer(modifier = Modifier.height(16.dp))
    }

}


@SuppressLint("StateFlowValueCalledInComposition")
@Composable
fun Profile(
    onLogoutClick: () -> Unit,
) {
    val profileViewModel: ProfileVM = hiltViewModel()
    val uiState by profileViewModel.uiState.collectAsState()
    // Estado para el modo oscuro
    // por default en false -> modo claro
    val isDarkMode by profileViewModel.isDarkMode.collectAsState()
    var profileImageUri by remember { mutableStateOf<Uri?>(null) }
    //para los toast
    val context = LocalContext.current
    val isEditing by profileViewModel.isEditing.collectAsState()
    val isEditingPass by profileViewModel.isEditingPass.collectAsState()

    //cambio contraseña
    var currentPassword by remember { mutableStateOf("") }
    var newPassword by remember { mutableStateOf("") }
    var confirmNewPassword by remember { mutableStateOf("") }

    WatchFinderTheme(darkTheme = isDarkMode) { // Pasamos isDarkMode al tema
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                ProfileImagePicker(
                    modifier = Modifier.padding(bottom = 20.dp),
                    currentFullImageUrl = uiState.fullProfileImageUrl,
                    onImageSelected = { uri ->
                        profileImageUri = uri
                    },
                    imageLoader = profileViewModel.imageLoader
                )
                if (!isEditing) {
                    Text("Email: ${uiState.user?.email ?: "N/A"}")
                    Text("Username: ${uiState.user?.username ?: "N/A"}")
                    Button(
                        onClick = { profileViewModel.setEditing(true)},
                        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)
                    ) {
                        Text("Editar Usuario")
                    }
                }
                //muestra info User
                uiState.user?.let { user ->
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("Películas con me gusta: ${user.likedMovies?.size ?: 0}")
                    Text("Peliculas con no me gusta: ${user.dislikedMovies?.size ?: 0}")
                    Text("Series con me gusta: ${user.likedSeries?.size ?: 0}")
                    Text("Series con no me gusta: ${user.dislikedSeries?.size ?: 0}")
                    Text("Películas Vistas: ${user.seenMovies?.size ?: 0}")
                    Text("Series Vistas: ${user.seenSeries?.size ?: 0}")
                } ?: run {
                    Text("User data not available.")
                }

                Spacer(modifier = Modifier.height(10.dp))

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Modo Oscuro")
                    Spacer(modifier = Modifier.weight(1f))
                    Switch(
                        checked = isDarkMode,
                        onCheckedChange = { profileViewModel.setDarkMode(it) }
                    )
                }
                // Campos de edición (solo se muestran si isEditing es true)
                if (isEditing) {
                    OutlinedTextField(
                        value = uiState.newEmail,
                        onValueChange = { profileViewModel.onEmailInputChanged(it) },
                        label = { Text("Email") },
                        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)
                    )

                    OutlinedTextField(
                        value = uiState.newUserName,
                        onValueChange = { profileViewModel.onUsernameInputChanged(it) },
                        label = { Text("Username") },
                        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Spacer(modifier = Modifier.height(10.dp))
                    Spacer(modifier = Modifier.height(10.dp))

                    //botón cambio contraseña
                    if (!isEditingPass) {
                        Button(
                            modifier = Modifier.fillMaxWidth().padding(10.dp),
                            onClick = { profileViewModel.setPasswordEditing(true)}
                        ) {
                            Text("Cambiar Contraseña")
                        }
                    }else {
                        Column {
                            Text("Cambio Contraseña")
                            OutlinedTextField(
                                value = currentPassword,
                                onValueChange = { currentPassword = it },
                                label = { Text("Contraseña Actual") }
                            )
                            OutlinedTextField(
                                value = newPassword,
                                onValueChange = { newPassword = it },
                                label = { Text("Nueva Contraseña") }
                            )
                            OutlinedTextField(
                                value = confirmNewPassword,
                                onValueChange = { confirmNewPassword = it },
                                label = { Text("Confirmar Nueva Contraseña") }
                            )
                            Row {
                                Button(
                                    modifier = Modifier.weight(1f).padding(10.dp),
                                    onClick = {
                                            currentPassword = ""
                                            newPassword = ""
                                            confirmNewPassword = ""
                                            profileViewModel.setPasswordEditing(false)
                                            //Log.d("ProfileScreen", "isEditingPass: ${profileViewModel.isEditingPass.value}")
                                        }
                                ) {
                                    Text("Cancelar")
                                }
                                Button(
                                    modifier = Modifier.weight(1f).padding(10.dp),
                                    onClick = {
                                        if (newPassword == confirmNewPassword) {
                                            profileViewModel.changePassword(
                                                currentPassword,
                                                newPassword
                                            )
                                            profileViewModel.setPasswordEditing(false)
                                        } else {
                                            Toast.makeText(
                                                context,
                                                "Las contraseñas no coinciden",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        }
                                    },
                                ) {
                                    Text("Guardar")
                                }
                            }
                        }
                    }
                }
                // Botón "Guardar Cambios
                Button(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(10.dp), onClick = {
                        profileViewModel.saveProfileChanges()
                        profileViewModel.fetchProfileImage()
                        profileViewModel.saveDarkModePreference(isDarkMode)
                        profileViewModel.setEditing(false)
                        // feedback al usuario
                        if (uiState.isImageUpdateSuccess) {
                            Toast.makeText(
                                context,
                                "Imagen de perfil actualizada con éxito",
                                Toast.LENGTH_SHORT
                            ).show()
                        } else if (uiState.error != null) {
                            Toast.makeText(
                                context,
                                "Error al actualizar la imagen: ${uiState.error}",
                                Toast.LENGTH_SHORT
                            ).show()
                            println({ uiState.error })
                        }
                    }
                ) { Text("Guardar Cambios") }

                Spacer(modifier = Modifier.height(10.dp))

                Button(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(10.dp), onClick = onLogoutClick
                ) { Text("CerrarSesion") }

                Spacer(modifier = Modifier.height(20.dp))

                Button(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(10.dp),
                    onClick = {profileViewModel.toggleDeleteConfirmation()},
                    colors = ButtonDefaults.buttonColors(MaterialTheme.colorScheme.error)
                ) {
                    Text("Eliminar Cuenta", color = Color.White)
                }

                val showConfirmation by profileViewModel.showDeleteConfirmation.collectAsState()
                val confirmationText by profileViewModel.deleteConfirmationText.collectAsState()

                // Campo de confirmación y botón de borrado (condicional)
                if (showConfirmation) {
                    OutlinedTextField(
                        value = confirmationText,
                        onValueChange = { newText ->
                            profileViewModel.updateDeleteConfirmationText(newText)
                        },
                        label = { Text("Escribe 'DELETE' para confirmar") },
                        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                        readOnly = false
                    )
                    Button(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(10.dp),
                        onClick = { profileViewModel.deleteAccount()},
                        colors = ButtonDefaults.buttonColors(MaterialTheme.colorScheme.error)
                    ) {
                        Text("Confirmar Borrado", color = Color.White)
                    }
                }
            }
        }
    }
}
