package com.andrewrs.sps.data;

import java.time.LocalDateTime;

public class ListRecord {
    private long timeStamp; 
    private int est_time;
    private String id,userId,message,due_date,completion_date,status;
    public ListRecord(String id, long timeStamp, String userId, String message, int est_time, 
                            String due_date, String completion_date, String status)
    {
      this.id = id;
      this.timeStamp = timeStamp; 
      this.message = message;
      this.userId = userId;
      this.est_time = est_time;
      this.due_date = due_date;
      this.completion_date = completion_date;
      this.status = status;
    }
        //the following functions are getter methods for properties of a ListRecord. Will 
    //add Javadoc documentation later. 
    public String getId(){
      return id; 
    }
    public long getTimeStamp(){
      return timeStamp; 
    }
    public String getMessage(){
      return message; 
    }
    public String getDueDate(){
      return due_date; 
    }
    public String getCompletionDate(){
      return completion_date; 
    }
    public String getStatus(){
      return status; 
    }
}