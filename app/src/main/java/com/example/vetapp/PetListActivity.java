package com.example.vetapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

public class PetListActivity extends AppCompatActivity {

    // UI Components
    RecyclerView recyclerView;        // List view to show all pets
    PetAdapter adapter;              // Adapter to bind pet data to RecyclerView
    AppDataBase db;                  // Reference to the app's local database
    List<Pet> petList;               // List of all pets from the database
    Button refreshButton;           // Button to manually refresh the list

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pet_list); // Set the layout for this activity

        // Initialize RecyclerView and set layout manager (vertical scrolling list)
        recyclerView = findViewById(R.id.petRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Initialize Refresh Button and handle its click event
        refreshButton = findViewById(R.id.button_refresh);

        refreshButton.setOnClickListener(v -> loadPets()); // Reload pet list when clicked

        // Get an instance of the local Room database
        db = AppDataBase.getDatabase(this);

        // Load the pets from the database into the list when activity starts
        loadPets();

        // Initialize and configure the FloatingActionButton to add a new pet
        FloatingActionButton fabAddPet = findViewById(R.id.fab_add);
        fabAddPet.setOnClickListener(v -> {
            // When the button is clicked, navigate to AddPet activity
            startActivity(new Intent(PetListActivity.this, AddPet.class));
        });
    }

    // Method to load all pets from the database and show them in the RecyclerView
    private void loadPets() {
        new Thread(() -> {
            // Fetch all pets in the background thread (Room requires this)
            petList = db.petDao().getAllPets();

            // Update UI with the fetched pet list on the main thread
            runOnUiThread(() -> {
                adapter = new PetAdapter(this, petList);  // Create adapter with pet list
                recyclerView.setAdapter(adapter);         // Set adapter to RecyclerView
            });
        }).start();
    }
}
