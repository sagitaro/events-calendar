package com.sagitaro.eventlist;

import android.app.Application;
import android.util.Log;

import timber.log.Timber;
import timber.log.Timber.DebugTree;

/**
 * Created by Patrik on 30. 12. 2016.
 */

public class App extends Application {
  @Override
  public void onCreate() {
    super.onCreate();

    // Initialize.
    if (BuildConfig.DEBUG) {
      Timber.plant(new DebugTree());
    }
  }

}
