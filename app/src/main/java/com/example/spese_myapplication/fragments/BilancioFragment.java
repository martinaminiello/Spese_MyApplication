package com.example.spese_myapplication.fragments;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.example.spese_myapplication.R;
//import com.example.spese_myapplication.fragments.BudgetScheduler;
import com.example.spese_myapplication.fragments.CalendarViewModel;

import java.util.Locale;

public class BilancioFragment extends Fragment {
    private TextView budgetText;
    private CalendarViewModel viewModel;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_bilancio, container, false);
        viewModel = new ViewModelProvider(this).get(CalendarViewModel.class);
        budgetText = view.findViewById(R.id.textView2);

        // Update the TextView with the initial budget value
        updateBudgetText(60.00); // You can set this to the initial value you want



        return view;
    }

    // Method to update the budget TextView
    private void updateBudgetText(Double newBudget) {
        budgetText.setText(String.format(Locale.getDefault(), "Budget:   %.2f€", newBudget));
    }

    @Override
    public void onResume() {
        super.onResume();

        // Observe changes in the budget and update the TextView accordingly
        viewModel.getBudget().observe(getViewLifecycleOwner(), new Observer<Double>() {
            @Override
            public void onChanged(Double newBudget) {
                updateBudgetText(newBudget);
            }
        });
    }
}