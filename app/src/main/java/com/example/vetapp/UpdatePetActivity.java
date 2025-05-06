package com.example.vetapp;

import android.app.DatePickerDialog;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.util.Calendar;

public class UpdatePetActivity extends AppCompatActivity {

    // UI elements
    EditText editTextName, editTextAge, editTextRendezvous, editTextLocation;
    ImageView imageView;
    Button btnSaveUpdate;

    // Database and DAO
    AppDataBase db;
    PetDao petDao;
    Pet currentPet;
    int petId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_pet); // Set layout for this activity

        // Link UI components from layout
        editTextName = findViewById(R.id.editTextPetName);
        editTextAge = findViewById(R.id.editTextPetAge);
        editTextRendezvous = findViewById(R.id.editTextRendezvous);
        editTextLocation = findViewById(R.id.editTextLocation);
        imageView = findViewById(R.id.imageViewPetPhoto);
        btnSaveUpdate = findViewById(R.id.buttonSaveUpdate);

        // Initialize Room database and DAO
        db = AppDataBase.getDatabase(this);
        petDao = db.petDao();

        // Get pet ID passed from previous activity
        petId = getIntent().getIntExtra("petId", -1);

        // Handle invalid ID
        if (petId == -1) {
            Toast.makeText(this, "Invalid pet ID", Toast.LENGTH_SHORT).show();
            finish(); // Close activity
            return;
        }

        // Load pet details from database in background thread
        new Thread(() -> {
            currentPet = petDao.getPetById(petId); // Fetch pet
            runOnUiThread(() -> {
                // Populate fields with pet data
                if (currentPet != null) {
                    editTextName.setText(currentPet.name);
                    editTextAge.setText(String.valueOf(currentPet.age));
                    editTextRendezvous.setText(currentPet.rendezvousDate);
                    editTextLocation.setText(currentPet.location);
                    if (currentPet.photoUri != null) {
                        imageView.setImageURI(Uri.parse(currentPet.photoUri)); // Show pet photo
                    }
                }
            });
        }).start();

        // Date & time picker dialog for rendezvous date
        editTextRendezvous.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);

            // Show DatePickerDialog
            DatePickerDialog datePickerDialog = new DatePickerDialog(
                    this,
                    (view, selectedYear, selectedMonth, selectedDay) -> {
                        // After date picked, show time picker
                        int hour = calendar.get(Calendar.HOUR_OF_DAY);
                        int minute = calendar.get(Calendar.MINUTE);

                        new android.app.TimePickerDialog(
                                this,
                                (timeView, selectedHour, selectedMinute) -> {
                                    // Format and set datetime to the editText
                                    String formatted = String.format("%02d/%02d/%04d %02d:%02d",
                                            selectedDay, selectedMonth + 1, selectedYear, selectedHour, selectedMinute);
                                    editTextRendezvous.setText(formatted);
                                },
                                hour, minute, true
                        ).show();
                    },
                    year, month, day
            );
            datePickerDialog.show(); // Show the calendar
        });

        // Handle Save button click
        btnSaveUpdate.setOnClickListener(v -> {
            // Get and validate user input
            String name = editTextName.getText().toString().trim();
            String ageText = editTextAge.getText().toString().trim();
            String rendezvous = editTextRendezvous.getText().toString().trim();
            String location = editTextLocation.getText().toString().trim();

            if (name.isEmpty() || ageText.isEmpty() || rendezvous.isEmpty() || location.isEmpty()) {
                Toast.makeText(this, "Please fill all the fields", Toast.LENGTH_SHORT).show();
                return;
            }

            try {
                int age = Integer.parseInt(ageText); // Parse age

                // Update currentPet object with new values
                currentPet.name = name;
                currentPet.age = age;
                currentPet.rendezvousDate = rendezvous;
                currentPet.location = location;

                // Update in database using background thread
                new Thread(() -> {
                    petDao.update(currentPet); // Update pet record

                    runOnUiThread(() -> {
                        Toast.makeText(this, "Pet profile updated!", Toast.LENGTH_SHORT).show();
                        finish(); // Close activity after saving
                    });
                }).start();

            } catch (NumberFormatException e) {
                // Handle invalid age input
                Toast.makeText(this, "Please enter a valid age", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
