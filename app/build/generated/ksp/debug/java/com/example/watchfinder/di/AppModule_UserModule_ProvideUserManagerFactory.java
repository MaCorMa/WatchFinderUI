package com.example.watchfinder.di;

import com.example.watchfinder.data.UserManager;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;

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
public final class AppModule_UserModule_ProvideUserManagerFactory implements Factory<UserManager> {
  @Override
  public UserManager get() {
    return provideUserManager();
  }

  public static AppModule_UserModule_ProvideUserManagerFactory create() {
    return InstanceHolder.INSTANCE;
  }

  public static UserManager provideUserManager() {
    return Preconditions.checkNotNullFromProvides(AppModule.UserModule.INSTANCE.provideUserManager());
  }

  private static final class InstanceHolder {
    private static final AppModule_UserModule_ProvideUserManagerFactory INSTANCE = new AppModule_UserModule_ProvideUserManagerFactory();
  }
}
