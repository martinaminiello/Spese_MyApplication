package com.example.spese_myapplication.fragments;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.graphics.Typeface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.os.Handler;
import android.os.Looper;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.RelativeSizeSpan;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.example.spese_myapplication.R;
//import com.example.spese_myapplication.fragments.BudgetScheduler;
import com.example.spese_myapplication.fragments.CalendarViewModel;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class BilancioFragment extends Fragment {

    private CalendarViewModel viewModel;
    private FirebaseFirestore firestore = FirebaseFirestore.getInstance();
    private DocumentReference documentReference = firestore.collection("RichiedenteAsilo").document("0001");

    private TextView budgetTextView;
    private double currentBudget = 60.0; // Initial budget

    private Handler handler = new Handler(Looper.getMainLooper());

    private long updateInterval = 30 * 24 * 60 * 60 * 1000L; // 30 days in milliseconds
    //  FOR TESTING  private long updateInterval = 15 * 60 * 1000L; // 15 minutes in

    CardView cardView1;
    TextView textSett;
    CardView cardView2;
    TextView textMese;

    ProgressBar loadingIndicator;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_bilancio, container, false);
        viewModel = new ViewModelProvider(requireActivity().getViewModelStore(), ViewModelProvider.AndroidViewModelFactory.getInstance(requireActivity().getApplication())).get(CalendarViewModel.class);
        budgetTextView = view.findViewById(R.id.textView2);
        cardView1 = view.findViewById(R.id.CardView1);
        textSett = view.findViewById(R.id.textSett);
        cardView2 = view.findViewById(R.id.CardView2);
        textMese = view.findViewById(R.id.textMese);
        loadingIndicator = view.findViewById(R.id.progressBar);


        showLoadingIndicator();



        // Load initial budget from Firestore
        loadInitialBudget();



        // Schedule periodic budget updates
        scheduleBudgetUpdates();

        QuerySett();
        QueryMese();

        viewModel.getUpdatedBudgetLiveData().observe(getActivity(), new Observer<Double>() {
            @Override
            public void onChanged(Double newBudget) {

                updateBudgetTextView(newBudget);

            }
        });



        return view;
    }






    private void loadInitialBudget() {
        documentReference.get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists()) {
                            Double budget = documentSnapshot.getDouble("Budget");
                            currentBudget = (budget != null) ? budget : 60.00;
                            updateBudgetTextView(currentBudget);
                            hideLoadingIndicator();
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Handle the failure to load the initial budget
                    }
                });
    }

    private void scheduleBudgetUpdates() {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                // Update budget every 30 days
                updateBudget(60.0);
                handler.postDelayed(this, updateInterval);
            }
        }, updateInterval);
    }


    private void updateBudget(double amount) {
        currentBudget += amount;

        // Update the budget in Firestore
        documentReference.update("Budget", currentBudget)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        updateBudgetTextView(currentBudget);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Handle the failure to update the budget
                    }
                });
    }

    private void updateBudgetTextView(Double currentBudget) {
        SpannableString spannableString = new SpannableString(String.format(Locale.getDefault(), "Budget:\n\n        %.2f€", currentBudget));

// Set a larger text size for the currentBudget part
        spannableString.setSpan(new RelativeSizeSpan(1.5f), 8, spannableString.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

// Apply the SpannableString to the TextView
        budgetTextView.setText(spannableString);



    }
    public void QuerySett(){
// Define the start and end dates for the current week
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_WEEK, calendar.getFirstDayOfWeek()); // Set to the first day of the week
        Date startDate = calendar.getTime(); // Start date is the first day of the week

        calendar.add(Calendar.DAY_OF_WEEK, 6); // Move to the last day of the week
        Date endDate = calendar.getTime(); // End date is the last day of the week

// Convert start and end dates to timestamps
        long startTimeStamp = startDate.getTime();
        long endTimeStamp = endDate.getTime();

// Reference to your Firestore collection
        DocumentReference parentDocument = firestore.collection("Spese").document("0001");

        // Get a reference to the subcollection
        CollectionReference subcollection = parentDocument.collection("Subspese");

// Perform a query to get the items for the current week
        subcollection.whereGreaterThanOrEqualTo("data", startTimeStamp)
                .whereLessThanOrEqualTo("data", endTimeStamp)
                .addSnapshotListener((queryDocumentSnapshots, e) -> {
                    if (e != null) {
                        // Handle the error
                        Log.e("Fetching", "Error getting prices for the current week", e);
                        return;
                    }

                    Double totSett=0.00;

                    // Iterate through the documents and extract prices
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        Double prezzo = document.getDouble("prezzo");
                        if (prezzo != null) {
                            totSett += prezzo;
                            Log.d("TotSett", "Getting prices for the current week: " + totSett);
                            System.out.println("Getting prices for the current week: " + totSett);
                        }
                    }

                    displayTotSett(totSett);
                    // You can further process or display this data as needed
                });
    }

    private void displayTotSett(Double totSett) {

        String text = String.format(Locale.getDefault(), "Spesa settimanale: %.2f€", totSett);

        SpannableString spannableString = new SpannableString(text);
        int startIndex = text.indexOf(String.format(Locale.getDefault(), "%.2f", totSett));
        int endIndex = startIndex + String.format(Locale.getDefault(), "%.2f", totSett).length();

        // Apply bold style to the totMese value
        spannableString.setSpan(new StyleSpan(Typeface.BOLD), startIndex, endIndex, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        textSett.setText(spannableString);


    }
    public void QueryMese() {
        // Define the start and end dates for the current month
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_MONTH, 1); // Set to the first day of the month
        Date startDate = calendar.getTime(); // Start date is the first day of the month

        calendar.add(Calendar.MONTH, 1); // Move to the first day of the next month
        calendar.add(Calendar.DAY_OF_MONTH, -1); // Move to the last day of the current month
        Date endDate = calendar.getTime(); // End date is the last day of the month

        // Convert start and end dates to timestamps
        long startTimeStamp = startDate.getTime();
        long endTimeStamp = endDate.getTime();

        // Reference to your Firestore collection
        DocumentReference parentDocument = firestore.collection("Spese").document("0001");

        // Get a reference to the subcollection
        CollectionReference subcollection = parentDocument.collection("Subspese");

        // Perform a query to get the items for the current month
        subcollection.whereGreaterThanOrEqualTo("data", startTimeStamp)
                .whereLessThanOrEqualTo("data", endTimeStamp)
                .addSnapshotListener((queryDocumentSnapshots, e) -> {
                    if (e != null) {
                        // Handle the error
                        Log.e("Fetching", "Error getting prices for the current month", e);
                        return;
                    }

                    Double totMese = 0.00;

                    // Iterate through the documents and extract prices
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        Double prezzo = document.getDouble("prezzo");
                        if (prezzo != null) {
                            totMese += prezzo;
                            Log.d("TotSett", "Getting prices for the current month: " + totMese);
                            System.out.println("Getting prices for the current month: " + totMese);
                        }
                    }

                    displayTotMese(totMese);
                    // You can further process or display this data as needed
                });
    }

    private void displayTotMese(Double totMese) {
        String text = String.format(Locale.getDefault(), "Spesa mensile: %.2f€", totMese);

        SpannableString spannableString = new SpannableString(text);
        int startIndex = text.indexOf(String.format(Locale.getDefault(), "%.2f", totMese));
        int endIndex = startIndex + String.format(Locale.getDefault(), "%.2f", totMese).length();

        // Apply bold style to the totMese value
        spannableString.setSpan(new StyleSpan(Typeface.BOLD), startIndex, endIndex, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        textMese.setText(spannableString);
    }

    private void showLoadingIndicator() {
        // Show your loading indicator
        loadingIndicator.setVisibility(View.VISIBLE);
    }

    private void hideLoadingIndicator() {
        // Hide your loading indicator
        loadingIndicator.setVisibility(View.GONE);
    }

}