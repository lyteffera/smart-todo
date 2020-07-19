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
 * The TaskProcessor class will process a List of tasks (ListRecords), and add them to the 
 * user's Calendar. 
 */
public class TaskProcessor {
    /**
     * Adds ListRecord task to calendar, using hard-coded times. 
     * In the future, I'll need to get those times from the ListRecord (but first I need to 
     * understand how the ListRecord's est_time and due_date are formatted).
     * @param task
     * @throws IOException
     */
     private Calendar service; 
     private CalendarQuickstart quickstart = new CalendarQuickstart(); 
     public TaskProcessor() {
         service = quickstart.getCalendarService();
     }
    public void addEventToCalendar(ListRecord task) throws IOException {
        Event event = new Event()
            .setSummary(task.getMessage())
            .setId(task.getId());
            
        DateTime start1 = new DateTime("2020-07-17T22:30:00+00:00");
        EventDateTime start = new EventDateTime()
            .setDateTime(start1) 
            .setTimeZone("America/New_York");
        event.setStart(start);
        DateTime end1 = new DateTime("2020-07-17T23:30:00+00:00");
        EventDateTime end = new EventDateTime()
            .setDateTime(end1)
            .setTimeZone("America/New_York");
        event.setEnd(end);
        String calendarId = "primary";
        event = service.events().insert(calendarId, event).execute();
        System.out.printf("Event created: %s\n", event.getHtmlLink());
        
    }
    public static void main(String[] args) throws IOException {
    TaskProcessor test = new TaskProcessor(); 
    Long i = 1234568L;
    ListRecord task1 = new ListRecord("12345",i,"userId","do dishes",2,"7/18/2020","7/18/2020","in-progress");
    test.addEventToCalendar(task1);

    }
}
