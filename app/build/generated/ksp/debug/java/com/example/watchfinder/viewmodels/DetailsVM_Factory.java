package com.example.watchfinder.viewmodels;

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
public final class DetailsVM_Factory implements Factory<DetailsVM> {
  private final Provider<MovieRepository> movieRepositoryProvider;

  private final Provider<SeriesRepository> seriesRepositoryProvider;

  public DetailsVM_Factory(Provider<MovieRepository> movieRepositoryProvider,
      Provider<SeriesRepository> seriesRepositoryProvider) {
    this.movieRepositoryProvider = movieRepositoryProvider;
    this.seriesRepositoryProvider = seriesRepositoryProvider;
  }

  @Override
  public DetailsVM get() {
    return newInstance(movieRepositoryProvider.get(), seriesRepositoryProvider.get());
  }

  public static DetailsVM_Factory create(Provider<MovieRepository> movieRepositoryProvider,
      Provider<SeriesRepository> seriesRepositoryProvider) {
    return new DetailsVM_Factory(movieRepositoryProvider, seriesRepositoryProvider);
  }

  public static DetailsVM newInstance(MovieRepository movieRepository,
      SeriesRepository seriesRepository) {
    return new DetailsVM(movieRepository, seriesRepository);
  }
}
