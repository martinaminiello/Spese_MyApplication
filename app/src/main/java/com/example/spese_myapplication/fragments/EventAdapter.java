package com.example.spese_myapplication.fragments;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.spese_myapplication.R;

import java.util.List;

public class EventAdapter extends RecyclerView.Adapter<EventAdapter.ViewHolder> {

    private List<Event> events;

    public EventAdapter(List<Event> events) {
        this.events = events;
    }

    public void setEvents(List<Event> events) {
        this.events = events;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Event event = events.get(position);
        holder.textName.setText("Prodotto: " + event.getName());
        holder.textType.setText("Tipo: " + event.getType());
        holder.textPrice.setText("Prezzo: " + event.getPrice()+"€");
    }

    @Override
    public int getItemCount() {
        return events.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        private TextView textName;
        private TextView textType;
        private TextView textPrice;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
          textName=itemView.findViewById(R.id.tvNome);
            textType=itemView.findViewById(R.id.tvTipo);
            textPrice=itemView.findViewById(R.id.tvPrezzo);
        }


    }
}