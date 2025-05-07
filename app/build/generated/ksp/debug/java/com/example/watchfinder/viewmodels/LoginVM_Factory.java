package com.example.watchfinder.viewmodels;

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
public final class LoginVM_Factory implements Factory<LoginVM> {
  private final Provider<AuthRepository> authRepositoryProvider;

  public LoginViewModel_Factory(Provider<AuthRepository> authRepositoryProvider) {
  private final Provider<TokenManager> tokenManagerProvider;

  private final Provider<UserManager> userManagerProvider;

  public LoginVM_Factory(Provider<AuthRepository> authRepositoryProvider,
      Provider<TokenManager> tokenManagerProvider, Provider<UserManager> userManagerProvider) {
    this.authRepositoryProvider = authRepositoryProvider;
  }

  @Override
  public LoginViewModel get() {
    return newInstance(authRepositoryProvider.get());
  }

  public static LoginViewModel_Factory create(Provider<AuthRepository> authRepositoryProvider) {
    return new LoginViewModel_Factory(authRepositoryProvider);
  }

  public static LoginViewModel newInstance(AuthRepository authRepository) {
    return new LoginViewModel(authRepository);
  public LoginVM get() {
    return newInstance(authRepositoryProvider.get(), tokenManagerProvider.get(), userManagerProvider.get());
  }

  public static LoginVM_Factory create(Provider<AuthRepository> authRepositoryProvider,
      Provider<TokenManager> tokenManagerProvider, Provider<UserManager> userManagerProvider) {
    return new LoginVM_Factory(authRepositoryProvider, tokenManagerProvider, userManagerProvider);
  }

  public static LoginVM newInstance(AuthRepository authRepository, TokenManager tokenManager,
      UserManager userManager) {
    return new LoginVM(authRepository, tokenManager, userManager);
  }
}
