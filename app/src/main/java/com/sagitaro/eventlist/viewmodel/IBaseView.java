package com.sagitaro.eventlist.viewmodel;

import android.view.View;

import android.support.annotation.Nullable;

import eu.inloop.viewmodel.IView;
import eu.inloop.viewmodel.binding.ViewModelBindingConfig;

/**
 * Base interface for all views.
 */
public interface IBaseView extends IView {

  void showKeyboard(View view);

  void hideKeyboard();

  void clearFocus();

  void finish();
}
