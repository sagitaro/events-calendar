package com.sagitaro.eventlist.viewmodel.calendar;

import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sagitaro.eventlist.App;
import com.sagitaro.eventlist.R;
import com.sagitaro.eventlist.helper.DateHelper;
import com.sagitaro.eventlist.helper.DateHelper.Output;
import com.sagitaro.eventlist.helper.NetworkHelper;
import com.sagitaro.eventlist.helper.PreferencesHelper;
import com.sagitaro.eventlist.model.Event;
import com.sagitaro.eventlist.model.Location;
import com.sagitaro.eventlist.parser.SParser;

import java.util.ArrayList;
import java.util.List;

import eu.inloop.viewmodel.AbstractViewModel;
import eu.inloop.viewmodel.IView;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import timber.log.Timber;

public class CalendarViewModel extends AbstractViewModel<ICalendarView> {

  /* Public Types *********************************************************************************/

  /**
   * Extra identifiers.
   */
  public static class Argument {
    public static final String LANGUAGE = "language";
    public static final String LIST_REFRESHING = "list_refreshing";
    public static final String LIST_EMPTY = "list_empty";
    public static final String LOCATION = "location";
    //public static final String EVENTS = "events";
  }

  /* Private Attributes ***************************************************************************/

  /**
   * {@link PreferencesHelper} singleton instance.
   */
  private PreferencesHelper mPreferencesHelper;

  //////////////////////////////////////////////////////////////////////////////////////////////////

  /**
   * Indicates that the content (list) is being refreshed.
   */
  private boolean mRefreshing;

  /**
   * Indicates that the content (list) is empty.
   */
  private boolean mEmpty = false;

  /**
   * Holds events.
   */
  private Event[] mEvents;

  /**
   * Holds location info.
   */
  private Location mLocation;

  private long mRequested;

  /* Public Methods *******************************************************************************/

  /**
   * @see AbstractViewModel#onCreate(Bundle, Bundle)
   */
  @Override
  public void onCreate(@Nullable Bundle arguments, @Nullable Bundle savedInstanceState) {
    Timber.d("onCreate()");

    super.onCreate(arguments, savedInstanceState);

    // Inject singleton instances.
    mPreferencesHelper = App.getAppComponent().providePreferencesHelper();

    // Decide which state bundle should be used when loading arguments.
    Bundle state = (savedInstanceState != null) ? savedInstanceState : arguments;

    loadArguments(state);

    initAttributes();
  }

  private void initAttributes() {
    Timber.d("initAttributes()");
    if (mEvents == null) {
      mEvents = getEvents();
    }
  }

  public void loadArguments(Bundle state) {
    // Load arguments.
    if (state != null) {
      if (state.containsKey(Argument.LIST_REFRESHING)) {
        mRefreshing = state.getBoolean(Argument.LIST_REFRESHING);
      }
      if (state.containsKey(Argument.LIST_EMPTY)) {
        mEmpty = state.getBoolean(Argument.LIST_EMPTY);
      }
      Gson gson = new GsonBuilder().create();
      if (state.containsKey(Argument.LOCATION)) {
        mLocation = gson.fromJson(state.getString(
          Argument.LOCATION), Location.class);
      }
      /*
      if (state.containsKey(Argument.EVENTS)) {
        mEvents = gson.fromJson(state.getString(
          Argument.EVENTS), Event[].class);
      }
      */
    }
  }

  /**
   * @see AbstractViewModel#onSaveInstanceState(Bundle)
   */
  @Override
  public void onSaveInstanceState(@NonNull Bundle bundle) {
    Timber.d("onSaveInstanceState()");

    super.onSaveInstanceState(bundle);

    // Save arguments.
    //bundle.putString(Argument.LANGUAGE, mLanguage);
    bundle.putBoolean(Argument.LIST_REFRESHING, mRefreshing);
    bundle.putBoolean(Argument.LIST_EMPTY, mEmpty);

    Gson gson = new GsonBuilder().create();
    bundle.putString(Argument.LOCATION, gson.toJson(mLocation));
    //bundle.putString(Argument.EVENTS, gson.toJson(mEvents));
  }

  /**
   * @see AbstractViewModel#onBindView(IView)
   */
  @Override
  public void onBindView(@NonNull ICalendarView view) {
    Timber.d("onBindView()");

    super.onBindView(view);

    // Call setters for all arguments.
    setEmpty(mEmpty);
    setEvents(mEvents);
    setTitle(mLocation.name);
    setLastUpdate(mLocation.lastUpdate);

    // Make first network request.
    if (mEvents == null || mEvents.length < 1 || mRequested == 0) {
      setRefreshing(requestEvents());
    }
  }
  //////////////////////////////////////////////////////////////////////////////////////////////////

  /**
   * Sets whether the content (list) is being refreshed.
   *
   * @param refreshing Whether the content is being refreshed.
   */
  public void setRefreshing(boolean refreshing) {
    Timber.d("setRefreshing()");

    mRefreshing = refreshing;

    // Update view with new data.
    ICalendarView view = getView();
    if (view != null) {
      view.setRefreshing(mRefreshing);
    }
  }

  //////////////////////////////////////////////////////////////////////////////////////////////////

  public void setEmpty(boolean isEmpty) {
    Timber.d("setEmpty(): setEmpty: " + isEmpty);

    mEmpty = isEmpty;

    // Notify view about change.
    final ICalendarView view = getView();
    // Do not show empty view if there is deep search in progress.
    if (view != null) {
      view.setEmpty(mEmpty);
    }
  }

  public boolean isEmpty() {
    return mEmpty;
  }

  //////////////////////////////////////////////////////////////////////////////////////////////////

  /**
   * Sets location.
   *
   * @param location Location.
   */
  public void saveLocation(Location location) {
    Timber.d("saveLocation(): " + location.name);

    Location[] locations = mPreferencesHelper.getLocations();
    if (locations == null) {
      locations = new Location[1];
      locations[0] = mLocation;
    } else {
      for (Location loc : locations) {
        if (loc.name.equals(location.name)) {
          loc = mLocation;
          break;
        }
      }
    }
    mPreferencesHelper.setLocations(locations);
  }

  /**
   * Sets events.
   *
   * @param events Events from the server.
   */
  public void setEvents(Event[] events) {
    Timber.d("setEvents(Event[] events) : events: " + (events == null ? "null" : events.length));

    if (events == null) {
      setEmpty(true);
      return;
    }
    if (events.length == 0) {
      setEmpty(true);
    } else {
      setEmpty(false);
    }
    for (Event event : events) {
      if (event == null) {
        Timber.e("setEvents(): event is null");
        continue;
      }
      event.locationId = mLocation.id;
    }

    mPreferencesHelper.setEvents(events);

    // Update view with new data.
    ICalendarView view = getView();
    if (view != null) {
      view.setEvents(events);
    }
  }

  /**
   * Gets events.
   */
  public Event[] getEvents() {
    Timber.d("getEvents(): " + mLocation.name);

    Event[] events = mPreferencesHelper.getEvents();
    if (events == null) {
      return null;
    }
    List<Event> eventList = new ArrayList<>();
    int n = 0;
    if (events.length > 0) {
      for (Event event : events) {
        if (event == null) {
          Timber.d("Event at index " + n + " is null:" + mLocation.name);
          continue;
        }
        if (mLocation.id > 0 && event.locationId == mLocation.id) {
          eventList.add(event);
        }
      }
      n++;
    }
    eventList.toArray(events);
    return events;
  }

  public String getTitle() {
    return mLocation.name;
  }

  /**
   * Set title.
   *
   * @param title Title for the calendar.
   */
  public void setTitle(String title) {
    Timber.d("setTitle(): " + title);

    // Update view with new data.
    ICalendarView view = getView();
    if (view != null) {
      view.setTitle(title);
    }
  }

  /**
   * Sets last update.
   *
   * @param lastUpdate Last update.
   */
  public void setLastUpdate(long lastUpdate) {
    Timber.d("setLastUpdate()");

    mLocation.lastUpdate = lastUpdate;
    saveLocation(mLocation);

    // Update view with new data.
    ICalendarView view = getView();
    if (view != null) {
      String lastUpdateTime = lastUpdate == 0 ? App.getResString(R.string.never) :
        DateHelper.format(Output.ALL, lastUpdate);
      String lastUpdateString =
        App.getResString(R.string.last_update, lastUpdateTime);
      view.setLastUpdate(lastUpdateString);
    }
  }

  //////////////////////////////////////////////////////////////////////////////////////////////////

  public void onEventClick(int correctedPosition, int itemViewType) {
    Timber.d("onEventClick(): " + correctedPosition);
  }

  //////////////////////////////////////////////////////////////////////////////////////////////////

  public boolean requestEvents() {
    return requestEvents(mLocation.url);
  }

  public boolean requestEvents(String url) {
    Timber.d("requestEvents(): " + url);

    if (!NetworkHelper.isInternetAvailable()) {
      return false;
    }

    mRequested = System.currentTimeMillis();
    OkHttpHandler request = new OkHttpHandler();
    request.execute(url);

    return true;
  }

  private class OkHttpHandler extends AsyncTask<String, Void, String> {

    OkHttpClient client = new OkHttpClient();

    @Override
    protected void onPreExecute() {
      Timber.d("onPreExecute()");
      super.onPreExecute();
      setRefreshing(true);
    }

    @Override
    protected String doInBackground(String... params) {
      Timber.d("doInBackground()");

      Request.Builder builder = new Request.Builder();
      builder.url(params[0]);
      Request request = builder.build();

      try {
        Response response = client.newCall(request).execute();
        return response.body().string();
      } catch (Exception e) {
        e.printStackTrace();
      }
      return null;
    }

    @Override
    protected void onPostExecute(String response) {
      Timber.d("onPostExecute()");
      super.onPostExecute(response);

      try {
        if (!TextUtils.isEmpty(response)) {
          // TODO: Section Pattern - more specific searching.
          setEvents(SParser.parse(response));
          setLastUpdate(System.currentTimeMillis());
        } else {
          Timber.d("onPostExecute(): empty response");
        }
      } catch (Exception e) {
        Timber.d("onPostExecute(): exception: " + e.getMessage());
        //tv.setText("sorry, something went wrong!");
      }
      setRefreshing(false);
    }
  }
}
