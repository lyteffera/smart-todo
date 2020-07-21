
package com.andrewrs.sps.utils;

import com.andrewrs.sps.data.ListRecord;

import java.util.ArrayList;
import java.util.HashMap;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.text.ParseException;

public final class TimeSpan {

  private long start = 0,end = 0;
  private ListRecord primary;
  private TimeSpan dependency,parent;
public TimeSpan(){}
  //assign dep/ parents.
  //iterate through list of time spans from most
  public TimeSpan(ListRecord primary)
  {
      start = System.currentTimeMillis();
      SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd");
      this.primary = primary;
      try {
            Date d = f.parse(primary.getDueDate());
            end = d.getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
  }
  public void setScheduledDate(long time)
  {
      primary.setScheduledTime(time);
      if(parent != null)
        parent.setStart(time);
  }
  public void assignDependency(HashMap<String,TimeSpan> lookup)
  {
    dependency = lookup.get(primary.getDepenency());
      if(dependency == null)
            dependency = new TimeSpan();
      else
      {
          dependency.setParent(this);
          dependency.setEnd(this.end-(long)(primary.getEstTime()*3600*1000));
          dependency.assignDependency(lookup);
      }
  }
  private void setParent(TimeSpan parent)
  {
    this.parent = parent;
    if(primary.getScheduledTime() > this.parent.getStart())
      this.parent.setStart(primary.getScheduledTime());
  }
  public void setStart(long start)
  {
      if(this.start < start)
      {
          this.start = start;
      }
      if(parent != null)
      {
          parent.setStart(this.start);
      }
  }
  public void setEnd(long end)
  {
      if(end < this.end)
        this.end = end;
  }
  @Override
  public int hashCode()
  {
    return Long.hashCode(start) + dependency.hashCode();
  }

  public long getStart()
  {
      return this.start;
  }
  public long getEnd()
  {
      return this.end;
  }
  public long getEstTime()
  {
      return (long)(primary.getEstTime()*360000);
  }
  public long getScheduledDate()
  {
      return primary.getScheduledTime();
  }
  public void save()
  {
      primary.createCalendarEvent();
  }
  public ListRecord getPrimary()
  {
      return primary;
  }
}
