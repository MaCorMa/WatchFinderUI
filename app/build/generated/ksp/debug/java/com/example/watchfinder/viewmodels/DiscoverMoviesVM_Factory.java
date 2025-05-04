package com.example.watchfinder.viewmodels;

import com.example.watchfinder.repository.MovieRepository;
import com.example.watchfinder.repository.UserRepository;
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
public final class DiscoverMoviesVM_Factory implements Factory<DiscoverMoviesVM> {
  private final Provider<UserRepository> userRepositoryProvider;

  private final Provider<MovieRepository> movieRepositoryProvider;

  public DiscoverMoviesVM_Factory(Provider<UserRepository> userRepositoryProvider,
      Provider<MovieRepository> movieRepositoryProvider) {
    this.userRepositoryProvider = userRepositoryProvider;
    this.movieRepositoryProvider = movieRepositoryProvider;
  }

  @Override
  public DiscoverMoviesVM get() {
    return newInstance(userRepositoryProvider.get(), movieRepositoryProvider.get());
  }

  public static DiscoverMoviesVM_Factory create(Provider<UserRepository> userRepositoryProvider,
      Provider<MovieRepository> movieRepositoryProvider) {
    return new DiscoverMoviesVM_Factory(userRepositoryProvider, movieRepositoryProvider);
  }

  public static DiscoverMoviesVM newInstance(UserRepository userRepository,
      MovieRepository movieRepository) {
    return new DiscoverMoviesVM(userRepository, movieRepository);
  }
}
