package com.example.spese_myapplication.fragments;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import static com.example.spese_myapplication.fragments.DateConverter.convertDateToTimestamp;

import android.util.Log;

import androidx.constraintlayout.motion.widget.KeyCycle;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.example.spese_myapplication.RichiedenteAsilo;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.HashMap;

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
    private MutableLiveData<Map<String, Integer>> tipoCountsLiveData = new MutableLiveData<>();

    public LiveData<Map<String, Integer>> getTipoCountsLiveData() {
        return tipoCountsLiveData;
    }


    public void addItem(String id, String nome, String tipo, double prezzo, String selectedDate){
        // Create a map with the data

        long timestamp = convertDateToTimestamp(selectedDate);
        Map<String, Object> item = new HashMap<>();
        item.put("idProdotto",id);
        item.put("nome", nome);
        item.put("tipo", tipo);
        item.put("prezzo", prezzo);
        item.put("data",timestamp);



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
    public void fillChart(){
        CollectionReference collectionReference = FirebaseFirestore.getInstance()
                .collection("Spese")
                .document("0001")
                .collection("Subspese");

        // Add a real-time listener
        collectionReference.addSnapshotListener((queryDocumentSnapshots, e) -> {
            if (e != null) {
                // Handle errors, such as logging or displaying an error message
                return;
            }

            if (queryDocumentSnapshots != null) {
                // Count occurrences of each Tipo
                Map<String, Integer> tipoCountMap = new HashMap<>();

                for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                    String tipo = document.getString("Tipo");

                    if (tipo != null) {
                        // Increment count for the Tipo
                        tipoCountMap.put(tipo, tipoCountMap.getOrDefault(tipo, 0) + 1);
                    }
                }

                // Calculate percentages
                int totalItems = queryDocumentSnapshots.size();
                Map<String, Double> tipoPercentageMap = new HashMap<>();

                for (Map.Entry<String, Integer> entry : tipoCountMap.entrySet()) {
                    String tipo = entry.getKey();
                    int count = entry.getValue();
                    double percentage = (count / (double) totalItems) * 100.0;
                    tipoPercentageMap.put(tipo, percentage);
                }


            }
        });
    }

    public void fetchTipoCounts() {
        db.collection("Spese").
                document("0001").collection("Subspese")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(QuerySnapshot queryDocumentSnapshots, FirebaseFirestoreException e) {
                if (e != null) {
                    // Handle error
                    return;
                }

                Map<String, Integer> tipoCounts = new HashMap<>();

                for (DocumentSnapshot document : queryDocumentSnapshots.getDocuments()) {
                    // Assuming "Tipo" is a field in your Firestore documents
                    String tipo = document.getString("Tipo");

                    // Update Tipo counts
                    if (tipo != null) {
                        tipoCounts.put(tipo, tipoCounts.getOrDefault(tipo, 0) + 1);
                    }
                }

                // Update LiveData with Tipo counts
                tipoCountsLiveData.postValue(tipoCounts);
            }
        });
    }
}

