package com.example.watchfinder.data;

import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;

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
public final class Utils_Factory implements Factory<Utils> {
  @Override
  public Utils get() {
    return newInstance();
  }

  public static Utils_Factory create() {
    return InstanceHolder.INSTANCE;
  }

  public static Utils newInstance() {
    return new Utils();
  }

  private static final class InstanceHolder {
    private static final Utils_Factory INSTANCE = new Utils_Factory();
  }
}
