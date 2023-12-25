package com.example.spese_myapplication;


import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager2.widget.ViewPager2;

import android.os.Bundle;

//import com.example.spese_myapplication.fragments.BudgetScheduler;
import com.example.spese_myapplication.fragments.BudgetScheduler;
import com.example.spese_myapplication.fragments.CalendarViewModel;
import com.google.android.material.tabs.TabLayout;

public class MainActivity extends AppCompatActivity  {

    TabLayout tabLayout;
    ViewPager2 viewPager2;
    MyViewPageAdapter myViewPageAdapter;

    CalendarViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        viewModel = new ViewModelProvider(this).get(CalendarViewModel.class);



        tabLayout= findViewById(R.id.tab_layout);
        viewPager2=findViewById(R.id.view_pager);
        myViewPageAdapter= new MyViewPageAdapter(this);


        viewPager2.setAdapter(new MyViewPageAdapter(this));
        viewPager2.setOffscreenPageLimit(1);


        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager2.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });

        viewPager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                tabLayout.selectTab(tabLayout.getTabAt(position));
            }
        });



        BudgetScheduler.scheduleBudgetIncrease(getApplicationContext());

    }




}