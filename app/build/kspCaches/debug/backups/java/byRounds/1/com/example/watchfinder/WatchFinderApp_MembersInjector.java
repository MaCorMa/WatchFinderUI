package com.example.watchfinder;

import coil3.ImageLoader;
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
public final class WatchFinderApp_MembersInjector implements MembersInjector<WatchFinderApp> {
  private final Provider<ImageLoader> imageLoaderProvider;

  public WatchFinderApp_MembersInjector(Provider<ImageLoader> imageLoaderProvider) {
    this.imageLoaderProvider = imageLoaderProvider;
  }

  public static MembersInjector<WatchFinderApp> create(Provider<ImageLoader> imageLoaderProvider) {
    return new WatchFinderApp_MembersInjector(imageLoaderProvider);
  }

  @Override
  public void injectMembers(WatchFinderApp instance) {
    injectImageLoaderProvider(instance, imageLoaderProvider);
  }

  @InjectedFieldSignature("com.example.watchfinder.WatchFinderApp.imageLoaderProvider")
  public static void injectImageLoaderProvider(WatchFinderApp instance,
      Provider<ImageLoader> imageLoaderProvider) {
    instance.imageLoaderProvider = imageLoaderProvider;
  }
}
