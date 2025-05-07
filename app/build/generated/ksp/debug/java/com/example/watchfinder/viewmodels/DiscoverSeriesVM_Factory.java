package com.example.watchfinder.viewmodels;

import com.example.watchfinder.repository.SeriesRepository;
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
public final class DiscoverSeriesVM_Factory implements Factory<DiscoverSeriesVM> {
  private final Provider<UserRepository> userRepositoryProvider;

  private final Provider<SeriesRepository> seriesRepositoryProvider;

  public DiscoverSeriesVM_Factory(Provider<UserRepository> userRepositoryProvider,
      Provider<SeriesRepository> seriesRepositoryProvider) {
    this.userRepositoryProvider = userRepositoryProvider;
    this.seriesRepositoryProvider = seriesRepositoryProvider;
  }

  @Override
  public DiscoverSeriesVM get() {
    return newInstance(userRepositoryProvider.get(), seriesRepositoryProvider.get());
  }

  public static DiscoverSeriesVM_Factory create(Provider<UserRepository> userRepositoryProvider,
      Provider<SeriesRepository> seriesRepositoryProvider) {
    return new DiscoverSeriesVM_Factory(userRepositoryProvider, seriesRepositoryProvider);
  }

  public static DiscoverSeriesVM newInstance(UserRepository userRepository,
      SeriesRepository seriesRepository) {
    return new DiscoverSeriesVM(userRepository, seriesRepository);
  }
}
