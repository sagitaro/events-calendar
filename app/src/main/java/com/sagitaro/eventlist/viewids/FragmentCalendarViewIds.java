package com.sagitaro.eventlist.viewids;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;

import com.sagitaro.eventlist.R;

public class FragmentCalendarViewIds {
  /* Public Attributes ****************************************************************************/

  public View rootView;

  public TextView titleTextView;
  public TextView lastUpdateTextView;
  public SwipeRefreshLayout swipeRefreshLayout;
  public RecyclerView eventsRecyclerView;
  public FloatingActionButton floatingButton;
  public TextView emptyView;

  /* Public Methods *******************************************************************************/

  public FragmentCalendarViewIds(View rootView) {
    this.rootView = rootView;

    this.titleTextView =
      (TextView) rootView.findViewById(R.id.events_title_text_view);
    this.lastUpdateTextView =
      (TextView) rootView.findViewById(R.id.events_last_update_text_view);
    this.swipeRefreshLayout =
      (SwipeRefreshLayout) rootView.findViewById(R.id.events_swipe_refresh_layout);
    this.eventsRecyclerView =
      (RecyclerView) rootView.findViewById(R.id.events_recycler_view);
    this.floatingButton =
      (FloatingActionButton) rootView.findViewById(R.id.events_floating_button);
    this.emptyView =
      (TextView) rootView.findViewById(R.id.events_empty_view);
  }

  public static FragmentCalendarViewIds inflate(LayoutInflater inflater,
    ViewGroup container) {
    View rootView = inflater.inflate(R.layout.fragment_calendar, container, false);
    return new FragmentCalendarViewIds(rootView);
  }
}
