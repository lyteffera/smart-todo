package com.andrewrs.sps.scheduling;



import com.google.api.services.calendar.Calendar;
import com.google.api.client.util.DateTime;

import com.calendarfiles.Gcal;
import com.andrewrs.sps.data.ListRecord;
import com.andrewrs.sps.utils.JsonObjectification;
import com.andrewrs.sps.utils.JsonObject;
import com.andrewrs.sps.utils.TimeSpan;
import com.andrewrs.sps.utils.Day;
import com.andrewrs.sps.utils.TimeRange;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.Entity;

import java.util.HashMap;
import java.util.ArrayList;
import java.text.SimpleDateFormat;
import java.util.Date;
import com.andrewrs.sps.utils.DataServiceInterface;
import java.text.ParseException;

public class Schedule {

    private Calendar calendar;
    private HashMap<String,TimeSpan> map;
    private TimeSpan[] timespans;
    public Schedule(ListRecord[] records)
    {
        String json = null;
        map = new HashMap<String,TimeSpan>();
        ArrayList<ListRecord> recordsDb  = DataServiceInterface.getAllSorted();
        timespans = new TimeSpan[recordsDb.size()];
        {
            int i = recordsDb.size()-1;
            for(ListRecord record:recordsDb)
            {
                timespans[i] = (new TimeSpan(record));
                map.put(record.getId(),timespans[i]);
                i--;
            }
        }
        for(int i = recordsDb.size()-1; i >= 0;i--)
        {
            timespans[i].assignDependency(map);
        }
        calendar = Gcal.getCalendarService();
        for(int i = 0; i < recordsDb.size();i++)
        {
            TimeSpan current = timespans[i];
            if(current.getScheduledDate() <=0 )
            {
                JsonObject freeBusy = JsonObjectification.objectify(
                    Gcal.getFreeBusy(calendar,
                            new DateTime(current.getStart()),new DateTime(current.getEnd())
                            ));
                JsonObject busy = freeBusy.getChild("calendars.primary.busy");
                // if(busy.getChildren().size() == 0)
                // {
                //     current.setScheduledDate(current.getStart() - (current.getStart()%360000*24) + 360000*9);
                //     current.save();
                //     continue;
                // }
                int checkedDays = 0;
                while(current.getScheduledDate() <= 0 && checkedDays*360000*24 < (current.getEnd()-current.getStart()))
                {
                    ArrayList<TimeRange> timesInDay = new ArrayList<TimeRange>();
                    for(JsonObject busyTime:busy.getChildren())
                    {
                        String startString = busyTime.getChild("start").getData();
                        String endString = busyTime.getChild("end").getData();
                        long start = parseTime(startString);
                        long end = parseTime(endString);
                        if(timesInDay.size() == 0 || timesInDay.get(timesInDay.size()-1).getDay() == start/(360000*24) &&
                        timesInDay.get(timesInDay.size()-1).getDay() > start/(360000*24) + checkedDays)
                        {
                            TimeRange range = TimeRange.fromStartDuration((int)start%(360000*24),(int)end%(360000*24));
                            range.setDay((int)(start/(360000*24)));
                            timesInDay.add(range);
                        }
                    }

                    Day today = new Day();
                    today.blockOutEventTimes(timesInDay);
                    ArrayList<TimeRange> todaysAvailabilities = today.buildAvailabilities((int)current.getEstTime());
                    if(todaysAvailabilities.size() > 0)
                    {
                        current.setScheduledDate(
                            (long)(todaysAvailabilities.get(0).start()+(360000*24*checkedDays)+current.getStart()%(360000*24)));
                    }
                    checkedDays++;

                    if(current.getScheduledDate() > 0) current.save();
                    else System.out.println("NOT SAVING: " + current.getPrimary().getMessage() + " "+ current.getPrimary().hashID() + " date: " + current.getScheduledDate());

                    Entity entity = DataServiceInterface.get(KeyFactory.stringToKey(current.getPrimary().getId()));
                    if(entity != null)
                    {
                        entity.setProperty("scheduled_date", current.getPrimary().getScheduledTime());
                        DataServiceInterface.put(entity);
                    }
                }

            }
            else
            {
                current.setScheduledDate(current.getScheduledDate());
            }
        }
    }
    private long parseTime(String s)
    {
      long rtnData = -1;
      SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm");
      try {
            Date d = f.parse(s);
            rtnData = d.getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return rtnData;
    }

}