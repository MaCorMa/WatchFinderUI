package com.example.watchfinder.viewmodels;

import com.example.watchfinder.data.UserManager;
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

  private final Provider<UserManager> userManagerProvider;

  public DiscoverSeriesVM_Factory(Provider<UserRepository> userRepositoryProvider,
      Provider<SeriesRepository> seriesRepositoryProvider,
      Provider<UserManager> userManagerProvider) {
    this.userRepositoryProvider = userRepositoryProvider;
    this.seriesRepositoryProvider = seriesRepositoryProvider;
    this.userManagerProvider = userManagerProvider;
  }

  @Override
  public DiscoverSeriesVM get() {
    return newInstance(userRepositoryProvider.get(), seriesRepositoryProvider.get(), userManagerProvider.get());
  }

  public static DiscoverSeriesVM_Factory create(Provider<UserRepository> userRepositoryProvider,
      Provider<SeriesRepository> seriesRepositoryProvider,
      Provider<UserManager> userManagerProvider) {
    return new DiscoverSeriesVM_Factory(userRepositoryProvider, seriesRepositoryProvider, userManagerProvider);
  }

  public static DiscoverSeriesVM newInstance(UserRepository userRepository,
      SeriesRepository seriesRepository, UserManager userManager) {
    return new DiscoverSeriesVM(userRepository, seriesRepository, userManager);
  }
}
