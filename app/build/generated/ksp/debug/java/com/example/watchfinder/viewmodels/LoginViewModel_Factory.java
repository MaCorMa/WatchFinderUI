package com.example.watchfinder.viewmodels;

import com.example.watchfinder.data.UserManager;
import com.example.watchfinder.data.prefs.TokenManager;
import com.example.watchfinder.repository.AuthRepository;
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
public final class LoginViewModel_Factory implements Factory<LoginViewModel> {
  private final Provider<AuthRepository> authRepositoryProvider;

  private final Provider<TokenManager> tokenManagerProvider;

  private final Provider<UserManager> userManagerProvider;

  public LoginViewModel_Factory(Provider<AuthRepository> authRepositoryProvider,
      Provider<TokenManager> tokenManagerProvider, Provider<UserManager> userManagerProvider) {
    this.authRepositoryProvider = authRepositoryProvider;
    this.tokenManagerProvider = tokenManagerProvider;
    this.userManagerProvider = userManagerProvider;
  }

  @Override
  public LoginViewModel get() {
    return newInstance(authRepositoryProvider.get(), tokenManagerProvider.get(), userManagerProvider.get());
  }

  public static LoginViewModel_Factory create(Provider<AuthRepository> authRepositoryProvider,
      Provider<TokenManager> tokenManagerProvider, Provider<UserManager> userManagerProvider) {
    return new LoginViewModel_Factory(authRepositoryProvider, tokenManagerProvider, userManagerProvider);
  }

  public static LoginViewModel newInstance(AuthRepository authRepository, TokenManager tokenManager,
      UserManager userManager) {
    return new LoginViewModel(authRepository, tokenManager, userManager);
  }
}
