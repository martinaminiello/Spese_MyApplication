package com.example.spese_myapplication.fragments;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateConverter {

    public static long convertDateToTimestamp(String dateString) {
        try {
            // Define the date format
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

            // Parse the date string to a Date object
            Date date = dateFormat.parse(dateString);

            // Get the time in milliseconds since January 1, 1970, 00:00:00 GMT (epoch)
            return date.getTime();
        } catch (ParseException e) {
            e.printStackTrace();
            // Handle the ParseException as needed
            return -1; // Return an invalid timestamp in case of an error
        }
    }
}
