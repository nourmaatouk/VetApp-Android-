package com.example.vetapp;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

// BroadcastReceiver triggered by an AlarmManager to show a vet appointment reminder
public class NotificationReceiver extends BroadcastReceiver {

    // Called when the broadcast is received
    @Override
    public void onReceive(Context context, Intent intent) {
        // Retrieve the pet's name from the intent extras
        String petName = intent.getStringExtra("petName");
        if (petName == null) petName = "votre animal"; // Fallback name if null

        // Create an intent to open the PetListActivity when the notification is clicked
        Intent notificationIntent = new Intent(context, PetListActivity.class); // You can change this to open another activity like PetProfileActivity

        // Create a PendingIntent to wrap the above intent (to be triggered on notification tap)
        PendingIntent pendingIntent = PendingIntent.getActivity(
                context,
                0,
                notificationIntent,
                PendingIntent.FLAG_IMMUTABLE // Ensures the PendingIntent cannot be changed
        );

        // Set up a notification channel (required for Android O and above)
        String channelId = "vet_reminder_channel"; // Unique ID for this notification channel
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Rappels de rendez-vous"; // Channel name shown to user
            String description = "Notifications pour les rendez-vous vétérinaires"; // Description shown in system settings
            int importance = NotificationManager.IMPORTANCE_HIGH; // High importance shows heads-up notification

            // Create and configure the channel
            NotificationChannel channel = new NotificationChannel(channelId, name, importance);
            channel.setDescription(description);

            // Register the channel with the system
            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(channel);
            }
        }

        // Build the actual notification
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, channelId)
                .setSmallIcon(android.R.drawable.ic_popup_reminder) // Small icon displayed with the notification
                .setContentTitle("Rendez-vous vétérinaire") // Title of the notification
                .setContentText("N'oubliez pas le rendez-vous de " + petName + " aujourd'hui 🐶🐱") // Main content text
                .setContentIntent(pendingIntent) // Action to perform when user taps the notification
                .setAutoCancel(true) // Dismiss notification after tapping
                .setPriority(NotificationCompat.PRIORITY_HIGH); // Ensures notification gets proper visibility

        // Show the notification using the NotificationManager
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        int notificationId = intent.getIntExtra("petId", 0); // Unique ID for each notification (based on pet ID)

        // Finally, show the notification
        notificationManager.notify(notificationId, builder.build());
    }
}
