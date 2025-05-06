package com.example.vetapp;

import android.content.Context;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

// Define the AppDatabase class as a Room Database, including both User and Pet entities
@Database(entities = {User.class, Pet.class}, version = 2) // Version 2 of the database
public abstract class AppDataBase extends RoomDatabase {

    // Abstract methods to access User and Pet DAOs
    public abstract UserDao userDao(); // Access to operations on User data
    public abstract PetDao petDao();   // Access to operations on Pet data

    // Singleton instance of the database
    private static volatile AppDataBase INSTANCE;

    // Method to get the singleton instance of the database
    public static AppDataBase getDatabase(final Context context) {
        // Check if INSTANCE is null (database not created yet)
        if (INSTANCE == null) {
            // Synchronize to make the database initialization thread-safe
            synchronized (AppDataBase.class) {
                // Double-check if INSTANCE is still null to avoid creating the database multiple times
                if (INSTANCE == null) {
                    // Build the database using Room's database builder
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                                    AppDataBase.class, "vet_database") // Set the database name
                            .fallbackToDestructiveMigration() // Allows schema changes by destroying and recreating the database
                            .build(); // Build the database
                }
            }
        }
        return INSTANCE; // Return the singleton instance
    }
}
