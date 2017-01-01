package com.sagitaro.eventlist.fragment;

import android.app.Application;
import android.content.Context;
import android.os.Bundle;

import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v4.app.Fragment;

import com.sagitaro.eventlist.App;
import com.sagitaro.eventlist.helper.PreferencesHelper;

import java.lang.reflect.Field;

import javax.inject.Inject;

/**
 * Parent class for all fragments.
 */
public abstract class BaseFragment extends Fragment {

  /* Protected Attributes *************************************************************************/

  /**
   * {@link Context} singleton instance.
   */
  @Inject
  protected Context mContext;

  /**
   * {@link Application} singleton instance.
   */
  @Inject
  protected Application mApplication;

  /**
   * {@link PreferencesHelper} singleton instance.
   */
  @Inject
  protected PreferencesHelper mPreferencesHelper;

  /* Public Methods *******************************************************************************/

  /**
   * Page category to be used with Forter tracking.
   */
  @StringRes
  public abstract int getPageCategory();

  /**
   * Page title to be used with Forter tracking.
   */
  public String getPageTitle() {
    return getClass().getName();
  }

  /**
   * @see Fragment#onCreate(Bundle)
   */
  @Override
  public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    // Inject singleton instances.
    App.getAppComponent().inject(this);
  }

  /**
   * @see Fragment#onDetach()
   */
  @Override
  public void onDetach() {
    super.onDetach();

    // Set the child fragment manager to null.
    // Needs to be done manually on Android 4 devices.
    try {
      Field childFragmentManager = Fragment.class.getDeclaredField("mChildFragmentManager");
      childFragmentManager.setAccessible(true);
      childFragmentManager.set(this, null);
    } catch (NoSuchFieldException e) {
      throw new RuntimeException(e);
    } catch (IllegalAccessException e) {
      throw new RuntimeException(e);
    }
  }
}
