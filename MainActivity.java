package com.example.ecostayretreat;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private Button roomsButton, activitiesButton, profileButton, greenInitiativesButton, 
                   notificationsButton, calendarButton, recommendationsButton, logoutButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        createNotificationChannel();
        

        SampleDataInitializer.initializeAllSampleData(this);

        roomsButton = findViewById(R.id.buttonRooms);
        activitiesButton = findViewById(R.id.buttonActivities);
        profileButton = findViewById(R.id.buttonProfile);
        greenInitiativesButton = findViewById(R.id.buttonGreenInitiatives);
        notificationsButton = findViewById(R.id.buttonNotifications);
        calendarButton = findViewById(R.id.buttonCalendar);
        recommendationsButton = findViewById(R.id.buttonRecommendations);
        logoutButton = findViewById(R.id.buttonLogout);

        roomsButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, RoomListActivity.class);
            startActivity(intent);
        });

        activitiesButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, ActivityListActivity.class);
            startActivity(intent);
        });

        profileButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, ProfileActivity.class);
            startActivity(intent);
        });

        greenInitiativesButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, GreenInitiativesActivity.class);
            startActivity(intent);
        });

        notificationsButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, NotificationsActivity.class);
            startActivity(intent);
        });

        calendarButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, ActivityCalendarActivity.class);
            startActivity(intent);
        });

        recommendationsButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, RecommendationsActivity.class);
            startActivity(intent);
        });

        logoutButton.setOnClickListener(v -> {

            AuthActivity.clearUserSession(MainActivity.this);
            Intent intent = new Intent(MainActivity.this, AuthActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        });
    }


    private void createNotificationChannel() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.channel_name);
            String description = getString(R.string.channel_description);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel("EcoStayChannelId", name, importance);
            channel.setDescription(description);


            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }
}