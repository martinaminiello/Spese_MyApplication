package com.example.spese_myapplication.fragments;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import java.util.Calendar;

public class BudgetScheduler {


    public static void scheduleBudgetIncrease(Context context) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, BudgetIncreaseService.class);
        PendingIntent pendingIntent = PendingIntent.getService(context, 0, intent, PendingIntent.FLAG_IMMUTABLE);

        // Schedule the job to run every month
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MONTH, 1);

      /*  alarmManager.setInexactRepeating(
                AlarmManager.RTC_WAKEUP,
                calendar.getTimeInMillis(),
                AlarmManager.INTERVAL_DAY * 30, // Every 30 days (adjust as needed)
                pendingIntent*/

                // FOR TESTING
                alarmManager.setInexactRepeating(
                        AlarmManager.RTC_WAKEUP,
                        System.currentTimeMillis(),
                        AlarmManager.INTERVAL_FIFTEEN_MINUTES, // Every 15 minutes for testing (adjust as needed)
                        pendingIntent
        );

    }
}
