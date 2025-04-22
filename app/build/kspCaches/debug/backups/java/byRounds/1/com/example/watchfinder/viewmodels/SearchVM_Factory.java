package com.example.watchfinder.viewmodels;

import com.example.watchfinder.repository.GenreRepository;
import com.example.watchfinder.repository.MovieRepository;
import com.example.watchfinder.repository.SeriesRepository;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

@ScopeMetadata
@QualifierMetadata
@DaggerGenerated
@Generated(
    value = "dagger.internal.codegen.ComponentProcessor",
    comments = "https://dagger.dev"
)
@SuppressWarnings({
    "unchecked",
    "rawtypes",
    "KotlinInternal",
    "KotlinInternalInJava",
    "cast"
})
public final class SearchVM_Factory implements Factory<SearchVM> {
  private final Provider<GenreRepository> genreRepositoryProvider;

  private final Provider<MovieRepository> movieRepositoryProvider;

  private final Provider<SeriesRepository> seriesRepositoryProvider;

  public SearchVM_Factory(Provider<GenreRepository> genreRepositoryProvider,
      Provider<MovieRepository> movieRepositoryProvider,
      Provider<SeriesRepository> seriesRepositoryProvider) {
    this.genreRepositoryProvider = genreRepositoryProvider;
    this.movieRepositoryProvider = movieRepositoryProvider;
    this.seriesRepositoryProvider = seriesRepositoryProvider;
  }

  @Override
  public SearchVM get() {
    return newInstance(genreRepositoryProvider.get(), movieRepositoryProvider.get(), seriesRepositoryProvider.get());
  }

  public static SearchVM_Factory create(Provider<GenreRepository> genreRepositoryProvider,
      Provider<MovieRepository> movieRepositoryProvider,
      Provider<SeriesRepository> seriesRepositoryProvider) {
    return new SearchVM_Factory(genreRepositoryProvider, movieRepositoryProvider, seriesRepositoryProvider);
  }

  public static SearchVM newInstance(GenreRepository genreRepository,
      MovieRepository movieRepository, SeriesRepository seriesRepository) {
    return new SearchVM(genreRepository, movieRepository, seriesRepository);
  }
}
