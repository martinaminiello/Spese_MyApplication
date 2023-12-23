package com.example.spese_myapplication.fragments;

import androidx.lifecycle.ViewModel;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class CalendarViewModel extends ViewModel {

    private final FirebaseFirestore db = FirebaseFirestore.getInstance();

    public void addItem(String id, String nome, String tipo, double prezzo, String selectedDate) {
        // Create a map with the data


        Map<String, Object> item = new HashMap<>();
        item.put("idProdotto",id);
        item.put("nome", nome);
        item.put("tipo", tipo);
        item.put("prezzo", prezzo);
        item.put("data",selectedDate);

        // Get a reference to the Firestore collection
        CollectionReference itemsCollection = db.collection("items");

        // Add the item to Firestore
        itemsCollection.add(item);
    }
    public void deleteItem(String idProdotto) {
        // Get a reference to the Firestore collection
        CollectionReference itemsCollection = db.collection("items");

        // Create a query to find the document with the specified idProdotto
        itemsCollection.whereEqualTo("idProdotto", idProdotto)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // Iterate through the result set
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            // Get the reference to the document and delete it
                            itemsCollection.document(document.getId()).delete()
                                    .addOnSuccessListener(aVoid -> {
                                        // Document successfully deleted
                                        System.out.println("DocumentSnapshot successfully deleted!");
                                    })
                                    .addOnFailureListener(e -> {
                                        // Handle errors here
                                        System.out.println("Error deleting item: " + e.getMessage());
                                    });
                        }
                    } else {
                        // Handle errors here
                        System.out.println("Error getting item: " + task.getException());
                    }
                });
    }
}
