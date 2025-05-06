package com.example.vetapp;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.MotionEvent;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import org.osmdroid.config.Configuration;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;

public class MapPickerActivity extends AppCompatActivity {

    // Constants for latitude and longitude data
    public static final String EXTRA_LAT = "lat";
    public static final String EXTRA_LNG = "lng";

    // Declare variables for map, selected point, and instruction text
    private MapView mapView;
    private GeoPoint selectedPoint;
    private TextView instructionText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Initialize OSMDroid configuration to load preferences
        Configuration.getInstance().load(getApplicationContext(),
                PreferenceManager.getDefaultSharedPreferences(getApplicationContext()));

        // Create a vertical LinearLayout as the root layout
        LinearLayout rootLayout = new LinearLayout(this);
        rootLayout.setOrientation(LinearLayout.VERTICAL);

        // Set up the Toolbar with a title and background color
        Toolbar toolbar = new Toolbar(this);
        toolbar.setBackgroundColor(Color.parseColor("#6200EE"));
        toolbar.setTitle("Choose Location"); // Set toolbar title
        toolbar.setTitleTextColor(Color.WHITE); // Set title text color
        rootLayout.addView(toolbar, new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                getResources().getDimensionPixelSize(
                        androidx.appcompat.R.dimen.abc_action_bar_default_height_material)
        ));

        // Set up instruction text for the user
        instructionText = new TextView(this);
        instructionText.setBackgroundColor(Color.parseColor("#CC000000")); // Dark background color
        instructionText.setTextColor(Color.WHITE); // White text color
        instructionText.setText("Tap anywhere on the map to select a location"); // Instruction text
        instructionText.setPadding(24, 24, 24, 24); // Padding around the text
        rootLayout.addView(instructionText, new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        ));

        // Set up the map container
        mapView = new MapView(this);
        mapView.setMultiTouchControls(true); // Enable multitouch controls on the map
        mapView.getController().setZoom(6.0); // Set the initial zoom level
        mapView.getController().setCenter(new GeoPoint(36.8, 10.2)); // Set the initial map center (Tunis)
        rootLayout.addView(mapView, new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                0, 1 // Set map to take the remaining space
        ));

        // Set up the confirm location button
        Button confirmBtn = new Button(this);
        confirmBtn.setText("Confirm Location"); // Set button text
        confirmBtn.setBackgroundColor(Color.parseColor("#6200EE")); // Set button color
        confirmBtn.setTextColor(Color.WHITE); // Set button text color
        LinearLayout.LayoutParams btnParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        btnParams.setMargins(32, 24, 32, 24); // Add margins around the button
        rootLayout.addView(confirmBtn, btnParams);

        // Set the content view to the root layout
        setContentView(rootLayout);

        // Map touch listener to detect taps and select a location
        mapView.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_UP) {
                // Get the tapped point on the map
                GeoPoint tappedPoint = (GeoPoint) mapView.getProjection()
                        .fromPixels((int) event.getX(), (int) event.getY());

                // Store the selected point
                selectedPoint = tappedPoint;

                // Clear existing markers and add the new one at the tapped location
                mapView.getOverlays().clear();
                Marker marker = new Marker(mapView);
                marker.setPosition(tappedPoint);
                marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM); // Set the marker position
                marker.setTitle("Selected Location"); // Title for the marker
                mapView.getOverlays().add(marker);
                mapView.invalidate(); // Redraw the map

                // Update instruction text to indicate location has been selected
                instructionText.setText("Location selected! You can confirm now.");
                instructionText.setTextColor(Color.WHITE); // Change instruction text color to white
            }
            return false; // Return false to let the map handle the event
        });

        // Set the logic for the confirm button
        confirmBtn.setOnClickListener(v -> {
            if (selectedPoint != null) {
                // If a location is selected, return the latitude and longitude
                Intent result = new Intent();
                result.putExtra(EXTRA_LAT, selectedPoint.getLatitude());
                result.putExtra(EXTRA_LNG, selectedPoint.getLongitude());
                setResult(RESULT_OK, result); // Set the result with the selected coordinates
                finish(); // Close the activity
            } else {
                // If no location is selected, prompt the user to select a location
                instructionText.setText("Please tap on the map to select a location first.");
                instructionText.setTextColor(Color.RED); // Change text color to red for an error message
            }
        });
    }

    // Override onResume to handle map's onResume
    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume(); // Resume the map
    }

    // Override onPause to handle map's onPause
    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause(); // Pause the map
    }
}
