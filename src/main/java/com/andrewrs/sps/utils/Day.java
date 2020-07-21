
package com.andrewrs.sps.utils;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.ArrayList;
import com.andrewrs.sps.utils.TimeRange;

public class Day {
    //Number of slots the day will be brocken into
    private int slotCount;
    //24 hours * 2 = 48 half hours, can be broken down further if you like, multiply by 4 instead of 2 
    //to make it 15 minute blocks, or 12 to make it 5 minute blocks
    private boolean[] timeSlots;
    //incrementSize is a calculated value that is the amount of minutes of the day each block represents
    private int incrementSize;
    
    public Day()
    {
        resize(30);
        only9to5();
    }
    //this constructor takes the size of the smallest block of time allocatable in minutes, so if you
    //give it 5 then you can have an appointment from 2:15-2:55, of any multiple of 5 in minutes
    //give it 30, and you can only have appointments on the :00, and :30 of the hour, and if 5 minutes of a thirty minute
    //time block are used the entired block will be unsuable
    //5 is probably most reasonable, the default constructor initializes it to 30
    public Day(int timeBlockSize)
    {
        resize(timeBlockSize);
        only9to5();
    }
    public void only9to5()
    {    
        //These two lines of code hard code blocking out times that are not between 9-5
        blockOutTimes(TimeRange.fromStartEnd(0,60*9,false));
        blockOutTimes(TimeRange.fromStartEnd(60*(12+5),60*24,false));
    }
    public void setTimeBlockSize(int timeBlockSize)
    {
        resize(timeBlockSize);
        only9to5();
    }
    private void resize(int size)
    {
        incrementSize = size;
        slotCount = (int) (60./size*24);
        timeSlots = new boolean[slotCount + 1];
        //set the extra time slot to true to represent a fake meeting at the end of the day so the 
        //algorithm can run the same for every case
        timeSlots[timeSlots.length - 1] = true;
    }
        
    //Blocks out time in schedule according to the request, and events attendees, and schedules
    public void blockOutEventTimes(Collection<TimeRange> events)
    {
        for(TimeRange event:events)
        {
            blockOutTimes(event);
        }
    }
    //you can use this method to block out time ranges you don't want to schedule in, see the consturctor 
    //for how I use this to only schedule from 9-5
    public void blockOutTimes(TimeRange block)
    {
        int end = block.end();
        for(int i = block.start(); i*incrementSize < end; i++)
        {
            timeSlots[i] = true;
        }
    }
    //using the known available timeSlots this returns a list of timeRanges that are free to fit the request
    public ArrayList<TimeRange> buildAvailabilities(int duration)
    {
        ArrayList<TimeRange> available = new ArrayList<TimeRange>();
        //start represents the start time of the current time block being considered
        //end represents the end of the current time block
        int start = 0, end = 0, trailer = 0;
        for(int i = 0; i < timeSlots.length; i++)
        {
            //this sets the end to the current time ie if the increment size is 30
            //and i is 16 then the end is 16 * 30 = 480 minutes past the start of the day or 8 am
            end = i*incrementSize;
            if(!timeSlots[trailer] && timeSlots[i]) 
            {
                if(end - start >=  duration)
                {
                    available.add(TimeRange.fromStartEnd(start, end, false));
                }
            }
            else if(timeSlots[trailer] && !timeSlots[i]) 
            {
                start = end;
            }
            trailer = i;
        }
        return available;
    }
}