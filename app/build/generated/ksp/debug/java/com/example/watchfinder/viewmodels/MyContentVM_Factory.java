package com.example.watchfinder.viewmodels;

import com.example.watchfinder.repository.UserRepository;
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
public final class MyContentVM_Factory implements Factory<MyContentVM> {
  private final Provider<UserRepository> userRepositoryProvider;

  public MyContentVM_Factory(Provider<UserRepository> userRepositoryProvider) {
    this.userRepositoryProvider = userRepositoryProvider;
  }

  @Override
  public MyContentVM get() {
    return newInstance(userRepositoryProvider.get());
  }

  public static MyContentVM_Factory create(Provider<UserRepository> userRepositoryProvider) {
    return new MyContentVM_Factory(userRepositoryProvider);
  }

  public static MyContentVM newInstance(UserRepository userRepository) {
    return new MyContentVM(userRepository);
  }
}
