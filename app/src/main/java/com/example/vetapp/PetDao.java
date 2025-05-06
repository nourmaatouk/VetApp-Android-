package com.example.vetapp;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;


@Dao
public interface PetDao {

    @Insert
    void insert(Pet pet);

    @Update
    void update(Pet pet);

    @Delete
    void delete(Pet pet);

    // Query to get all pets
    @Query("SELECT * FROM pets")  // Ensure table name matches entity table name (pet in lowercase)
    List<Pet> getAllPets();

    // Query to get a pet by ID
    @Query("SELECT * FROM pets WHERE id = :petId LIMIT 1")
    Pet getPetById(int petId);  // Returns a single pet by its ID
}

