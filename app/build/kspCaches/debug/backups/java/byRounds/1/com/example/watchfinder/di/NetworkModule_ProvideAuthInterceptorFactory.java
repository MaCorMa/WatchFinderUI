package com.example.watchfinder.di;

import com.example.watchfinder.api.AInterceptor;
import com.example.watchfinder.data.prefs.TokenManager;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
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
public final class NetworkModule_ProvideAuthInterceptorFactory implements Factory<AInterceptor> {
  private final Provider<TokenManager> tokenManagerProvider;

  public NetworkModule_ProvideAuthInterceptorFactory(Provider<TokenManager> tokenManagerProvider) {
    this.tokenManagerProvider = tokenManagerProvider;
  }

  @Override
  public AInterceptor get() {
    return provideAuthInterceptor(tokenManagerProvider.get());
  }

  public static NetworkModule_ProvideAuthInterceptorFactory create(
      Provider<TokenManager> tokenManagerProvider) {
    return new NetworkModule_ProvideAuthInterceptorFactory(tokenManagerProvider);
  }

  public static AInterceptor provideAuthInterceptor(TokenManager tokenManager) {
    return Preconditions.checkNotNullFromProvides(NetworkModule.INSTANCE.provideAuthInterceptor(tokenManager));
  }
}
