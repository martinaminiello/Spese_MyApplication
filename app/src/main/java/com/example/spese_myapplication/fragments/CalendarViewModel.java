package com.example.spese_myapplication.fragments;

import androidx.constraintlayout.motion.widget.KeyCycle;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.spese_myapplication.RichiedenteAsilo;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class CalendarViewModel extends ViewModel {

    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private Double budget;
    DocumentReference documentRef = db.collection("Spese").document("0001");
    DocumentReference documentRefBudget = db.collection("RichiedenteAsilo").document("0001");

    CollectionReference itemsCollection = db.collection("Subspese");

    DocumentReference newDocumentReference = itemsCollection.document();
    String documentId = newDocumentReference.getId();
    private MutableLiveData<Double> updatedBudgetLiveData = new MutableLiveData<>();

    public LiveData<Double> getUpdatedBudgetLiveData() {
        return updatedBudgetLiveData;
    }


    public void addItem(String id, String nome, String tipo, double prezzo, String selectedDate) {
        // Create a map with the data


        Map<String, Object> item = new HashMap<>();
        item.put("idProdotto",id);
        item.put("nome", nome);
        item.put("tipo", tipo);
        item.put("prezzo", prezzo);
        item.put("data",selectedDate);



        documentRef.collection("Subspese").add(item)
                .addOnSuccessListener(documentReference -> {
                    // Item added successfully, now update the budget

                    // Retrieve the current budget from Firestore
                    documentRefBudget.get()
                            .addOnSuccessListener(documentSnapshot -> {
                                if (documentSnapshot.exists()) {
                                    Double currentBudget = documentSnapshot.getDouble("Budget");

                                    // Check if currentBudget is not null before proceeding
                                    if (currentBudget != null) {
                                        // Subtract 'prezzo' from the current budget
                                        double newBudget = currentBudget - prezzo;

                                        // Update the budget in Firestore
                                        documentRefBudget.update("Budget", newBudget)
                                                .addOnSuccessListener(aVoid -> {
                                                    updatedBudgetLiveData.postValue(newBudget);
                                                })
                                                .addOnFailureListener(e -> {
                                                    // Handle the failure to update the budget
                                                    // You might want to consider rolling back the item addition
                                                    // since the budget update failed
                                                });
                                    } else {
                                        // Handle the case where 'Budget' is null in Firestore
                                        // You might want to consider rolling back the item addition
                                    }
                                } else {
                                    // Handle the case where the document doesn't exist
                                    // You might want to consider rolling back the item addition
                                }
                            })
                            .addOnFailureListener(e -> {
                                // Handle the failure to retrieve the current budget
                                // You might want to consider rolling back the item addition
                            });

                })
                .addOnFailureListener(e -> {
                    // Handle the failure to add the item to Firestore
                });

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
                    // Get the price of the deleted item
                    Double deletedItemPrice = document.getDouble("prezzo");

                    // Update the budget first
                    updateBudget(deletedItemPrice);

                    // Now, delete the item from the subcollection
                    subcollection.document(document.getId()).delete()
                            .addOnSuccessListener(aVoid -> {
                                System.out.println("DocumentSnapshot successfully deleted!");
                            })
                            .addOnFailureListener(e -> {
                                // Handle errors here
                                System.out.println("Error deleting item: " + e.getMessage());
                            });
                }
            } else {
                // Handle errors in fetching the item to be deleted
                System.out.println("Error getting item: " + task.getException());
            }
        });
    }

    private void updateBudget(double deletedItemPrice) {
        // Fetch the current budget from Firestore
        documentRefBudget.get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        Double currentBudget = documentSnapshot.getDouble("Budget");

                        // Check if currentBudget is not null before proceeding
                        if (currentBudget != null) {
                            // Subtract 'prezzo' from the current budget
                            double newBudget = currentBudget + deletedItemPrice;

                            // Update the budget in Firestore
                            documentRefBudget.update("Budget", newBudget)
                                    .addOnSuccessListener(aVoid -> {
                                        updatedBudgetLiveData.postValue(newBudget);
                                    })
                                    .addOnFailureListener(e -> {
                                        // Handle the failure to update the budget
                                        // You might want to consider rolling back the item addition
                                        // since the budget update failed
                                    });
                        } else {
                            // Handle the case where 'Budget' is null in Firestore
                            // You might want to consider rolling back the item addition
                        }
                    } else {
                        // Handle the case where the document doesn't exist
                        // You might want to consider rolling back the item addition
                    }
                })
                .addOnFailureListener(e -> {
                    // Handle the failure to retrieve the current budget
                    // You might want to consider rolling back the item addition
                });
}   //FOR TESTING
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
}
