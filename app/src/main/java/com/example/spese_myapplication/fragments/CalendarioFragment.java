package com.example.spese_myapplication.fragments;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.example.spese_myapplication.R;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

public class CalendarioFragment extends Fragment {

    private CalendarView calendarView;
    private EditText editTextName;
    private EditText editTextType;
    private EditText editTextPrice;
    private Spinner spinnerType;
    private Button btnAddItem;
    private RecyclerView recyclerViewEvents;
    private EventAdapter eventAdapter;
    private Map<String, List<Event>> eventMap; // Map to store events for each date
    private String selectedDate; // Currently selected date

    private CalendarViewModel viewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_calendario, container, false);
        viewModel = new ViewModelProvider(this).get(CalendarViewModel.class);

        calendarView = view.findViewById(R.id.calendarView);
        editTextName = view.findViewById(R.id.editTextName);
        spinnerType = view.findViewById(R.id.spinnerType);
        editTextPrice = view.findViewById(R.id.editTextPrice);
        btnAddItem = view.findViewById(R.id.btnAddItem);
        recyclerViewEvents = view.findViewById(R.id.recyclerViewEvents);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                requireContext(), R.array.tipi, android.R.layout.simple_spinner_item
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerType.setAdapter(adapter);
        // Initialize event map
        eventMap = new HashMap<>();

        // Set up RecyclerView
        eventAdapter = new EventAdapter(new ArrayList<>(), viewModel, recyclerViewEvents);
        recyclerViewEvents.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerViewEvents.setAdapter(eventAdapter);

        eventAdapter.enableSwipeToDelete();

        // Set up CalendarView listener
        calendarView.setOnDateChangeListener((view1, year, month, dayOfMonth) -> {
            // Update events for the selected date
            selectedDate = String.format(Locale.getDefault(), "%04d-%02d-%02d", year, month + 1, dayOfMonth);
            updateEventsForDate(selectedDate);
        });

        // Set initial date
        selectedDate = getCurrentDate();
        updateEventsForDate(selectedDate);

        // Set up Add Item button click listener
        btnAddItem.setOnClickListener(v -> addItemAction());




        return view;
    }

    // Get the current date in "yyyy-MM-dd" format
    private String getCurrentDate() {
        // Get the current date from the CalendarView
        return Event.getCurrentDate();
    }

    // Update events for the selected date
    private void updateEventsForDate(String date) {
        List<Event> events = eventMap.get(date);
        if (events != null) {
            eventAdapter.setEvents(events);
        } else {
            eventAdapter.setEvents(new ArrayList<>());
        }
    }

    // Add Item button click handler
    private void addItemAction() {
        UUID uuid = UUID.randomUUID();
        String itemId= uuid.toString();
        String itemName = editTextName.getText().toString().trim();
        String itemType = spinnerType.getSelectedItem().toString().trim();
        String itemPriceString = editTextPrice.getText().toString().trim();

        if (!itemName.isEmpty()  && !itemPriceString.isEmpty()) {
            CharSequence itemPrice = itemPriceString;

            // Create a new event with the entered item details
            Event newItem = new Event(itemId,itemName, itemType, itemPrice);

            // Add the item to the map for the selected date
            List<Event> eventsForDate = eventMap.get(selectedDate);
            if (eventsForDate == null) {
                eventsForDate = new ArrayList<>();
                eventMap.put(selectedDate, eventsForDate);
            }
            eventsForDate.add(newItem);

            // Update the events for the selected date
            updateEventsForDate(selectedDate);
            String nome = editTextName.getText().toString();
            String tipo = spinnerType.getSelectedItem().toString();
            double prezzo = Double.parseDouble(editTextPrice.getText().toString());

            // Add the item using the ViewModel
            viewModel.addItem(itemId,nome, tipo, prezzo, selectedDate);

            // Clear the input fields
            editTextName.getText().clear();
            spinnerType.setSelection(0);
            editTextPrice.getText().clear();

            Toast.makeText(getContext(), "Item added successfully", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getContext(), "Please fill in all fields", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onResume() {
        super.onResume();

    }

}