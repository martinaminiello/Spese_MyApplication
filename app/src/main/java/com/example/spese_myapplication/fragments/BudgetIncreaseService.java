package com.example.spese_myapplication.fragments;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import androidx.core.app.JobIntentService;
import androidx.core.app.NotificationCompat;

import com.example.spese_myapplication.R;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

public class BudgetIncreaseService extends JobIntentService {
    static final int JOB_ID = 1000;

    public static void enqueueWork(Context context, Intent work) {
        enqueueWork(context, BudgetIncreaseService.class, JOB_ID, work);
    }



    @Override
    protected void onHandleWork(Intent intent) {

        // Fetch the current budget from Firestore
        Log.d("BudgetIncreaseService", "Service executed at " + System.currentTimeMillis());
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("RichiedenteAsilo").document("0001").get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        // Get the current budget
                        Double currentBudget = documentSnapshot.getDouble("Budget");

                        // Increase the budget by 60
                        Double newBudget = currentBudget + 60.00;

                        // Update the budget in Firestore
                        db.collection("RichiedenteAsilo").document("0001").update("Budget", newBudget);

                        Log.d("BudgetIncreaseService", "New budget: " + newBudget);
                    }
                })
                .addOnFailureListener(e -> {
                    // Handle errors, such as Firestore connection issues
                    Log.e("BudgetIncreaseService", "Firestore error: " + e.getMessage());
                });
    }


}
