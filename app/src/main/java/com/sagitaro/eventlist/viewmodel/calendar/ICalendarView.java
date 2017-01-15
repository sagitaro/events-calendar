package com.sagitaro.eventlist.viewmodel.calendar;

import android.support.annotation.Nullable;

import com.sagitaro.eventlist.model.Event;

import eu.inloop.viewmodel.IView;
import eu.inloop.viewmodel.binding.ViewModelBindingConfig;

/**
 * Created by Patrik on 30. 12. 2016.
 */

public interface ICalendarView extends IView {
  void setEvents(Event[] events);
  void setEmpty(boolean empty);
  void setRefreshing(boolean refreshing);
  void setTitle(String title);
  void setLastUpdate(String lastUpdate);
}
