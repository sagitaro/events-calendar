package com.sagitaro.eventlist;

import android.app.Application;
import android.content.Context;
import android.content.res.Resources;

import com.sagitaro.eventlist.core.component.AppComponent;
import com.sagitaro.eventlist.core.component.DaggerAppComponent;
import com.sagitaro.eventlist.core.component.module.AppModule;

import java.lang.ref.WeakReference;

import timber.log.Timber;
import timber.log.Timber.DebugTree;

/**
 * Created by Patrik on 30. 12. 2016.
 */

public class App extends Application {


  /* Private Static Attributes ********************************************************************/

  /**
   * Application resources.
   */
  private static Resources sResources;

  /**
   * Application component used for dependency injection.
   */
  private static AppComponent sAppComponent;

  /**
   * Application context.
   */
  private static WeakReference<Context> sContextReference;

  @Override
  public void onCreate() {
    super.onCreate();

    // Initialize.
    if (BuildConfig.DEBUG) {
      Timber.plant(new DebugTree());
    }

    // Initialize attributes.
    sAppComponent = DaggerAppComponent.builder().appModule(new AppModule(this)).build();
    sResources = getResources();

    sContextReference = new WeakReference<Context>(this);
  }


  /* Public Static Methods ************************************************************************/

  /**
   * Returns application context.
   */
  public static Context getContext() {
    return sContextReference.get();
  }

  /**
   * Returns application component.
   */
  public static AppComponent getAppComponent() {
    return sAppComponent;
  }

  /**
   * @see Resources#getString(int)
   */
  public static String getResString(int id) {
    return sResources.getString(id);
  }

  /**
   * @see Resources#getString(int, Object...)
   */
  public static String getResString(int resourceId, Object... formatArgs) {
    return sResources.getString(resourceId, formatArgs);
  }

  /**
   * @see Resources#getString(int)
   */
  public static int getResColor(int id) {
    return sResources.getColor(id);
  }
}
