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
import com.example.spese_myapplication.RichiedenteAsilo;

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

// Observe the LiveData for budget changes
        viewModel.getBudgetLiveData().observe(getViewLifecycleOwner(), new Observer<Double>() {
            @Override
            public void onChanged(Double newBudget) {
                // Update the TextView with the new budget
                budgetText.setText(String.format(Locale.getDefault(), "Budget:    %.1f€", newBudget));
            }
        });

        //IN ATTESA DI IMPLEMENTAZIONE VISTA RICHIEDENTE ASILO
        RichiedenteAsilo userProva=new RichiedenteAsilo("0001");
        double budget = userProva.getBudget();
        viewModel.addUser();
        viewModel.updateBudgetOnFirestore( "0001", budget);

        budgetText.setText("Budget:    " + String.valueOf(budget)+"€");




        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

    }


}