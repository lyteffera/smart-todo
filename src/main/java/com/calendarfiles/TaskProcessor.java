package com.calendarfiles;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.DateTime;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.CalendarScopes;
import com.google.api.services.calendar.model.FreeBusyRequest;
import com.google.api.services.calendar.model.FreeBusyRequestItem;
import com.google.api.services.calendar.model.FreeBusyResponse;
import com.google.api.services.calendar.model.FreeBusyCalendar;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.Events;
import com.google.api.services.calendar.model.EventDateTime; 
import com.google.api.client.util.DateTime; 

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.GeneralSecurityException;
import java.util.Collections;
import java.util.List; 
import java.util.Arrays;
import com.andrewrs.sps.data.ListRecord;

import com.google.gson.Gson;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.SortDirection;
import com.google.appengine.api.datastore.KeyFactory;
/**
 * The TaskProcessor class has methods that will process a List of tasks (ListRecords), and add them to the 
 * user's Calendar. 
 */
public class TaskProcessor {
    private CalendarQuickstart quickstart = new CalendarQuickstart(); 
    private Calendar service; 
    public TaskProcessor() {
        service = quickstart.getCalendarService();
    }
    /**
     * Given a String date formatted as 'YYYY-MM-DD', and the hours/minutes 
     * given in 24 hour time, converts the time to a RFC 3339 formatted String. 
     * Serves as a helper function for createDateTimeFromString().
     * given the date formatted as 'MM/DD/YYYY'. 
     * @param date
     * @return a formatted String. 
     */
    private String getFormattedString(String date, Integer hours, Integer minutes){
        String[] strings = date.split("-");
        String formatted = strings[0] + "-" + strings[1] + "-" + strings[2] + "T";
        if (hours<9){
            formatted += "0" + hours;
        } else {
            formatted += hours; 
        }
        formatted += ":" + minutes + "-04:00"; 
        return formatted;
    }
    /**
     * Given a String date formatted as 'YYYY-MM-DD', and the hours/minutes 
     * given in 24 hour time, creates a DateTime object, to be used in 
     * addEventToCalendar()
     * @param String date
     * @param Integer hours 
     * @param Integer minutes
     * @return DateTime time
     */
    public DateTime createDateTimeFromString(String date, Integer hours, Integer minutes){
        String formatted = this.getFormattedString(date, hours, minutes);
        DateTime time = new DateTime(formatted);
        return time; 

    }
    /**
     * Adds ListRecord task to calendar, using hard-coded times. 
     * @param task
     * @throws IOException
     */
    public void addEventToCalendar(ListRecord task, DateTime startTime, DateTime endTime) throws IOException {
        Event event = new Event()
            .setSummary(task.getMessage())
            .setId(task.getId());
            
        //DateTime start1 = new DateTime("2020-07-17T22:30:00+00:00");
        EventDateTime start = new EventDateTime()
            .setDateTime(startTime) 
            .setTimeZone("America/New_York");
        event.setStart(start);
        //DateTime end1 = new DateTime("2020-07-17T23:30:00+00:00");
        EventDateTime end = new EventDateTime()
            .setDateTime(endTime)
            .setTimeZone("America/New_York");
        event.setEnd(end);
        String calendarId = "primary";
        event = service.events().insert(calendarId, event).execute();
        System.out.printf("Event created: %s\n", event.getHtmlLink());
        
    }
    /**
     * Adds a list of tasks to the calendar. 
     * @param List<ListRecord> tasks
     * @throws IOException
     */
     public void addTasksToCalendar(List<ListRecord> tasks){
         DateTime hardCodedStartTime = this.createDateTimeFromString("2020-8-20",13,20);
         DateTime hardCodedEndTime = this.createDateTimeFromString("2020-8-20",16,20);
         for (int i=0;i<tasks.size();i++){
             ListRecord task = tasks.get(i); 
             try {
                 this.addEventToCalendar(task,hardCodedStartTime,hardCodedEndTime);
             }
             catch(IOException e){
                 e.printStackTrace();
             }
         }
     }
    public static void main(String[] args) throws IOException {
        TaskProcessor test = new TaskProcessor(); 
        Long i = 1234568L;
        ListRecord task1 = new ListRecord("12345",i,"userId","do dishes",2,"7/18/2020","7/18/2020","in-progress");
        DateTime startSample = test.createDateTimeFromString("2020-7-20",9,20);
        DateTime endSample = test.createDateTimeFromString("2020-7-20",11,20);
        test.addEventToCalendar(task1,startSample,endSample);
    }
}