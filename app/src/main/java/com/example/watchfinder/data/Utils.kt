package com.example.watchfinder.data;

import com.example.watchfinder.data.dto.MovieCard;
import com.example.watchfinder.data.dto.SeriesCard
import com.example.watchfinder.data.model.Movie;
import com.example.watchfinder.data.model.Series
import javax.inject.Inject

public class Utils @Inject constructor() {

    fun movieToCard(movie: Movie): MovieCard {
        return MovieCard(
            _id = movie._id,
            Title = movie.title,
            Plot = movie.plot ?: "Sin descripción",
            Url = movie.url ?: "",
            Genres = movie.genres,
            Runtime = movie.runtime,
            Director = movie.director,
            Cast = movie.cast,
            Ratings = movie.ratings,
            Languages = movie.languages,
            Country = movie.country,
            Awards = movie.awards,
            Year = movie.year,
            ReleaseDate = movie.releaseDate,
            Rated = movie.rated
        )
    }
    fun cardToMovie(movieCard: MovieCard): Movie {
        return Movie(
            _id = movieCard._id,
            title = movieCard.Title,
            plot = movieCard.Plot ?: "Sin descripción",
            url = movieCard.Url ?: "",
            genres = movieCard.Genres,
            runtime = movieCard.Runtime,
            director = movieCard.Director,
            cast = movieCard.Cast,
            ratings = movieCard.Ratings,
            languages = movieCard.Languages,
            country = movieCard.Country,
            awards = movieCard.Awards,
            year = movieCard.Year,
            rated = movieCard.Rated,
            releaseDate = movieCard.ReleaseDate
        )
    }

    fun seriesToCard(series: Series): SeriesCard {
        return SeriesCard(
            _id = series._id,
            Title = series.title,
            Plot = series.plot ?: "Sin descripción",
            Url = series.url ?: "",
            Genres = series.genres,
            Runtime = series.runtime,
            Director = series.director,
            Cast = series.cast,
            Ratings = series.ratings,
            Languages = series.languages,
            Country = series.country,
            Awards = series.awards,
            Year = series.year ?: "",
            ReleaseDate = series.releaseDate,
            Rated = series.rated ?: "",
            EndDate = series.endDate,
            Status = series.status,
            Seasons = series.seasons
        )
    }
}
