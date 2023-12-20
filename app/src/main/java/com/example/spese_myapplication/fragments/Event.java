package com.example.spese_myapplication.fragments;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Event {

    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

    private String name;
    private String type;
    private CharSequence price;

    public Event(String name, String type, CharSequence price) {
        this.name = name;
        this.type=type;
        this.price=price;
    }

    public String getName() {
        return name;
    }
    public String getType() {
        return type;
    }
    public CharSequence getPrice() {
        return price;
    }
    public static String getCurrentDate() {
        // Get the current date
        long currentDateMillis = System.currentTimeMillis();
        return dateFormat.format(new Date(currentDateMillis));
    }

    // Add any additional fields and methods as needed
}