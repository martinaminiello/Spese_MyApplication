package com.example.spese_myapplication.fragments;



import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModel;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

public class SwipeToDeleteCallback extends ItemTouchHelper.SimpleCallback {
    private EventAdapter adapter;
    private CalendarViewModel viewModel;

    public SwipeToDeleteCallback(EventAdapter adapter, CalendarViewModel viewModel) {
        super(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT);
        this.adapter = adapter;
        this.viewModel = viewModel;

    }

    @Override
    public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
        return false;
    }

    @Override
    public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
        int position = viewHolder.getAdapterPosition();
        String idProdotto = String.valueOf(adapter.getItemId(position)); // Assuming you have a method to get the item ID
        adapter.deleteItem(position);
        viewModel.deleteItem("0001","Subspese",idProdotto); // Call the deleteItem method in your ViewModel
    }

}
