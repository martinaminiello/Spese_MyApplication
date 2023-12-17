package com.example.spese_myapplication.fragments;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import androidx.fragment.app.Fragment;


import com.example.spese_myapplication.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class CalendarioFragment extends Fragment {

    private Button btnDatePicker;
    private Calendar selectedDate;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_calendario, container, false);

        btnDatePicker = view.findViewById(R.id.datepicker);

        btnDatePicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog();
            }
        });

        // Initialize selectedDate with the current date
        selectedDate = Calendar.getInstance();

        // Update button text with the current date
        updateButtonText();

        return view;
    }

    private void showDatePickerDialog() {
        int year = selectedDate.get(Calendar.YEAR);
        int month = selectedDate.get(Calendar.MONTH);
        int day = selectedDate.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                requireContext(),
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int selectedYear, int selectedMonth, int selectedDay) {
                        // Update selectedDate with the picked date
                        selectedDate.set(selectedYear, selectedMonth, selectedDay);

                        // Update button text with the selected date
                        updateButtonText();
                    }
                },
                year, month, day);

        datePickerDialog.show();
    }

    private void updateButtonText() {
        // Format the date using the desired format
        SimpleDateFormat sdf = new SimpleDateFormat("EEEE d MMMM", Locale.getDefault());
        String buttonText = sdf.format(selectedDate.getTime());
        btnDatePicker.setText(buttonText);
    }
}