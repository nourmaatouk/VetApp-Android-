package com.example.vetapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {

    EditText etUsername, etPassword;
    Button btnLogin, btnSignup;
    AppDataBase db;
    UserDao userDao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Initialize views
        etUsername = findViewById(R.id.editUsername);
        etPassword = findViewById(R.id.editPassword);
        btnLogin = findViewById(R.id.btnLogin);
        btnSignup = findViewById(R.id.btnSignup);

        // Get the database instance and the user DAO
        db = AppDataBase.getDatabase(this);
        userDao = db.userDao();

        // Handle Signup button click
        btnSignup.setOnClickListener(v -> {

            startActivity(new Intent(this, SignupActivity.class));

        });

        // Handle Login button click
        btnLogin.setOnClickListener(v -> {
            new Thread(() -> {
                String username = etUsername.getText().toString().trim();
                String password = etPassword.getText().toString().trim();

                if(username.isEmpty()||password.isEmpty()){
                    runOnUiThread(() -> Toast.makeText(this, "Invalid, Please create Account", Toast.LENGTH_SHORT).show());

                }

                // Check if the username and password match an existing user
                User user = userDao.login(username, password);

                if (user != null) {
                    // If login is successful, show a message and start the PetListActivity
                    runOnUiThread(() -> {
                        Toast.makeText(this, "Login Successful", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(this, PetListActivity.class));
                    });
                } else {
                    // If login fails, show an error message
                    runOnUiThread(() -> Toast.makeText(this, "Invalid Credentials", Toast.LENGTH_SHORT).show());
                }
            }).start();
        });
    }
}
