package com.example.watchfinder.repository;

import com.example.watchfinder.api.ApiService;
import com.example.watchfinder.data.Utils;
import com.example.watchfinder.data.prefs.TokenManager;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

@ScopeMetadata("javax.inject.Singleton")
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
public final class MovieRepository_Factory implements Factory<MovieRepository> {
  private final Provider<ApiService> apiServiceProvider;

  private final Provider<TokenManager> tokenManagerProvider;

  private final Provider<Utils> utilsProvider;

  public MovieRepository_Factory(Provider<ApiService> apiServiceProvider,
      Provider<TokenManager> tokenManagerProvider, Provider<Utils> utilsProvider) {
    this.apiServiceProvider = apiServiceProvider;
    this.tokenManagerProvider = tokenManagerProvider;
    this.utilsProvider = utilsProvider;
  }

  @Override
  public MovieRepository get() {
    return newInstance(apiServiceProvider.get(), tokenManagerProvider.get(), utilsProvider.get());
  }

  public static MovieRepository_Factory create(Provider<ApiService> apiServiceProvider,
      Provider<TokenManager> tokenManagerProvider, Provider<Utils> utilsProvider) {
    return new MovieRepository_Factory(apiServiceProvider, tokenManagerProvider, utilsProvider);
  }

  public static MovieRepository newInstance(ApiService apiService, TokenManager tokenManager,
      Utils utils) {
    return new MovieRepository(apiService, tokenManager, utils);
  }
}
