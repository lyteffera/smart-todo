package com.google.sps.servlets;

import java.io.IOException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.SortDirection;
import com.google.appengine.api.datastore.KeyFactory;

import java.util.ArrayList;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import com.andrewrs.sps.data.ListRecord;
import com.andrewrs.sps.utils.StringUtil;
import com.calendarfiles.CalendarQuickstart; 
import com.google.api.services.calendar.Calendar;
import com.google.api.client.util.DateTime;
/** Servlet that returns some example content. TODO: modify this file to handle comments data */
@WebServlet("/calendar_stuff")
public class CalendarCheckFunctionality extends HttpServlet {
  CalendarQuickstart quickstart = new CalendarQuickstart(); 
  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException 
  {
    Calendar cal = quickstart.getCalendarService(); 
    DateTime testDate = new DateTime("2020-07-19T00:27:08+00:00");
    try {
        quickstart.printUpcomingEvents(cal,testDate); 
    }
    catch(Exception e) {
        e.printStackTrace();
    }
  }
    

 

  
}
