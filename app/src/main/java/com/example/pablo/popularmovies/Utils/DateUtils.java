package com.example.pablo.popularmovies.Utils;

import java.util.Calendar;
import java.util.Date;

public class DateUtils {

    /**
     * Number of seconds in one day(24 hours)
     */
    public static final long HOURS_24_IN_SECONDS = 86400L;
    /**
     * Number of miliseconds in one day (24 hours)
     */
    public static final long HOURS_24_IN_MILISECONDS = 86400000L;


    public static Date getCurrentDate(){
        return new Date(getCurrentDateInMiliseconds());
    }

    public static long getCurrentDateInMiliseconds(){
        return System.currentTimeMillis();
    }

    /**
     * Helper method to check if a Date is older than 24 hours.
     *
     * @param dateToCheck   The date to check.
     * @return              true if the date is older than 24 hours.
     *                      false if not.
     */
    public static boolean isDateOlderThan24Hours(Date dateToCheck){

        long currentDateInMiliseconds = getCurrentDateInMiliseconds();
        long dateToCheckInMiliseconds = dateToCheck.getTime();

        long elapsedTime = currentDateInMiliseconds - dateToCheckInMiliseconds;
        //  if the elapsed time is older than 24 hours, return true;
        return elapsedTime > HOURS_24_IN_MILISECONDS;


    }

}
