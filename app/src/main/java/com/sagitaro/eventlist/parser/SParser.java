package com.sagitaro.eventlist.parser;

import android.graphics.Color;
import android.text.TextUtils;

import com.sagitaro.eventlist.App;
import com.sagitaro.eventlist.R;
import com.sagitaro.eventlist.model.Event;

import org.parceler.apache.commons.lang.StringEscapeUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Patrik on 30. 12. 2016.
 */

public class SParser {
  static Pattern eventPattern = Pattern.compile(
    "\\[([0-9]+),([0-9]+),([0-9]+),([0-9]+),([0-9]+),([0-9]+),([0-9]+),\"([^\"]+)\",\"([^\"]*?)\",([0-9]+),\"([^\"]*?)\",([0-9]+)\\]");

  static int[][] colors = new int[][] {
    { Color.parseColor("#668CD9"), Color.parseColor("#2952A3") },
    { Color.parseColor("#D96666"), Color.parseColor("#A32929") },
    { Color.parseColor("#8CBF40"), Color.parseColor("#528800") },
    { Color.parseColor("#C4A883"), Color.parseColor("#8D6F47") },
    { Color.parseColor("#8C66D9"), Color.parseColor("#5229A3") },
    { Color.parseColor("#8997A5"), Color.parseColor("#4E5D6C") },
    { Color.parseColor("#E67399"), Color.parseColor("#B1365F") },
    { Color.parseColor("#BFBF4D"), Color.parseColor("#88880E") },
    { Color.parseColor("#85AAA5"), Color.parseColor("#4A716C") },
    { Color.parseColor("#59BFB3"), Color.parseColor("#1B887A") },
    { Color.parseColor("#58BBD2"), Color.parseColor("#00A3C2") },
    { Color.parseColor("#80B7E7"), Color.parseColor("#2D83D5") },
    { Color.parseColor("#9DC0AC"), Color.parseColor("#75A38C") },
    { Color.parseColor("#939871"), Color.parseColor("#737A52") },
    { Color.parseColor("#F3C857"), Color.parseColor("#E7A732") }
  };

  /**
   * Parse string as array of events.
   *
   * @param eventsString Expected format:
   * [[startTime(Long),endTime(Long),backgroundColor(int),capacity(int),registered(int),colorIndexHeader(int),colorIndexContent(int),title(String),note(String),?(int),?(String),cost(int)],...]
   * [[1480350600,1480356000,15583653,20,0,3,7,"Pronájem","Prvni radek\r\nDruhý řádek\r\n\r\nVíce
   * informací na http://www.google.com.",0,"",0]]
   *
   * @return Array with parsed events.
   */
  public static Event[] parse(String eventsString) {
    if (TextUtils.isEmpty(eventsString)) {
      return null;
    }
    List<Event> eventList = new ArrayList<>();
    Matcher matcher = eventPattern.matcher(eventsString);
    int offset = 0;
    while (matcher.find(offset)) {
      offset = matcher.end();

      Event event = new Event();
      event.id = Integer.parseInt(matcher.group(3));
      event.startTime = Long.parseLong(matcher.group(1)) * 1000;
      event.endTime = Long.parseLong(matcher.group(2)) * 1000;
      event.capacity = Integer.parseInt(matcher.group(4));
      event.registered = Integer.parseInt(matcher.group(5));
      event.backgroundColor = colors[Integer.parseInt(matcher.group(7))][0];
      event.borderColor = colors[Integer.parseInt(matcher.group(7))][1];
      event.textColor = App.getResColor(R.color.white);
      event.title = StringEscapeUtils.unescapeJava(matcher.group(8)).trim();
      String subtitle = StringEscapeUtils.unescapeJava(matcher.group(9)).trim();
      int firstIndexOfDoubleNewLine = subtitle.indexOf("\r\n\r\n");
      String note =
        firstIndexOfDoubleNewLine == -1 ? "" : subtitle.substring(firstIndexOfDoubleNewLine).trim();
      event.subtitle = firstIndexOfDoubleNewLine == -1 ? subtitle :
        subtitle.substring(0, firstIndexOfDoubleNewLine).trim();
      event.note = note;
      event.cost = Double.parseDouble(matcher.group(12)) / 100;
      event.currency = "CZK";

      if (event.registered > 0 && event.registered >= event.capacity) {
        event.backgroundColor = Color.WHITE;
        event.borderColor = Color.RED;
        event.textColor = Color.RED;
        event.note = App.getResString(R.string.event_full) + "\r\n\r\n" + event.note;
      }

      eventList.add(event);
    }

    // Sort by start date.
    Collections.sort(eventList, new Comparator<Event>() {
      @Override
      public int compare(Event event1, Event event2) {
        return (int) (event1.startTime - event2.startTime);
      }
    });

    Event[] eventArray = new Event[eventList.size()];
    eventList.toArray(eventArray);
    return eventArray;
  }
}
