package com.example.vetapp;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.example.vetapp.R;

public class SignupActivity extends AppCompatActivity {

    EditText etUsername, etPassword;
    Button btnSignup;
    AppDataBase db; // Room Database

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup); // Assuming you have a layout for SignupActivity

        // Initialize views
        etUsername = findViewById(R.id.editUsername); // Ensure these ids match your XML layout
        etPassword = findViewById(R.id.editPassword);
        btnSignup = findViewById(R.id.btnSignup);

        // Get the database instance
        db = AppDataBase.getDatabase(this);

        // Signup button click listener
        btnSignup.setOnClickListener(v -> {
            String username = etUsername.getText().toString().trim();
            String password = etPassword.getText().toString().trim();

            // Check if fields are not empty
            if (username.isEmpty() || password.isEmpty()) {
                Toast.makeText(SignupActivity.this, "Please fill in both fields.", Toast.LENGTH_SHORT).show();
            } else {
                // Check if the username already exists
                new Thread(() -> {
                    User existingUser = db.userDao().findByUsername(username);

                    if (existingUser != null) {
                        // Show a message if the username already exists
                        runOnUiThread(() -> {
                            Toast.makeText(SignupActivity.this, "Username already exists", Toast.LENGTH_SHORT).show();
                        });
                    } else {
                        // Insert the new user into the database
                        User newUser = new User(username, password); // Assuming your User class has this constructor
                        db.userDao().insert(newUser);

                        runOnUiThread(() -> {
                            Toast.makeText(SignupActivity.this, "Account Created!", Toast.LENGTH_SHORT).show();
                            finish(); // Close the activity and go back to login
                        });
                    }
                }).start();
            }
        });
    }
}
