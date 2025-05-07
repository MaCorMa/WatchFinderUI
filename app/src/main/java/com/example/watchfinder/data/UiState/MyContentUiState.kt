import com.example.watchfinder.data.dto.MovieCard
import com.example.watchfinder.data.dto.SeriesCard
import com.example.watchfinder.data.model.Movie
import com.example.watchfinder.data.model.Series

data class MyContentUiState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val currentView: MyContentView = MyContentView.BUTTONS, // Empieza mostrando los botones
    val favoriteMovies: List<MovieCard?> = emptyList(),
    val favoriteSeries: List<SeriesCard?> = emptyList()
)