package com.example.watchfinder.repository;

import com.example.watchfinder.api.ApiService;
import com.example.watchfinder.data.UserManager;
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
public final class GenreRepository_Factory implements Factory<GenreRepository> {
  private final Provider<ApiService> apiServiceProvider;

  private final Provider<TokenManager> tokenManagerProvider;

  private final Provider<UserManager> userManagerProvider;

  public GenreRepository_Factory(Provider<ApiService> apiServiceProvider,
      Provider<TokenManager> tokenManagerProvider, Provider<UserManager> userManagerProvider) {
    this.apiServiceProvider = apiServiceProvider;
    this.tokenManagerProvider = tokenManagerProvider;
    this.userManagerProvider = userManagerProvider;
  }

  @Override
  public GenreRepository get() {
    return newInstance(apiServiceProvider.get(), tokenManagerProvider.get(), userManagerProvider.get());
  }

  public static GenreRepository_Factory create(Provider<ApiService> apiServiceProvider,
      Provider<TokenManager> tokenManagerProvider, Provider<UserManager> userManagerProvider) {
    return new GenreRepository_Factory(apiServiceProvider, tokenManagerProvider, userManagerProvider);
  }

  public static GenreRepository newInstance(ApiService apiService, TokenManager tokenManager,
      UserManager userManager) {
    return new GenreRepository(apiService, tokenManager, userManager);
  }
}
