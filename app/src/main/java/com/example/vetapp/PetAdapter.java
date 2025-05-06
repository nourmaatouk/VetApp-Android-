package com.example.vetapp;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

// Adapter class to bind the list of Pet objects to RecyclerView items
public class PetAdapter extends RecyclerView.Adapter<PetAdapter.PetViewHolder> {

    private List<Pet> petList;      // List containing all the pets to be displayed
    private Context context;        // Context of the calling activity

    // Constructor to initialize adapter with context and data list
    public PetAdapter(Context context, List<Pet> petList) {
        this.context = context;
        this.petList = petList;
    }

    // Called when RecyclerView needs a new ViewHolder (item layout inflated here)
    @NonNull
    @Override
    public PetViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate the layout for individual pet item
        View view = LayoutInflater.from(context).inflate(R.layout.pet_item, parent, false);
        return new PetViewHolder(view);
    }

    // Binds the data of a pet to the ViewHolder (UI element) at a given position
    @Override
    public void onBindViewHolder(@NonNull PetViewHolder holder, int position) {
        Pet pet = petList.get(position); // Get the pet at the current position

        // Set pet's name and age in the TextViews
        holder.nameText.setText(pet.name);
        holder.ageText.setText("Age: " + pet.age);

        // If a photo URI is stored, load and display the pet's image
        if (pet.photoUri != null) {
            holder.photoView.setImageURI(Uri.parse(pet.photoUri));
        }

        // Set a click listener on the entire item to open the pet profile
        holder.itemView.setOnClickListener(v -> {
            // Create intent to start PetProfileActivity and send pet ID with it
            Intent intent = new Intent(context, PetProfileActivity.class);
            intent.putExtra("petId", pet.id); // Pass pet ID to the profile screen
            context.startActivity(intent);    // Launch profile activity
        });
    }

    // Returns the total number of pet items in the list
    @Override
    public int getItemCount() {
        return petList.size();
    }

    // ViewHolder class to represent the views for each item in the list
    static class PetViewHolder extends RecyclerView.ViewHolder {
        TextView nameText, ageText;   // Text fields to display pet name and age
        ImageView photoView;          // ImageView to show pet photo

        public PetViewHolder(@NonNull View itemView) {
            super(itemView);
            // Link UI components from the item layout
            nameText = itemView.findViewById(R.id.petName);
            ageText = itemView.findViewById(R.id.petAge);
            photoView = itemView.findViewById(R.id.petPhoto);
        }
    }
}
