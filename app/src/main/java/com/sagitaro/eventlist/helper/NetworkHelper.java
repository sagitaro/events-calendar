package com.sagitaro.eventlist.helper;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.sagitaro.eventlist.App;

public class NetworkHelper {

  /* Public Static Methods ************************************************************************/
  /**
   * Returns true is internet connection is available.
   */
  public static boolean isInternetAvailable() {
    ConnectivityManager manager = (ConnectivityManager) App.getContext().
      getSystemService(Context.CONNECTIVITY_SERVICE);
    NetworkInfo info = manager.getActiveNetworkInfo();
    return (info != null) && info.isConnectedOrConnecting();
  }
}
