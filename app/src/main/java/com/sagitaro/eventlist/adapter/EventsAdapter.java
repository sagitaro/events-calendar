package com.sagitaro.eventlist.adapter;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;

import android.support.annotation.NonNull;

import com.sagitaro.eventlist.App;
import com.sagitaro.eventlist.R;
import com.sagitaro.eventlist.helper.DateHelper;
import com.sagitaro.eventlist.helper.DateHelper.Output;
import com.sagitaro.eventlist.model.Event;
import com.sagitaro.eventlist.viewmodel.calendar.CalendarViewModel;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map.Entry;
import java.util.SortedMap;
import java.util.TreeMap;

import timber.log.Timber;

public class EventsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

  /* Private Constants ****************************************************************************/

  /**
   * Events.
   */
  private List<Event> mEvents;

  /**
   * Days. (Timestamp, IndexOfFirstEvent in mEvents)
   */
  private SortedMap<Long, Integer> mDays;

  /**
   * View model.
   */
  private CalendarViewModel mViewModel;

  /**
   * Context (activity/fragment) with the list.
   */
  private Context mContext;

  /* Public Types *********************************************************************************/

  /**
   * Identifiers of adapter item types.
   */
  public class ViewType {
    public static final int HEADER_DAY = 0;
    public static final int EVENT = 1;
  }

  /* Public Methods *******************************************************************************/

  /**
   * Constructor.
   *
   * @param viewModel
   * @param context
   */
  // Provide a suitable constructor (depends on the kind of dataset)
  public EventsAdapter(@NonNull CalendarViewModel viewModel,
    @NonNull Context context) {

    // Initialize attributes.
    mViewModel = viewModel;
    mContext = context;

    mEvents = new ArrayList<>();
    mDays = new TreeMap<>();
  }

  /**
   * @see RecyclerView.Adapter#onCreateViewHolder(ViewGroup, int)
   */
  @Override
  public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    LayoutInflater inflater = LayoutInflater.from(parent.getContext());
    View itemView;
    switch (viewType) {
      case ViewType.HEADER_DAY:
        itemView = inflater.inflate(R.layout.item_events_adapter_header, parent, false);
        return new HeaderViewHolder(itemView);
      default:
        itemView = inflater.inflate(R.layout.item_events_adapter_event, parent, false);
        return new EventViewHolder(itemView);
    }
  }

  /**
   * @see RecyclerView.Adapter#onBindViewHolder(ViewHolder, int)
   */
  @Override
  public void onBindViewHolder(ViewHolder holder, int position) {
    switch (holder.getItemViewType()) {
      case ViewType.HEADER_DAY:
        if (mDays.size() < 1) {
          break;
        }
        int correctedDayPosition = getCorrectedDayPosition(position);

        String dateString = "DEFAULT DAY: 01.01.2012";
        int n = 0;
        for (Long time : mDays.keySet()) {
          if (n == correctedDayPosition) {
            dateString = new LocalDate(time).dayOfWeek().getAsText()
              + " " + DateHelper.format(Output.DATE, time);
            break;
          }
          n++;
        }

        ((HeaderViewHolder) holder).setHeaderText(dateString);
        break;
      default:
        // TODO
        if (mEvents.size() < 1) {
          break;
        }
        int correctedPosition = getCorrectedEventPosition(position);
        if (correctedPosition == -1) {
          break;
        }
        // Assign the data to the views.
        ((EventViewHolder) holder).bindData(mEvents.get(correctedPosition), "");
        addEventListeners((EventViewHolder) holder);
        break;
    }
  }

  /**
   * @see RecyclerView.Adapter#getItemCount()
   */
  @Override
  public int getItemCount() {
    return mEvents.size() + mDays.size();
  }

  /**
   * @see RecyclerView.Adapter#getItemViewType(int)
   */
  @Override
  public int getItemViewType(int position) {
    return getItemViewTypeByPosition(position);
  }

  /**
   * Adds listeners to item views.
   *
   * @param holder Item view holder.
   */
  public void addEventListeners(final EventViewHolder holder) {
    Timber.d("addEventListeners()");

    // Notify view-model when user selects a place.
    holder.itemView.setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View v) {
        int position = holder.getAdapterPosition();

        Timber.d("onClick(): position: " + position);
        if (!TextUtils.isEmpty(holder.mSubtitle2TextView.getText())) {
          int newVisibility =
            holder.mSubtitle2TextView.getVisibility() == View.GONE ? View.VISIBLE : View.GONE;
          holder.mSubtitle2TextView.setVisibility(newVisibility);
        }

        int itemViewType = holder.getItemViewType();
        int correctedPosition = getCorrectedEventPosition(position);
        if (position != RecyclerView.NO_POSITION) {
          mViewModel.onEventClick(correctedPosition, itemViewType);
        }
      }
    });
  }

  //////////////////////////////////////////////////////////////////////////////////////////////////

  public void clearEvents() {
    Timber.d("clearEvents()");

    mEvents.clear();
    mDays.clear();
  }

  public void addEvents(Event[] events) {
    Timber.d("addEvents(): " + (events == null ? "null" : events.length));

    // Get only upcoming events.
    List<Event> filteredEvents = new ArrayList<>();
    if (events != null && events.length > 0) {
      for (Event event : events) {
        if (event == null) {
          Timber.e("OMG! "+mViewModel.getTitle());
          continue;
        }
        long today = new DateTime().withTimeAtStartOfDay().getMillis();
        long eventStart = new DateTime(event.startTime).withTimeAtStartOfDay().getMillis();
        if (eventStart > today) {
          filteredEvents.add(event);
        }
      }
      mEvents.addAll(filteredEvents);
      Timber.d("addEvents(): filteredEvents: " + filteredEvents.size());
    }

    // Create days.
    int indexOfFirstEvent = 0;
    for (Event event : mEvents) {
      long startOfDay = new DateTime(event.startTime).withTimeAtStartOfDay().getMillis();
      if (!mDays.keySet().contains(startOfDay)) {
        // Create a new day.
        mDays.put(startOfDay, indexOfFirstEvent);
      }
      indexOfFirstEvent++;
    }
    Timber.d("days: " + mDays.size());
  }

  /* Public Static ViewHolders ********************************************************************/

  /**
   * Header view holder.
   */
  public static class HeaderViewHolder extends RecyclerView.ViewHolder {

    // List item views.
    public TextView mHeaderTextView;

    /**
     * @see ViewHolder#ViewHolder(View)
     */
    public HeaderViewHolder(View itemView) {
      super(itemView);

      // Find item views.
      mHeaderTextView = (TextView) itemView.findViewById(R.id.events_header_text_view);
    }

    public void setHeaderText(String text) {
      mHeaderTextView.setText(text);
    }
  }

  /**
   * Event view holder.
   */
  public static class EventViewHolder extends RecyclerView.ViewHolder {

    // List item views.
    public CardView mContainer;
    public TextView mTitleTextView;
    public TextView mSubtitleTextView;
    public TextView mSubtitle2TextView;
    public TextView mCostTextView;
    public TextView mTimeTextView;
    public TextView mCapacityTextView;

    /**
     * @see ViewHolder#ViewHolder(View)
     */
    public EventViewHolder(View itemView) {
      super(itemView);

      // Find item views.
      mContainer = (CardView) itemView.findViewById(R.id.event_container);
      mTitleTextView = (TextView) itemView.findViewById(R.id.event_title_text_view);
      mSubtitleTextView = (TextView) itemView.findViewById(R.id.event_subtitle_text_view);
      mSubtitle2TextView = (TextView) itemView.findViewById(R.id.event_subtitle2_text_view);
      mCostTextView = (TextView) itemView.findViewById(R.id.event_cost_text_view);
      mTimeTextView = (TextView) itemView.findViewById(R.id.event_time_text_view);
      mCapacityTextView = (TextView) itemView.findViewById(R.id.event_capacity_text_view);
    }

    public void bindData(Event place, String filterString) {
      // TODO: Color.
      mContainer.setCardBackgroundColor(place.backgroundColor);

      mTimeTextView.setBackgroundColor(place.borderColor);
      mTimeTextView.setTextColor(place.textColor);

      mTitleTextView.setTextColor(place.textColor);
      mSubtitleTextView.setTextColor(place.textColor);
      mSubtitle2TextView.setTextColor(place.textColor);
      mCostTextView.setTextColor(place.textColor);
      mCapacityTextView.setTextColor(place.textColor);

      mTitleTextView.setText(place.title);
      mSubtitleTextView.setText(place.subtitle);
      mSubtitle2TextView.setText(place.note);
      mSubtitle2TextView.setVisibility(View.GONE);

      if (place.cost == 0) {
        mCostTextView.setText(App.getResString(R.string.cost_free));
      } else {
        mCostTextView.setText(
          App.getResString(R.string.cost_with_currency, formatDouble(place.cost), place.currency));
      }

      String dateString = DateHelper.format(Output.TIME, place.startTime) + " - "
        + DateHelper.format(Output.TIME, place.endTime);
      mTimeTextView.setText(dateString);

      String capacityString =
        App.getResString(R.string.capacity_filled_max, place.registered, place.capacity);
      mCapacityTextView.setText(capacityString);
    }

    public static String formatDouble(double d) {
      if (d == (long) d) {
        return String.format(Locale.US, "%d", (long) d);
      } else {
        return String.format("%s", d);
      }
    }
  }

  /* Private Methods ******************************************************************************/

  /**
   * Get item view type by its position in the recycler view.
   *
   * @param position
   *
   * @return
   */
  private int getItemViewTypeByPosition(int position) {
    int viewType = ViewType.EVENT;
    // If we find exact position match => it is a header.
    // Else: it is an item.
    int alreadyDrawnHeaders = 0;
    for (Entry entry : mDays.entrySet()) {
      int headerIndexInRecyclerView = (int) entry.getValue();
      if (headerIndexInRecyclerView + alreadyDrawnHeaders > position) {
        break;
      }
      if (headerIndexInRecyclerView + alreadyDrawnHeaders == position) {
        viewType = ViewType.HEADER_DAY;
        break;
      }
      alreadyDrawnHeaders++;
    }
    return viewType;
  }

  /**
   * Get position (index) of the item in its list of days.
   *
   * @param position
   *
   * @return Index of the day in mDays map.
   */
  private int getCorrectedDayPosition(int position) {
    int correctedPosition = -1;

    int alreadyDrawnHeaders = 0;
    for (Entry entry : mDays.entrySet()) {
      int headerIndexInRecyclerView = (int) entry.getValue();
      if (headerIndexInRecyclerView + alreadyDrawnHeaders == position) {
        correctedPosition = alreadyDrawnHeaders;
        break;
      }
      alreadyDrawnHeaders++;
    }
    Timber.d("getCorrectedDayPosition(): " + position + " => " + correctedPosition);
    return correctedPosition;
  }

  /**
   * Get position (index) of the item in its list of events.
   *
   * @param position
   *
   * @return Index of the event in mEvents list.
   */
  private int getCorrectedEventPosition(int position) {
    int correctedPosition = -1;

    // Counts already drawn headers.
    int alreadyDrawnHeaders = 0;
    int headerIndexInRecyclerView = -1;
    for (Entry entry : mDays.entrySet()) {
      headerIndexInRecyclerView = (int) entry.getValue();
      if (headerIndexInRecyclerView + alreadyDrawnHeaders > position) {
        // correctedPosition = position - already drawn headers.
        correctedPosition = position - alreadyDrawnHeaders;
        break;
      }
      alreadyDrawnHeaders++;
    }
    // If we could not find a corrected position, assume it was below the last header.
    if (correctedPosition == -1) {
      correctedPosition = position - alreadyDrawnHeaders;
    }
    Timber.d("getCorrectedEventPosition(): " + position + " => "
      + correctedPosition + "(days: " + mDays.size() + ")");
    return correctedPosition;
  }

  //////////////////////////////////////////////////////////////////////////////////////////////////
}
