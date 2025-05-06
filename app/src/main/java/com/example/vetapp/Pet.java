package com.example.vetapp;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "pets")
public class Pet {

    @PrimaryKey(autoGenerate = true)
    public int id;

    @ColumnInfo(name = "name")
    public String name;

    @ColumnInfo(name = "age")
    public int age;

    @ColumnInfo(name = "rendezvousDate")
    public String rendezvousDate;
    @ColumnInfo(name = "location") // Add this column for location
    public String location;

    @ColumnInfo(name = "photoUri")
    public String photoUri; // This field can hold the URI of the photo if you implement photo upload

    // Getters and Setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getRendezvousDate() {
        return rendezvousDate;
    }

    public void setRendezvousDate(String rendezvousDate) {
        this.rendezvousDate = rendezvousDate;
    }

    public String getPhotoUri() {
        return photoUri;
    }

    public void setPhotoUri(String photoUri) {
        this.photoUri = photoUri;
    }
}
