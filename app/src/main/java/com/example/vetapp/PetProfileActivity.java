package com.example.vetapp;

// 📦 Imports nécessaires pour les fonctionnalités de l'activité
import android.Manifest;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

// Classe représentant l’activité de profil de l’animal
public class PetProfileActivity extends AppCompatActivity {

    // Constantes pour les codes de requête
    private static final int CAMERA_REQUEST_CODE = 100;
    private static final int PERMISSION_REQUEST_CODE = 200;
    private static final int LOCATION_REQUEST_CODE = 300;
    private static final int NOTIFICATION_PERMISSION_REQUEST = 400;

    // Déclaration des vues
    TextView editTextName, editTextAge, editTextRendezvous;
    TextView textViewLocation;
    ImageView imageView;
    Button btnSave, btnDelete, btnSetLocation, btnTakePhoto, btnUpdate;

    // Base de données et DAO
    AppDataBase db;
    PetDao petDao;
    Pet currentPet;
    int petId;
    String currentLocation = "";
    Uri imageUri;
    private boolean cameFromMap = false;

    // Méthode appelée lors de la création de l’activité
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pet_profile);

        // 1. Récupérer les vues par leurs ID
        editTextName = findViewById(R.id.editTextPetName);
        editTextAge = findViewById(R.id.editTextPetAge);
        editTextRendezvous = findViewById(R.id.editTextRendezvous);
        textViewLocation = findViewById(R.id.textViewLocation);
        imageView = findViewById(R.id.imageViewPetPhoto);
        btnSave = findViewById(R.id.buttonSave);
        btnDelete = findViewById(R.id.buttonDelete);
        btnSetLocation = findViewById(R.id.buttonSetLocation);
        btnTakePhoto = findViewById(R.id.buttonTakePhoto);
        btnUpdate = findViewById(R.id.Update);

        // 2. Initialiser la base de données et le DAO
        db = AppDataBase.getDatabase(this);
        petDao = db.petDao();

        // 3. Charger les données du pet via son ID
        petId = getIntent().getIntExtra("petId", -1);
        new Thread(() -> {
            currentPet = petDao.getPetById(petId);
            runOnUiThread(() -> {
                // Afficher les infos dans les vues
                if (currentPet != null) {
                    editTextName.setText(currentPet.name);
                    editTextAge.setText(String.valueOf(currentPet.age));
                    editTextRendezvous.setText(currentPet.rendezvousDate);
                    currentLocation = currentPet.location;
                    textViewLocation.setText("Location: " + (currentLocation == null ? "Unknown" : currentLocation));
                    if (currentPet.photoUri != null) {
                        imageUri = Uri.parse(currentPet.photoUri);
                        imageView.setImageURI(imageUri);
                    }
                }
            });
        }).start();

        // 4. Définir la localisation via carte
        btnSetLocation.setOnClickListener(v -> {
            Intent i = new Intent(this, MapPickerActivity.class);
            startActivityForResult(i, LOCATION_REQUEST_CODE);
        });

        // 5. Sauvegarder les modifications et programmer une notification
        btnSave.setOnClickListener(v -> {
            new Thread(() -> {
                currentPet.name = editTextName.getText().toString();
                currentPet.age = Integer.parseInt(editTextAge.getText().toString());
                currentPet.rendezvousDate = editTextRendezvous.getText().toString();
                currentPet.location = currentLocation;
                if (imageUri != null) {
                    currentPet.photoUri = imageUri.toString();
                }
                petDao.update(currentPet);
                runOnUiThread(() -> {
                    Toast.makeText(this, "Saved!", Toast.LENGTH_SHORT).show();
                    scheduleAppointmentNotification(currentPet.name, currentPet.rendezvousDate);
                });
            }).start();
        });

        // 6. Supprimer un pet
        btnDelete.setOnClickListener(v -> {
            new Thread(() -> {
                petDao.delete(currentPet);
                runOnUiThread(() -> {
                    Toast.makeText(this, "Deleted", Toast.LENGTH_SHORT).show();
                    finish();
                });
            }).start();
        });

        // 7. Prendre une photo via la caméra
        btnTakePhoto.setOnClickListener(v -> {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.CAMERA},
                        PERMISSION_REQUEST_CODE);
            } else {
                openCamera();
            }
        });

        // 8. Accéder à l’activité de mise à jour
        btnUpdate.setOnClickListener(v -> {
            Intent intent = new Intent(this, UpdatePetActivity.class);
            intent.putExtra("petId", petId);
            startActivity(intent);
        });

        // 9. Gérer la permission de notification (Android 13+)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.POST_NOTIFICATIONS},
                        NOTIFICATION_PERMISSION_REQUEST);
            }
        }
    }

    // Méthode pour ouvrir l’appareil photo
    private void openCamera() {
        Intent cam = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (cam.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(cam, CAMERA_REQUEST_CODE);
        } else {
            Toast.makeText(this, "No camera app found", Toast.LENGTH_SHORT).show();
        }
    }

    // Résultat des activités (caméra ou carte)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CAMERA_REQUEST_CODE
                && resultCode == RESULT_OK
                && data != null
                && data.getExtras() != null) {
            Bitmap photo = (Bitmap) data.getExtras().get("data");
            imageView.setImageBitmap(photo);
            String path = MediaStore.Images.Media.insertImage(
                    getContentResolver(), photo, "PetPhoto", null
            );
            imageUri = Uri.parse(path);
        } else if (requestCode == LOCATION_REQUEST_CODE
                && resultCode == RESULT_OK
                && data != null) {
            double lat = data.getDoubleExtra(MapPickerActivity.EXTRA_LAT, 0);
            double lng = data.getDoubleExtra(MapPickerActivity.EXTRA_LNG, 0);
            currentLocation = "Lat: " + lat + ", Lng: " + lng;
            textViewLocation.setText("Location: " + currentLocation);
        }
        cameFromMap = true;
    }

    // Gérer le résultat des demandes de permissions
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String[] permissions,
                                           int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE
                && grantResults.length > 0
                && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            openCamera();
        } else if (requestCode == NOTIFICATION_PERMISSION_REQUEST) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Notification permission granted", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Notification permission is required for reminders", Toast.LENGTH_SHORT).show();
            }
        }
    }

    // Rafraîchir les données lors du retour à l’activité
    @Override
    protected void onResume() {
        super.onResume();
        if (cameFromMap) {
            cameFromMap = false;
            return;
        }
        new Thread(() -> {
            currentPet = petDao.getPetById(petId);
            runOnUiThread(() -> {
                if (currentPet != null) {
                    editTextName.setText(currentPet.name);
                    editTextAge.setText(String.valueOf(currentPet.age));
                    editTextRendezvous.setText(currentPet.rendezvousDate);
                    currentLocation = currentPet.location;
                    textViewLocation.setText("Location: " + (currentLocation == null ? "Unknown" : currentLocation));
                    if (currentPet.photoUri != null) {
                        imageUri = Uri.parse(currentPet.photoUri);
                        imageView.setImageURI(imageUri);
                    }
                }
            });
        }).start();
    }

    // 📅 Planifier une notification de rendez-vous
    private void scheduleAppointmentNotification(String petName, String rendezvousDate) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());

        try {
            Date date = format.parse(rendezvousDate);
            if (date == null) return;

            long timeInMillis = date.getTime();
            if (timeInMillis < System.currentTimeMillis()) return;

            // Préparer l’intent pour la notification
            Intent intent = new Intent(this, NotificationReceiver.class);
            intent.putExtra("petName", petName);
            intent.putExtra("petId", petId); // ✅ On ajoute l’ID du pet

            // Créer un PendingIntent pour la notification
            PendingIntent pendingIntent = PendingIntent.getBroadcast(
                    this,
                    petId,
                    intent,
                    PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
            );

            // Utiliser AlarmManager pour programmer la notification
            AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
            if (alarmManager != null) {
                alarmManager.setExact(AlarmManager.RTC_WAKEUP, timeInMillis, pendingIntent);
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }
}
