package com.sagitaro.eventlist.fragment.calendar;

import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;

import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sagitaro.eventlist.adapter.EventsAdapter;
import com.sagitaro.eventlist.fragment.ViewModelFragment;
import com.sagitaro.eventlist.model.Event;
import com.sagitaro.eventlist.model.Location;
import com.sagitaro.eventlist.viewids.FragmentCalendarViewIds;
import com.sagitaro.eventlist.viewmodel.calendar.CalendarViewModel;
import com.sagitaro.eventlist.viewmodel.calendar.CalendarViewModel.Argument;
import com.sagitaro.eventlist.viewmodel.calendar.ICalendarView;

import eu.inloop.viewmodel.binding.ViewModelBindingConfig;
import timber.log.Timber;

/**
 * Fragment with list of events.
 */
public class CalendarFragment
  extends ViewModelFragment<ICalendarView, CalendarViewModel>
  implements ICalendarView {

  /* Private Constants ***********************************************************************/

  private final int SPACE_BETWEEN_ITEMS = 0;

  /* Private Attributes ***************************************************************************/

  /**
   * Activity views.
   */
  private FragmentCalendarViewIds mViews;

  private EventsAdapter mAdapter;

  /* Public Static Methods ***********************************************************************/

  public static CalendarFragment createInstance(Location location) {
    CalendarFragment fragment = new CalendarFragment();
    Bundle arguments = new Bundle();

    Gson gson = new GsonBuilder().create();
    arguments.putString(Argument.LOCATION, gson.toJson(location));
    fragment.setArguments(arguments);
    return fragment;
  }

  /* Public Methods *****************************************************************************/

  /**
   * @see ViewModelFragment#getPageCategory()
   */
  @Override
  public int getPageCategory() {
    return 0;
  }

  /**
   * @see ViewModelFragment#getViewModelClass()
   */
  @Nullable
  @Override
  public Class getViewModelClass() {
    return CalendarViewModel.class;
  }

  /**
   * @see ViewModelFragment#onCreateView(LayoutInflater, ViewGroup, Bundle)
   */
  @Nullable
  @Override
  public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
    @Nullable Bundle savedInstanceState) {

    Timber.d("onCreateView()");

    super.onCreateView(inflater, container, savedInstanceState);

    // Setup layout and inject activity views.
    mViews = FragmentCalendarViewIds.inflate(inflater, container);

    setupViews();

    addListeners();

    return mViews.rootView;
  }

  /**
   * @see ViewModelFragment#onViewCreated(View, Bundle)
   */
  @Override
  public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    setModelView(this);
  }

  /**
   * @see ViewModelFragment#onDestroyView()
   */
  @Override
  public void onDestroyView() {
    Timber.d("onDestroyView()");

    super.onDestroyView();

    removeListeners();
  }

  /**
   * @see ViewModelFragment#getViewModelBindingConfig()
   */
  @Nullable
  @Override
  public ViewModelBindingConfig getViewModelBindingConfig() {
    Timber.d("getViewModelBindingConfig()");
    return null;
  }

  /**
   * @see ViewModelFragment#removeViewModel()
   */
  @Override
  public void removeViewModel() {
    Timber.d("removeViewModel()");
  }

  //////////////////////////////////////////////////////////////////////////////////////////////////

  /**
   * @see ICalendarView#setEvents(Event[])
   */
  @Override
  public void setEvents(Event[] events) {
    Timber.d("setEvents()");

    mAdapter.clearEvents();
    mAdapter.addEvents(events);
    mAdapter.notifyDataSetChanged();
  }

  /**
   * @see ICalendarView#setRefreshing(boolean) (boolean)
   */
  public void setRefreshing(boolean refreshing) {
    Timber.d("setRefreshing(): " + refreshing);

    mViews.swipeRefreshLayout.setRefreshing(refreshing);
  }

  /**
   * @see ICalendarView#setEmpty(boolean)
   */
  @Override
  public void setEmpty(boolean isEmpty) {
    Timber.d("setEmpty(): setEmpty: " + isEmpty);

    mViews.eventsRecyclerView.setVisibility(isEmpty ? View.GONE : View.VISIBLE);
    mViews.emptyView.setVisibility(isEmpty ? View.VISIBLE : View.GONE);
  }

  /**
   * @see ICalendarView#setTitle(String)
   */
  @Override
  public void setTitle(String title) {
    Timber.d("setTitle(): " + title);
    mViews.titleTextView.setText(title);
  }

  /**
   * @see ICalendarView#setLastUpdate(String)
   */
  @Override
  public void setLastUpdate(String lastUpdate) {
    Timber.d("setLastUpdate(): " + lastUpdate);
    mViews.lastUpdateTextView.setText(lastUpdate);
  }

  /* Private Methods **************************************************************************/

  private void setupViews() {
    // Initialize recycler view.
    mAdapter = new EventsAdapter(getViewModel(), this.getContext());
    mViews.eventsRecyclerView.setAdapter(mAdapter);
    mViews.eventsRecyclerView.setHasFixedSize(true);
    LinearLayoutManager llm = new LinearLayoutManager(getActivity());
    llm.setOrientation(LinearLayoutManager.VERTICAL);
    mViews.eventsRecyclerView.setLayoutManager(llm);
    mViews.eventsRecyclerView
      .addItemDecoration(new DividerItemDecoration(getContext(), SPACE_BETWEEN_ITEMS));

    // TODO
  }

  private void addListeners() {
    mViews.swipeRefreshLayout.setOnRefreshListener(new OnRefreshListener() {
      @Override
      public void onRefresh() {
        setRefreshing(getViewModel().requestEvents());
      }
    });

    mViews.eventsRecyclerView.setOnScrollListener(new RecyclerView.OnScrollListener() {
      @Override
      public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
        if (dy > 0 || dy < 0 && mViews.floatingButton.isShown()) {
          mViews.floatingButton.hide();
        }
      }

      @Override
      public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
        if (newState == RecyclerView.SCROLL_STATE_IDLE) {
          mViews.floatingButton.show();
        }
        super.onScrollStateChanged(recyclerView, newState);
      }
    });

    mViews.floatingButton.setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View view) {
        mViews.eventsRecyclerView.smoothScrollToPosition(0);
      }
    });
  }

  private void removeListeners() {
    mViews.swipeRefreshLayout.setOnRefreshListener(null);
    mViews.eventsRecyclerView.setOnScrollListener(null);
    mViews.floatingButton.setOnClickListener(null);
  }
}
