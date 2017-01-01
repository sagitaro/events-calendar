package com.sagitaro.eventlist.core.component;

import android.app.Activity;
import android.app.Application;
import android.content.Context;

import com.sagitaro.eventlist.core.component.module.AppModule;
import com.sagitaro.eventlist.fragment.BaseFragment;
import com.sagitaro.eventlist.helper.PreferencesHelper;

import javax.inject.Singleton;

import dagger.Component;

/**
 * Declares app component interface.
 */
@Singleton
@Component(modules = { AppModule.class })
public interface AppComponent {

  void inject(Activity baseActivity);

  void inject(BaseFragment baseFragment);

  ///////////////////////////////////

  Context provideContext();

  Application provideApplication();

  PreferencesHelper providePreferencesHelper();
}

