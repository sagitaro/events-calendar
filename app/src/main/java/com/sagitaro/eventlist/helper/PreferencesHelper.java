package com.sagitaro.eventlist.helper;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.preference.PreferenceManager;

import com.google.gson.Gson;
import com.google.gson.JsonParseException;
import com.sagitaro.eventlist.R;
import com.sagitaro.eventlist.model.Event;
import com.sagitaro.eventlist.model.Location;

/**
 * Simple accessor to preferences items.
 */
public class PreferencesHelper {

  /* Private Attributes ***************************************************************************/

  /**
   * Shared preferences for current context.
   */
  private SharedPreferences mPreferences;

  /**
   * Resources for current context.
   */
  private Resources mResources;

  /* Public Methods *******************************************************************************/

  /**
   * Constructor
   *
   * @param context Application context.
   */
  public PreferencesHelper(Context context) {
    // Initialize attributes.
    mPreferences = PreferenceManager.getDefaultSharedPreferences(context);
    mResources = context.getResources();
  }

  //////////////////////////////////////////////////////////////////////////////////////////////////

  /**
   * Determine if application runs first time.
   */
  public boolean getFirstRun() {
    return mPreferences.getBoolean(mResources.getString(R.string.pref_key_first_run), true);
  }

  /**
   * Sets when application has run first time.
   */
  public void setFirstRun(boolean firstRun) {
    SharedPreferences.Editor editor = mPreferences.edit();
    editor.putBoolean(mResources.getString(R.string.pref_key_first_run), firstRun);
    editor.apply();
  }

  //////////////////////////////////////////////////////////////////////////////////////////////////

  /**
   * Returns saved events, or null if not set yet.
   */
  public Event[] getEvents() {
    String json = mPreferences.getString(mResources.getString(
      R.string.pref_key_events), null);
    try {
      return (json != null) ? new Gson().fromJson(json, Event[].class) : null;
    } catch (JsonParseException e) {
      return null;
    }
  }

  /**
   * Saves events to shared preferences.
   */
  public void setEvents(Event[] events) {
    SharedPreferences.Editor editor = mPreferences.edit();
    String json = (events != null) ? new Gson().toJson(events) : null;
    editor.putString(mResources.getString(R.string.pref_key_events), json);
    editor.apply();
  }
  //////////////////////////////////////////////////////////////////////////////////////////////////

  /**
   * Returns saved events, or null if not set yet.
   */
  public Location[] getLocations() {
    String json = mPreferences.getString(mResources.getString(
      R.string.pref_key_locations), null);
    try {
      return (json != null) ? new Gson().fromJson(json, Location[].class) : null;
    } catch (JsonParseException e) {
      return null;
    }
  }

  /**
   * Saves events to shared preferences.
   */
  public void setLocations(Location[] locations) {
    SharedPreferences.Editor editor = mPreferences.edit();
    String json = (locations != null) ? new Gson().toJson(locations) : null;
    editor.putString(mResources.getString(R.string.pref_key_locations), json);
    editor.apply();
  }

  //////////////////////////////////////////////////////////////////////////////////////////////////
}
