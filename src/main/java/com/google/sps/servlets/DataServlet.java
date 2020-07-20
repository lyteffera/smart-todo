
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

/** Servlet that returns some example content. TODO: modify this file to handle comments data */
@WebServlet("/data")
public class DataServlet extends HttpServlet {
  private DatastoreService datastore;
  private Gson gson;
  public void init()
  {
    datastore = DatastoreServiceFactory.getDatastoreService();
    gson = new Gson();
  }
  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException
  {
    ArrayList<ListRecord> records = new ArrayList<ListRecord>();
    ArrayList<ListRecord> closedRecords = new ArrayList<ListRecord>();
    response.setContentType("text/json;");
    Query query = new Query("message_log").addSort("timeStamp", SortDirection.DESCENDING);
    PreparedQuery results = datastore.prepare(query);
    for (Entity entity : results.asIterable())
    {
      ListRecord temp = entityToLR(entity);
      if (temp.getStatus().equals("closed")) closedRecords.add(temp);
      else records.add(temp);
    }
    for(ListRecord record:closedRecords)
    {
        records.add(record);
    }
    response.getWriter().println(gson.toJson(records));
  }
    @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException
  {
    String message = getParameter(request, "message", "");
    String est_time = getParameter(request, "est_time", "");
    String due_date = getParameter(request, "due_date", "");
    String completion_date = getParameter(request, "completion_date", "");
    String user_id = getParameter(request, "user_id", "");
    String dependency = getParameter(request, "dependency", "");
    String status = "open";

    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();

      Entity entity = new Entity("message_log");
      entity.setProperty("timeStamp", System.currentTimeMillis());
      entity.setProperty("message", StringUtil.escapeQuotesInParameter(message));
      entity.setProperty("user_id", StringUtil.escapeQuotesInParameter(user_id));
      entity.setProperty("est_time", StringUtil.escapeQuotesInParameter(est_time));
      entity.setProperty("due_date", StringUtil.escapeQuotesInParameter(due_date));
      entity.setProperty("completion_date", StringUtil.escapeQuotesInParameter(completion_date));
      entity.setProperty("dependency", StringUtil.escapeQuotesInParameter(dependency));
      entity.setProperty("status", status);
      entity.setProperty("scheduled_date", -1);
      datastore.put(entity);
      ListRecord newEntity = entityToLR(entity);
      newEntity.createCalendarEvent();
    response.sendRedirect("/index.html");
  }

  private String getParameter(HttpServletRequest request, String name, String defaultValue)
  {
    String value = request.getParameter(name);
    if (value == null) {
      value = defaultValue;
    }
    return value;
  }

  private ListRecord entityToLR(Entity entity) {
    long time = -1, scheduled_date = -1;
    double est_time = -1;
    String user_id = "", message = "", due_date = "", completion_date = "", status = "invalid", dependency = "";
    String id = "";
    try{
        id = KeyFactory.keyToString(entity.getKey());
    }catch(Exception e)
    {
      System.out.print("Could not find id, ");
    }
    try{
        time = Long.parseLong((String)entity.getProperty("timeStamp"));
    }catch(Exception e)
    {
      System.out.print("time, ");
    }
    try{
      scheduled_date = Long.parseLong((String)entity.getProperty("scheduled_date"));
    }catch(Exception e)
    {
      System.out.print("scheduled_date, ");
    }
    try{
        est_time = Double.parseDouble((String)entity.getProperty("est_time"));
    }catch(Exception e)
    {
      System.out.print("est_time, ");
    }
    try{
        message = (String)entity.getProperty("message");
    }catch(Exception e){
      System.out.print("message, ");
    }
    try{
      dependency = (String)entity.getProperty("dependency");
    }catch(Exception e){
    System.out.print("dependency, ");
  }
    try{
        user_id = (String)entity.getProperty("user_id");
    }catch(Exception e){
      System.out.print("user_id, ");
    }
    try{
        due_date = (String)entity.getProperty("due_date");
    }catch(Exception e){
      System.out.print("due_date, ");
    }
    try{
        completion_date = (String)entity.getProperty("completion_date");
    }catch(Exception e){
      System.out.print("completion_date, ");
    }
    try{
        status = (String)entity.getProperty("status");
    }catch(Exception e){
      System.out.print("status");
    }
    return new ListRecord(id, dependency, time, user_id, message, est_time, due_date, completion_date, status, scheduled_date);
  }
}