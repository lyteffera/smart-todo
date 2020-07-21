package com.andrewrs.sps.utils;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.ArrayList;

public class Day {
    boolean[] timeSlots = new boolean[24 * 2 + 1];

    public Day()
    {
        timeSlots[timeSlots.length - 1] = true;
        //These two lines of code hard code blocking out times that are not between 9-5
        blockOutTimes(TimeRange.fromStartEnd(0,60*9,false));
        blockOutTimes(TimeRange.fromStartEnd(60*(12+5),60*24,false));
    }
    //Blocks out time in schedule according to the request, and events attendees, and schedules
    public void setAvailability(Collection<TimeRange> events)
    {
        for(TimeRange event:events)
        {
            blockOutTimes(event);
        }
    }
    private void blockOutTimes(TimeRange block)
    {
        int end = block.end();
        for(int i = block.start(); i < end && i < 50; i += 30)
        {
            timeSlots[i / 30] = true;
        }
    }
    public void blockOutEventTimes(Collection<TimeRange> events)
    {
        for(TimeRange event:events)
        {
            blockOutTimes(event);
        }
    }
    //using the known available timeSlots this returns a list of timeRamges that are free to fit the request
    public ArrayList<TimeRange> buildAvailabilities(int duration)
    {
        ArrayList<TimeRange> available = new ArrayList<TimeRange>();
        int start = 0,end = 0,trailer = 0;
        for(int i = 0; i < timeSlots.length; i++)
        {
            end = i*30;
            if(!timeSlots[trailer] && timeSlots[i])
            {
                if(end - start >=  duration)
                {
                    available.add(TimeRange.fromStartDuration(start, end - start));
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