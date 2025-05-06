package com.example.vetapp;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "users")
public class User {
    @PrimaryKey(autoGenerate = true)
    public int id;
    @ColumnInfo(name = "username")
    public String username;
    @ColumnInfo(name = "password")
    public String password;
    // Default constructor required by Room
    public User() {}

    // Constructor with parameters to create a new user
    public User(String username, String password) {
        this.username = username;
        this.password = password;
    }

    // Getters and Setters (Optional, not required for Room)
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
