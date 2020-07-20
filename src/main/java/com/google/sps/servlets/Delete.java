



// Copyright 2019 Google LLC
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     https://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

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
import com.google.appengine.api.datastore.Key;

import java.util.ArrayList;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.io.BufferedReader;

import com.andrewrs.sps.data.ListRecord;
import com.andrewrs.sps.utils.StringUtil;
import com.andrewrs.sps.utils.JsonObjectification;
import com.andrewrs.sps.utils.JsonObject;
import com.andrewrs.sps.utils.HTTPHandler;
/** Servlet that returns some example content. TODO: modify this file to handle comments data */
@WebServlet("/delete")
public class Delete extends HttpServlet {
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
    response.sendRedirect("/index.html");
  }
    @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException
  {
      BufferedReader reader = request.getReader();
      StringBuilder body = new StringBuilder();
      String currentLine;
      while((currentLine = reader.readLine()) != null)
      {
          body.append(currentLine);
      }
      JsonObject parsedBodyData = JsonObjectification.objectify(body.toString());
      Key[] keys = new Key[parsedBodyData.getChildren().size()];
      int i = 0;
      ListRecord[] eventsToDelete = new ListRecord[parsedBodyData.getChildren().size()];
      for(JsonObject object:parsedBodyData.getChildren())
      {
        try{
          keys[i] = KeyFactory.stringToKey(object.getChild("id").getData());
          eventsToDelete[i] = new ListRecord(object.getChild("id").getData());
        }catch(Exception e)
        {
          e.printStackTrace();
        }
        i++;
      }
      for(i = 0; i < keys.length; ++i)
      {
        try
        {
          if(eventsToDelete[i].deleteCalendarEvent()) datastore.delete(keys[i]);
        }catch(Exception e)
        {
          e.printStackTrace();
        }
      }


    response.sendRedirect("/index.html");
  }

  private String getParameter(HttpServletRequest request, String name, String defaultValue)
  {
    String value = request.getParameter(name);
    if (value == null) {
      return defaultValue;
    }
    return value;
  }
}
