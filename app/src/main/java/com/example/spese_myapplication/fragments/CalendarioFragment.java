package com.example.spese_myapplication.fragments;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.example.spese_myapplication.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class CalendarioFragment extends Fragment {

    private Button btnDatePicker;
    private Calendar selectedDate;
    private List<Item> itemList = new ArrayList<>();
    private MyAdapter adapter;
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
        RecyclerView recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));

        adapter = new MyAdapter(itemList);
        recyclerView.setAdapter(adapter);

        FloatingActionButton btnAddItem = view.findViewById(R.id.btnAddItem);
        EditText etNome = view.findViewById(R.id.editNome);
        EditText etTipo = view.findViewById(R.id.editTipo);
        EditText etPrezzo = view.findViewById(R.id.editPrezzo);

        btnAddItem.setOnClickListener(view1 -> {
            // Get user input
            String nome = etNome.getText().toString();
            String tipo = etTipo.getText().toString();
            int prezzo = Integer.parseInt(etPrezzo.getText().toString());


            // Create a new item with the selected date
            Item newItem = new Item(nome, tipo, prezzo, selectedDate);

            // Add the new item to the list
            itemList.add(newItem);

            // Clear the input fields
            etNome.getText().clear();
            etTipo.getText().clear();
            etPrezzo.getText().clear();

            // Filter items based on the selected date
            List<Item> itemsForSelectedDate = new ArrayList<>();
            for (Item existingItem : itemList) {
                if (existingItem.hasSameDate(selectedDate)) {
                    itemsForSelectedDate.add(existingItem);
                }
            }

            // Update the adapter with the filtered list
            adapter.setItems(itemsForSelectedDate);

        });

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