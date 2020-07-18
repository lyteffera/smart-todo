package com.andrewrs.sps.data;

import java.time.LocalDateTime;

public class ListRecord {
    private long timeStamp; 
    private double est_time;
    private String id,dependency,userId,message,due_date,completion_date,status;
    public ListRecord(String id, String dependency, long timeStamp, String userId, String message, double est_time, 
                            String due_date, String completion_date, String status)
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
    }
}