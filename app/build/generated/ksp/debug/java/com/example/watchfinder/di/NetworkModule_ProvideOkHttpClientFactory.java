package com.example.watchfinder.di;

import com.example.watchfinder.api.AInterceptor;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;
import okhttp3.OkHttpClient;

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
public final class NetworkModule_ProvideOkHttpClientFactory implements Factory<OkHttpClient> {
  private final Provider<AInterceptor> aInterceptorProvider;

  public NetworkModule_ProvideOkHttpClientFactory(Provider<AInterceptor> aInterceptorProvider) {
    this.aInterceptorProvider = aInterceptorProvider;
  }

  @Override
  public OkHttpClient get() {
    return provideOkHttpClient(aInterceptorProvider.get());
  }

  public static NetworkModule_ProvideOkHttpClientFactory create(
      Provider<AInterceptor> aInterceptorProvider) {
    return new NetworkModule_ProvideOkHttpClientFactory(aInterceptorProvider);
  }

  public static OkHttpClient provideOkHttpClient(AInterceptor aInterceptor) {
    return Preconditions.checkNotNullFromProvides(NetworkModule.INSTANCE.provideOkHttpClient(aInterceptor));
  }
}
