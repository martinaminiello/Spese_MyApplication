package com.example.spese_myapplication.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.os.Handler;
import android.os.Looper;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.RelativeSizeSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
    //  FOR TESTING  private long updateInterval = 15 * 60 * 1000L; // 15 minutes in milliseconds

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_bilancio, container, false);
        viewModel = new ViewModelProvider(requireActivity().getViewModelStore(), ViewModelProvider.AndroidViewModelFactory.getInstance(requireActivity().getApplication())).get(CalendarViewModel.class);
        budgetTextView = view.findViewById(R.id.textView2);





        // Load initial budget from Firestore
        loadInitialBudget();

        // Schedule periodic budget updates
        scheduleBudgetUpdates();

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
                // Update budget every 15 minutes
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




}