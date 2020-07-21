
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
import com.andrewrs.sps.scheduling.Schedule;
import com.andrewrs.sps.utils.DataServiceInterface;

/** Servlet that returns some example content. TODO: modify this file to handle comments data */
@WebServlet("/data")
public class DataServlet extends HttpServlet {
  private Gson gson;
  public void init()
  {
    gson = new Gson();
  }
  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException
  {
    response.setContentType("text/json;");
    response.getWriter().println(gson.toJson(DataServiceInterface.getAll()));
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
      DataServiceInterface.put(entity);
      ListRecord newEntity = DataServiceInterface.entityToLR(entity);
      ListRecord[] entities = new ListRecord[1];
      entities[0] = newEntity;
      Schedule scheduler = new Schedule(entities);
      DataServiceInterface.put(entity);
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


}