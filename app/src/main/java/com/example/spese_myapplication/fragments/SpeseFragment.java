package com.example.spese_myapplication.fragments;

import android.graphics.Color;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.spese_myapplication.R;
import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class SpeseFragment extends Fragment {
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    TextView textView;
    private PieChart pieChart;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_spese, container, false);

        db = FirebaseFirestore.getInstance();
        textView=view.findViewById(R.id.textView1);
        pieChart = view.findViewById(R.id.pieChart);

        pieChart.getDescription().setEnabled(false);

        // Call method to fetch initial data and set up the listener
        fetchDataAndSetupListener();

        return view;
    }

    private void fetchDataAndSetupListener() {
        db.collection("Spese").document("0001").collection("Subspese")
                .addSnapshotListener((value, error) -> {
                    if (error != null) {
                        // Handle the error
                        Log.e("FirestoreListener", "Error getting data", error);
                        return;
                    }

                    if (value != null) {
                        List<DocumentSnapshot> documents = value.getDocuments();
                        // Now you have the updated documents, proceed to calculate percentages and display the pie chart.
                        calculatePercentagesAndDisplayChart(documents);
                    }
                });
    }

    private void calculatePercentagesAndDisplayChart(List<DocumentSnapshot> documents) {
        Map<String, Float> tipoTotalPrice = new HashMap<>();
        int currentMonth = getCurrentMonth();

        // Calculate total price for each type only for the current month
        for (DocumentSnapshot document : documents) {
            // Retrieve the "data" field as a long
            long timestampMillis = document.getLong("data");

            // Convert the long to a Date object
            Date date = new Date(timestampMillis);

            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);

            if (calendar.get(Calendar.MONTH) == currentMonth) {
                String tipo = document.getString("tipo");
                float price = document.getDouble("prezzo").floatValue(); // Assuming you have a field named "prezzo" for the price

                if (tipoTotalPrice.containsKey(tipo)) {
                    tipoTotalPrice.put(tipo, tipoTotalPrice.get(tipo) + price);
                } else {
                    tipoTotalPrice.put(tipo, price);
                }
            }
        }

        List<PieEntry> entries = new ArrayList<>();
        float totalPrices = 0f;

        // Calculate total prices
        for (Map.Entry<String, Float> entry : tipoTotalPrice.entrySet()) {
            totalPrices += entry.getValue();
        }

        // Create entries with percentages based on total prices
        for (Map.Entry<String, Float> entry : tipoTotalPrice.entrySet()) {
            String tipo = entry.getKey();
            float totalPrice = entry.getValue();
            float percentage = (totalPrice / totalPrices) * 100;

            // Set the actual value associated with each slice
            entries.add(new PieEntry(percentage, tipo + "\n€" + String.format("%.2f", totalPrice)));
        }

        PieDataSet dataSet = new PieDataSet(entries, "");
        dataSet.setColors(ColorTemplate.COLORFUL_COLORS);
        dataSet.setValueTextSize(16f);

        PieData pieData = new PieData(dataSet);
        pieChart.setData(pieData);

        // Further customization if needed
        pieChart.setCenterText("Spese");
        pieChart.setHoleRadius(25f);
        pieChart.setTransparentCircleRadius(30f);
        pieChart.animateY(1000, Easing.EaseInOutCubic);
        pieChart.invalidate();
        pieChart.setCenterTextSize(20f);
        Legend legend = pieChart.getLegend();

// Set legend orientation to vertical
        legend.setOrientation(Legend.LegendOrientation.VERTICAL);
        legend.setXEntrySpace(10f);
        legend.setYOffset(60f);

    }

    // Get the current month
    private int getCurrentMonth() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        return calendar.get(Calendar.MONTH);
    }
}