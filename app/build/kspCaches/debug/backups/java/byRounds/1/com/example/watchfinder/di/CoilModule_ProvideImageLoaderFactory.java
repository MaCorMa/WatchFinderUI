package com.example.watchfinder.di;

import android.content.Context;
import coil3.ImageLoader;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;
import okhttp3.OkHttpClient;

@ScopeMetadata("javax.inject.Singleton")
@QualifierMetadata("dagger.hilt.android.qualifiers.ApplicationContext")
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
public final class CoilModule_ProvideImageLoaderFactory implements Factory<ImageLoader> {
  private final Provider<Context> contextProvider;

  private final Provider<OkHttpClient> okHttpClientProvider;

  public CoilModule_ProvideImageLoaderFactory(Provider<Context> contextProvider,
      Provider<OkHttpClient> okHttpClientProvider) {
    this.contextProvider = contextProvider;
    this.okHttpClientProvider = okHttpClientProvider;
  }

  @Override
  public ImageLoader get() {
    return provideImageLoader(contextProvider.get(), okHttpClientProvider.get());
  }

  public static CoilModule_ProvideImageLoaderFactory create(Provider<Context> contextProvider,
      Provider<OkHttpClient> okHttpClientProvider) {
    return new CoilModule_ProvideImageLoaderFactory(contextProvider, okHttpClientProvider);
  }

  public static ImageLoader provideImageLoader(Context context, OkHttpClient okHttpClient) {
    return Preconditions.checkNotNullFromProvides(CoilModule.INSTANCE.provideImageLoader(context, okHttpClient));
  }
}
