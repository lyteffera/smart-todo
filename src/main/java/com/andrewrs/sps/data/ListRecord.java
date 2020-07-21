package com.andrewrs.sps.data;

import java.time.LocalDateTime;
import com.calendarfiles.Gcal;
import com.google.api.services.calendar.model.Event;
import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.model.EventDateTime;
import com.google.api.services.calendar.model.EventAttendee;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.model.EventReminder;

import java.util.Arrays;

public class ListRecord {
    private long timeStamp, scheduled_date;
    private double est_time;
    private String id,dependency,userId,message,due_date,completion_date,status;
    public ListRecord(String id) {this.id = id; scheduled_date = 0;}
    public ListRecord(String id, String dependency, long timeStamp, String userId, String message, double est_time,
                            String due_date, String completion_date, String status, long scheduled_date)
    {
      this.id = id;
      this.timeStamp = timeStamp;
      this.message = message;
      this.userId = userId;
      this.est_time = est_time;
      this.due_date = due_date;
      this.completion_date = completion_date;
      this.dependency = dependency;
      this.status = status;
      this.scheduled_date = scheduled_date;
    }

    public boolean deleteCalendarEvent() {
      try {
        Calendar service = Gcal.getCalendarService();
        service.events().delete("primary", hashID()).execute();
        return true;
      } catch(Exception e) {
        e.printStackTrace();
        return false;
      }
    }

    public boolean createCalendarEvent() {
      try {
        Calendar service = Gcal.getCalendarService();
        System.out.println("list record id: " + hashID());
        Event event = new Event()
            .setSummary(this.message)
            .setId(hashID())
            .setDescription("Automatically made by smart-todo.com " + this.message);

        // get value from scheduled_date
        DateTime startDateTime = new DateTime(this.scheduled_date);
        EventDateTime start = new EventDateTime()
            .setDateTime(startDateTime)
            .setTimeZone("America/Los_Angeles");
        event.setStart(start);

        // event end time will be determined by scheduling software
        DateTime endDateTime = new DateTime((long)(this.scheduled_date + (this.est_time * 3600 * 1000)));
        EventDateTime end = new EventDateTime()
            .setDateTime(endDateTime)
            .setTimeZone("America/Los_Angeles");
        event.setEnd(end);

        System.out.println("startDate: " + startDateTime.getValue() + " endDate: " + endDateTime.getValue());
        EventReminder[] reminderOverrides = new EventReminder[] {
            new EventReminder().setMethod("email").setMinutes(24 * 60),
            new EventReminder().setMethod("popup").setMinutes(10),
        };
        Event.Reminders reminders = new Event.Reminders()
            .setUseDefault(false)
            .setOverrides(Arrays.asList(reminderOverrides));
        event.setReminders(reminders);

        String calendarId = "primary";
        event = service.events().insert(calendarId, event).execute();
        System.out.printf("Event created: %s\n", event.getHtmlLink());
      } catch(Exception e) {
        e.printStackTrace();
        return false;
      }
      return true;
    }

    public String getStatus() {
      return this.status;
    }

    public String hashID() {
      long hashedID = 1;
      for (char c : this.id.toCharArray()) {
        hashedID *= c + 123;
      }
      return Long.toUnsignedString(hashedID);
    }
    public String getDueDate()
    {
        return due_date;
    }
    public String getDepenency()
    {
        return dependency;
    }
    public String getId()
    {
        return id;
    }
    public String getUserId()
    {
        return userId;
    }
    public double getEstTime()
    {
        return est_time;
    }
    public long getTimeStamp()
    {
        return timeStamp;
    }
    public String getMessage()
    {
        return message;
    }
    public String getCompletionDate()
    {
        return completion_date;
    }
    public long getScheduledTime()
    {
        return scheduled_date;
    }
    public void setScheduledTime(long time)
    {
        scheduled_date = time;
    }
}