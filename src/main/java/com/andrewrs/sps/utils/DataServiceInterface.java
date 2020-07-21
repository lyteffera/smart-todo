package com.andrewrs.sps.utils;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.SortDirection;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.Key;

import com.andrewrs.sps.data.ListRecord;

import java.util.ArrayList;

public class DataServiceInterface {

  private static DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    public static void put(Entity entity)
    {
      datastore.put(entity);
    }
    public static Entity get(Key id)
    {
        try{
            return datastore.get(id);
        }catch(Exception e)
        {
            e.printStackTrace();
            return null;
        }
    }
    public static ArrayList<ListRecord> getAll()
    {
        ArrayList<ListRecord> records = new ArrayList<ListRecord>();
    ArrayList<ListRecord> closedRecords = new ArrayList<ListRecord>();
    Query query = new Query("message_log").addSort("timeStamp", SortDirection.DESCENDING);
    PreparedQuery results = datastore.prepare(query);
    for (Entity entity : results.asIterable())
    {
      ListRecord temp = entityToLR(entity);
      if (temp.getStatus().equals("closed")) 
        closedRecords.add(temp);
      else 
        records.add(temp);
    }
    for(ListRecord record:closedRecords)
    {
        records.add(record);
    }
    return records;
    }
    public static ArrayList<ListRecord> getAllSorted()
    {
        ArrayList<ListRecord> records = new ArrayList<ListRecord>();
        ArrayList<ListRecord> closedRecords = new ArrayList<ListRecord>();
        Query query = new Query("message_log").addSort("timeStamp", SortDirection.DESCENDING);
        PreparedQuery results = datastore.prepare(query);
        for (Entity entity : results.asIterable())
        {
            ListRecord temp = entityToLR(entity);
            records.add(temp);
        }
        return records;
    }
    public static ListRecord entityToLR(Entity entity) 
    {
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