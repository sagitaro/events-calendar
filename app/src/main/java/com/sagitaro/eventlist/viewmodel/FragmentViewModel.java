package com.sagitaro.eventlist.viewmodel;

import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;

import eu.inloop.viewmodel.AbstractViewModel;
import eu.inloop.viewmodel.IView;

/**
 * Base class for view models of {@link ViewModelFragment}
 */
public abstract class FragmentViewModel<TView extends IView> extends AbstractViewModel<TView> {

  /* Protected Attributes *************************************************************************/

  /**
   * Link to view (Fragment).
   */
  protected Fragment mFragment;

  /* Public Methods *******************************************************************************/

  /**
   * @see AbstractViewModel#onBindView(IView)
   */
  @Override
  public void onBindView(@NonNull TView view) {
    super.onBindView(view);

    // Remember the link to fragment.
    mFragment = (Fragment) view;
  }
}
