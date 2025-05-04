import androidx.compose.runtime.Composable
import com.example.watchfinder.R


@Composable
fun providerToLogo(providerName: String): Int? { // Devuelve el ID del drawable o null
    // IMPORTANTE: Usa los nombres EXACTOS que te devuelve tu API/backend
    // y los nombres EXACTOS de tus archivos PNG en drawable.
    return when (providerName.lowercase()) { // Compara en minúsculas para flexibilidad
        "netflix" -> R.drawable.netflix_logo // Asume que tienes netflix_logo.png
        "hbo max", "max" -> R.drawable.hbo_max_logo // Asume que tienes hbo_max_logo.png (maneja variaciones)
        "disney+" -> R.drawable.disney_plus_logo // Asume que tienes disney_plus_logo.png
        "amazon prime video", "prime video" -> R.drawable.prime_video_logo // Asume que tienes prime_video_logo.png
        "movistar plus+" -> R.drawable.movistar_plus_logo // Asume que tienes movistar_plus_logo.png
        // Añade más proveedores según necesites...
        "hulu" -> R.drawable.hulu_logo
        "apple tv+" -> R.drawable.apple_tv_plus_logo
        // Si un proveedor no tiene logo o no lo reconoces:
        else -> null // O devuelve un R.drawable.placeholder_logo si tienes uno genérico
    }
}