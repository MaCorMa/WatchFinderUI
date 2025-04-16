package com.example.watchfinder;

import com.example.watchfinder.data.UserManager;
import com.example.watchfinder.data.prefs.TokenManager;
import com.example.watchfinder.repository.AuthRepository;
import dagger.MembersInjector;
import dagger.internal.DaggerGenerated;
import dagger.internal.InjectedFieldSignature;
import dagger.internal.QualifierMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

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
public final class MainActivity_MembersInjector implements MembersInjector<MainActivity> {
  private final Provider<TokenManager> tokenManagerProvider;

  private final Provider<UserManager> userManagerProvider;

  private final Provider<AuthRepository> authRepositoryProvider;

  public MainActivity_MembersInjector(Provider<TokenManager> tokenManagerProvider,
      Provider<UserManager> userManagerProvider, Provider<AuthRepository> authRepositoryProvider) {
    this.tokenManagerProvider = tokenManagerProvider;
    this.userManagerProvider = userManagerProvider;
    this.authRepositoryProvider = authRepositoryProvider;
  }

  public static MembersInjector<MainActivity> create(Provider<TokenManager> tokenManagerProvider,
      Provider<UserManager> userManagerProvider, Provider<AuthRepository> authRepositoryProvider) {
    return new MainActivity_MembersInjector(tokenManagerProvider, userManagerProvider, authRepositoryProvider);
  }

  @Override
  public void injectMembers(MainActivity instance) {
    injectTokenManager(instance, tokenManagerProvider.get());
    injectUserManager(instance, userManagerProvider.get());
    injectAuthRepository(instance, authRepositoryProvider.get());
  }

  @InjectedFieldSignature("com.example.watchfinder.MainActivity.tokenManager")
  public static void injectTokenManager(MainActivity instance, TokenManager tokenManager) {
    instance.tokenManager = tokenManager;
  }

  @InjectedFieldSignature("com.example.watchfinder.MainActivity.userManager")
  public static void injectUserManager(MainActivity instance, UserManager userManager) {
    instance.userManager = userManager;
  }

  @InjectedFieldSignature("com.example.watchfinder.MainActivity.authRepository")
  public static void injectAuthRepository(MainActivity instance, AuthRepository authRepository) {
    instance.authRepository = authRepository;
  }
}
