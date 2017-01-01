package com.sagitaro.eventlist.fragment;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;

import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;

import com.sagitaro.eventlist.viewmodel.IBaseView;

import eu.inloop.viewmodel.AbstractViewModel;
import eu.inloop.viewmodel.IView;
import eu.inloop.viewmodel.ViewModelHelper;

/**
 * Base class for fragments using view-model.
 *
 * @param <TView> View interface type.
 * @param <TViewModel> View model type.
 */
public abstract class ViewModelFragment<TView extends IView, TViewModel extends
  AbstractViewModel<TView>> extends BaseFragment implements IBaseView {

  /* Private Attributes ***************************************************************************/

  /**
   * View-model helper.
   */
  private ViewModelHelper<TView, TViewModel> mViewModelHelper = new ViewModelHelper<>();

  /* Public Abstract Methods **********************************************************************/

  /**
   * Returns class of view-model coupled with this fragment.
   */
  public abstract Class<TViewModel> getViewModelClass();

  /* Public Methods *******************************************************************************/

  /**
   * Returns the fragment view model.
   *
   * @return Fragment view model.
   */
  public TViewModel getViewModel() {
    return mViewModelHelper.getViewModel();
  }

  /**
   * Call this after your view is ready - usually on the end of
   * {@link Fragment#onCreateView(LayoutInflater, ViewGroup, Bundle)}.
   *
   * @param view The view-model view.
   */
  public void setModelView(@NonNull TView view) {
    mViewModelHelper.setView(view);
  }

  //////////////////////////////////////////////////////////////////////////////////////////////////

  /**
   * @see Fragment#onCreate(Bundle)
   */
  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    mViewModelHelper.onCreate(getActivity(), savedInstanceState,
      getViewModelClass(), getArguments());
  }

  /**
   * @see Fragment#onViewCreated(View, Bundle)
   */
  @Override
  public void onViewCreated(View view, Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    setModelView((TView) this);
  }

  /**
   * @see Fragment#onStart()
   */
  @Override
  public void onStart() {
    super.onStart();
    mViewModelHelper.onStart();
  }

  /**
   * @see Fragment#onStop()
   */
  @Override
  public void onStop() {
    super.onStop();
    mViewModelHelper.onStop();
  }

  /**
   * @see Fragment#onDestroyView()
   */
  @Override
  public void onDestroyView() {
    mViewModelHelper.onDestroyView(this);
    super.onDestroyView();
  }

  /**
   * @see Fragment#onDestroy()
   */
  @Override
  public void onDestroy() {
    mViewModelHelper.onDestroy(this);
    super.onDestroy();
  }

  /**
   * @see Fragment#onSaveInstanceState(Bundle)
   */
  @Override
  public void onSaveInstanceState(Bundle outState) {
    super.onSaveInstanceState(outState);
    mViewModelHelper.onSaveInstanceState(outState);
  }

  //////////////////////////////////////////////////////////////////////////////////////////////////

  /**
   * @see IBaseView#showKeyboard(View)
   */
  public void showKeyboard(View view) {
    FragmentActivity activity = getActivity();
    if (activity != null) {
      InputMethodManager manager = (InputMethodManager) activity.
        getSystemService(Context.INPUT_METHOD_SERVICE);
      manager.showSoftInput(view, InputMethodManager.SHOW_FORCED);
    }
  }

  /**
   * @see IBaseView#hideKeyboard()
   */
  public void hideKeyboard() {
    FragmentActivity activity = getActivity();
    if (activity != null) {
      InputMethodManager manager = (InputMethodManager) activity.
        getSystemService(Context.INPUT_METHOD_SERVICE);
      View currentFocus = activity.getCurrentFocus();
      if (currentFocus != null) {
        manager.hideSoftInputFromWindow(currentFocus.getWindowToken(), 0);
      }
    }
  }

  /**
   * @see IBaseView#clearFocus()
   */
  public void clearFocus() {
    FragmentActivity activity = getActivity();
    if (activity != null) {
      View currentFocus = activity.getCurrentFocus();
      if (currentFocus != null) {
        currentFocus.clearFocus();
      }
    }
  }

  /**
   * @see IBaseView#finish()
   */
  public void finish() {
    FragmentActivity activity = getActivity();
    if (activity != null) {
      activity.getSupportFragmentManager()
        .beginTransaction()
        .remove(this)
        .commit();
    }
  }
}
