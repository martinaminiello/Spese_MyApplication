package com.example.spese_myapplication.fragments;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.spese_myapplication.RichiedenteAsilo;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class CalendarViewModel extends ViewModel {

    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private Double budget;
    DocumentReference documentRef = db.collection("Spese").document("0001");
    CollectionReference itemsCollection = db.collection("Subspese");

    DocumentReference newDocumentReference = itemsCollection.document();
    String documentId = newDocumentReference.getId();
    private final MutableLiveData<Double> budgetLiveData = new MutableLiveData<>();

    public LiveData<Double> getBudgetLiveData() {
        return budgetLiveData;
    }

    public void addItem(String id, String nome, String tipo, double prezzo, String selectedDate) {
        // Create a map with the data


        Map<String, Object> item = new HashMap<>();
        item.put("idProdotto",id);
        item.put("nome", nome);
        item.put("tipo", tipo);
        item.put("prezzo", prezzo);
        item.put("data",selectedDate);

       // updateBudget(prezzo);

        // Get a reference to the Firestore collection



        // Add the item to Firestore
        documentRef.collection("Subspese").add(item);

    }
    public void deleteItem(String parentId, String subcollectionName, String idProdotto) {
        // Get a reference to the parent document
        DocumentReference parentDocument = db.collection("Spese").document(parentId);

        // Get a reference to the subcollection
        CollectionReference subcollection = parentDocument.collection(subcollectionName);

        // Create a query to find the document with the specified idProdotto
        Query query = subcollection.whereEqualTo("idProdotto", idProdotto);

        // Execute the query
        query.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                for (QueryDocumentSnapshot document : task.getResult()) {
                    // Get the reference to the document within the subcollection and delete it
                    subcollection.document(document.getId()).delete()
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
    public void addUser() {
        // Create a map with the data

        Double budget=60.00;


        Map<String, Object> RichiedenteAsilo = new HashMap<>();

        RichiedenteAsilo.put("Budget", budget);


        // Get a reference to the Firestore collection
        CollectionReference itemsCollection = db.collection("RichiedenteAsilo");

        // Add the item to Firestore
        itemsCollection.document("0001").set(RichiedenteAsilo);

    }

    public void updateBudgetOnFirestore(String userId, double newBudget) {
        // Get a reference to the Firestore collection
        DocumentReference userDocument = db.collection("RichiedenteAsilo").document(userId);

        // Update the budget in Firestore
        Map<String, Object> updatedData = new HashMap<>();
        updatedData.put("Budget", newBudget);

        userDocument.update(updatedData)
                .addOnSuccessListener(aVoid -> {
                    // Budget successfully updated on Firestore
                    System.out.println("Budget successfully updated on Firestore!");

                    budgetLiveData.setValue(newBudget);
                })
                .addOnFailureListener(e -> {
                    // Handle errors here
                    System.out.println("Error updating budget on Firestore: " + e.getMessage());
                });
    }

}
