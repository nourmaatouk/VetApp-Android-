package com.example.vetapp;

import android.Manifest;
import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Calendar;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AddPet extends AppCompatActivity {

    // Declare views for the pet name, age, rendezvous, save button, and photo
    EditText etPetName, etPetAge, etPetRendezvous;
    Button btnSavePet;
    ImageView imageViewPetPhoto;

    // Database instance and PetDao to interact with the database
    AppDataBase db;
    PetDao petDao;

    // Calendar instance to store the selected date and time for the rendezvous
    Calendar selectedDateTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_pet);

        // Initialize the views from XML layout
        etPetName = findViewById(R.id.editPetName);
        etPetAge = findViewById(R.id.editPetAge);
        etPetRendezvous = findViewById(R.id.editPetRendezvous);
        btnSavePet = findViewById(R.id.btnSavePet);
        imageViewPetPhoto = findViewById(R.id.imageViewPetPhoto);

        // Initialize the database and DAO
        db = AppDataBase.getDatabase(this);
        petDao = db.petDao();

        // Initialize the calendar for date and time selection
        selectedDateTime = Calendar.getInstance();

        // Check if the app has permission to post notifications for Android 13 and above
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                // Request permission if not granted
                requestPermissions(new String[]{Manifest.permission.POST_NOTIFICATIONS}, 1001);
            }
        }

        // Set an on-click listener to show a date and time picker when user selects the rendezvous field
        etPetRendezvous.setOnClickListener(v -> showDateTimePicker());

        // Set an on-click listener for saving the pet details
        btnSavePet.setOnClickListener(v -> {
            // Retrieve pet details from user input
            String petName = etPetName.getText().toString().trim();
            String petAge = etPetAge.getText().toString().trim();
            String petRendezvous = etPetRendezvous.getText().toString().trim();

            // Check if any field is empty
            if (petName.isEmpty() || petAge.isEmpty() || petRendezvous.isEmpty()) {
                // Show an error message if any field is empty
                Toast.makeText(AddPet.this, "Please fill in all fields.", Toast.LENGTH_SHORT).show();
            } else {
                // Create a new Pet object and assign values from user input
                Pet newPet = new Pet();
                newPet.name = petName;
                newPet.age = Integer.parseInt(petAge); // Convert age to integer
                newPet.rendezvousDate = petRendezvous;

                // Use a background thread to save the pet details in the database
                ExecutorService executor = Executors.newSingleThreadExecutor();
                executor.execute(() -> {
                    petDao.insert(newPet); // Insert the new pet into the database
                    runOnUiThread(() -> {
                        // Show success message on the UI thread
                        Toast.makeText(AddPet.this, "Pet Added Successfully!", Toast.LENGTH_SHORT).show();
                        // Schedule a notification for the pet's rendezvous time
                        scheduleNotification(petName, selectedDateTime.getTimeInMillis());
                        // Close the activity after saving the pet
                        finish();
                    });
                });
            }
        });
    }

    // Show Date and Time Picker Dialog to select the rendezvous date and time
    private void showDateTimePicker() {
        final Calendar calendar = Calendar.getInstance();
        // Create a DatePickerDialog to select the date
        DatePickerDialog dpd = new DatePickerDialog(AddPet.this,
                (DatePicker view, int year, int month, int dayOfMonth) -> {
                    // Set the selected date in the calendar
                    calendar.set(year, month, dayOfMonth);
                    // Create a TimePickerDialog to select the time
                    TimePickerDialog tpd = new TimePickerDialog(AddPet.this,
                            (TimePicker timeView, int hourOfDay, int minute) -> {
                                // Set the selected time in the calendar
                                calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                                calendar.set(Calendar.MINUTE, minute);
                                calendar.set(Calendar.SECOND, 0);
                                selectedDateTime = calendar; // Store the selected date and time

                                // Format and display the selected date and time in the EditText
                                String formattedDateTime = android.text.format.DateFormat
                                        .format("dd/MM/yyyy HH:mm", calendar).toString();
                                etPetRendezvous.setText(formattedDateTime);
                            },
                            calendar.get(Calendar.HOUR_OF_DAY),
                            calendar.get(Calendar.MINUTE),
                            true);
                    tpd.show(); // Show the time picker
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH));
        dpd.show(); // Show the date picker
    }

    // Schedule a notification for the pet's rendezvous time
    private void scheduleNotification(String petName, long triggerTimeMillis) {
        // Create an Intent to trigger the NotificationReceiver
        Intent intent = new Intent(this, NotificationReceiver.class);
        intent.putExtra("petName", petName); // Pass the pet name to the receiver

        // Create a PendingIntent to wrap the intent for the alarm manager
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                this,
                (int) System.currentTimeMillis(), // Use current time as a unique request code
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE // Flags for mutability
        );

        // Get the AlarmManager system service
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        if (alarmManager != null) {
            // Set an exact alarm to trigger the notification at the scheduled time
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, triggerTimeMillis, pendingIntent);
        }
    }
}
