package com.sagitaro.eventlist.model;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.sagitaro.eventlist.helper.DateHelper;
import com.sagitaro.eventlist.helper.DateHelper.Output;

import java.io.Serializable;

/**
 * Date range used to specify departure or return.
 */
public class DateRange implements Serializable {

  /* Public Constants *****************************************************************************/

  /**
   * Debug log tag.
   */
  public static final String TAG = "com.trinerdis.skypicker.model.search.DateRange";

  /* Private Attributes ***************************************************************************/

  private Long firstDate;  // UNIX timestamp in milliseconds.

  private Long lastDate;  // UNIX timestamp in milliseconds.

  private Integer color;  // Color for use in calendar.

  /* Public Methods *******************************************************************************/

  public DateRange(Long firstDate, Long lastDate) {
    this.firstDate = firstDate;
    this.lastDate = lastDate;
    correctOrder();
  }

  public DateRange(Long specificDate) {
    this.firstDate = specificDate;
    this.lastDate = specificDate;
  }

  public DateRange(DateRange other) {
    if (other != null) {
      this.firstDate = other.firstDate;
      this.lastDate = other.lastDate;
      this.color = other.color;
    }
  }

  public DateRange(Long firstDate, Long lastDate, Integer color) {
    this.firstDate = firstDate;
    this.lastDate = lastDate;
    this.color = color;
    correctOrder();
  }

  public DateRange(DateRange other, Integer color) {
    if (other != null) {
      this.firstDate = other.firstDate;
      this.lastDate = other.lastDate;
      this.color = color;
    }
  }

  //////////////////////////////////////////////////////////////////////////////////////////////////

  public Long getFirstDate() {
    return this.firstDate;
  }

  public void setFirstDate(Long firstDate) {
    this.firstDate = firstDate;
    correctOrder();
  }

  public Long getLastDate() {
    return this.lastDate;
  }

  public void setLastDate(Long lastDate) {
    this.lastDate = lastDate;
    correctOrder();
  }

  public Integer getColor() {
    return this.color;
  }

  public void setColor(Integer color) {
    this.color = color;
  }

  //////////////////////////////////////////////////////////////////////////////////////////////////

  /**
   * Indicates if the range is just one specific date.
   */
  public boolean isSpecific() {
    return (this.firstDate != null) && (this.lastDate != null) &&
      this.firstDate.equals(this.lastDate);
  }

  /**
   * Returns true if the timestamp is between first and last date.
   */
  public boolean isInRange(Long timestamp) {
    return (timestamp != null) && (this.firstDate != null) && (this.lastDate != null) &&
      (timestamp >= this.firstDate) && (timestamp <= this.lastDate);
  }

  //////////////////////////////////////////////////////////////////////////////////////////////////

  @Override
  public boolean equals(Object o) {
    if (!(o instanceof DateRange)) {
      return false;
    }
    DateRange that = (DateRange) o;
    Gson gson = new GsonBuilder().serializeNulls().create();
    JsonParser parser = new JsonParser();
    JsonElement jsonThis = parser.parse(gson.toJson(this));
    JsonElement jsonThat = parser.parse(gson.toJson(that));
    return jsonThis.equals(jsonThat);
  }

  @Override
  public String toString() {
    return "{ "
      + "firstDate: " + this.firstDate + ", "
      + "lastDate: " + this.lastDate + ", "
      + "backgroundColor: " + this.color + ", "
      + DateHelper.format(Output.ALL, this)
      + " }";
  }

  /* Private Methods ******************************************************************************/

  /**
   * Corrects order of first and last date if necessary.
   */
  private void correctOrder() {
    if ((this.firstDate != null) && (this.lastDate != null) && (this.firstDate > this.lastDate)) {
      Long tempDate = this.firstDate;
      this.firstDate = this.lastDate;
      this.lastDate = tempDate;
    }
  }
}
