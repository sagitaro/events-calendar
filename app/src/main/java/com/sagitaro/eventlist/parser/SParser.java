package com.sagitaro.eventlist.parser;

import android.text.TextUtils;

import com.sagitaro.eventlist.model.Event;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Patrik on 30. 12. 2016.
 */

public class SParser {
  static Pattern eventPattern = Pattern.compile(
    "\\[([0-9]+),([0-9]+),([0-9]+),([0-9]+),([0-9]+),([0-9]+),([0-9]+),\"([^\"]+)\",\"([^\"]*?)\",([0-9]+),\"([^\"]*?)\",([0-9]+)\\]");

  /**
   * Parse string as array of events.
   *
   * @param eventsString Expected format:
   * [[startTime(Long),endTime(Long),color(int),capacity(int),registered(int),?(int),?(int),title(String),note(String),?(int),?(String),cost(int)],...]
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
      event.id = new Random().nextLong();
      event.startTime = Long.parseLong(matcher.group(1));
      event.endTime = Long.parseLong(matcher.group(2));
      event.color = Integer.parseInt(matcher.group(3));
      event.capacity = Integer.parseInt(matcher.group(4));
      event.title = matcher.group(8);
      event.note = matcher.group(9);
      event.cost = Double.parseDouble(matcher.group(12)) / 100;
      event.currency = "CZK";

      eventList.add(event);
    }
    Event[] eventArray = new Event[eventList.size()];
    eventList.toArray(eventArray);
    return eventArray;
  }
}
