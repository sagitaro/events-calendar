package com.sagitaro.eventlist.core.component.module;

import android.app.Application;
import android.content.Context;

import com.sagitaro.eventlist.helper.PreferencesHelper;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * Module that provides application wide components.
 */
@Module
public class AppModule {

  /* Private Attributes ***************************************************************************/

  /**
   * Application instance.
   */
  private Application mApplication;

  /* Public Methods *******************************************************************************/

  /**
   * Constructor.
   *
   * @param application Application instance.
   */
  public AppModule(Application application) {
    mApplication = application;
  }

  //////////////////////////////////////////////////////////////////////////////////////////////////

  @Provides
  @Singleton
  public Context provideContext() {
    return mApplication.getApplicationContext();
  }

  @Provides
  @Singleton
  public Application provideApplication() {
    return mApplication;
  }

  //////////////////////////////////////////////////////////////////////////////////////////////////

  @Provides
  @Singleton
  public PreferencesHelper providePreferencesHelper(Context context) {
    return new PreferencesHelper(context);
  }
}
