package com.example.spese_myapplication;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;


import com.example.spese_myapplication.fragments.BilancioFragment;
import com.example.spese_myapplication.fragments.CalendarioFragment;
import com.example.spese_myapplication.fragments.SpeseFragment;

public class MyViewPageAdapter extends FragmentStateAdapter {
    public MyViewPageAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
        
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch(position){
            case 0:
                return new CalendarioFragment();
            case 1:
                return new BilancioFragment();
            case 2:
                return new SpeseFragment();
            default:
                return new BilancioFragment();
        }
    }

    @Override
    public int getItemCount() {
        return 3;
    }

}
