package com.example.ecostayretreat;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.bumptech.glide.Glide; // Assuming you added Glide dependency
import com.example.ecostayretreat.database.AppDatabase;
import com.example.ecostayretreat.database.ActivityDao;
import com.example.ecostayretreat.database.ActivityEntity;

public class ActivityDetailActivity extends AppCompatActivity {

    private ImageView activityImageView;
    private TextView activityNameTextView, descriptionTextView, priceTextView, durationTextView, scheduleTextView, availabilityTextView;
    private Button bookActivityButton;
    private ActivityEntity currentActivity;
    private AppDatabase database;
    private ActivityDao activityDao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_activity_detail);

        database = AppDatabase.getDatabase(this);
        activityDao = database.activityDao();

        activityImageView = findViewById(R.id.imageViewActivity);
        activityNameTextView = findViewById(R.id.textViewActivityName);
        descriptionTextView = findViewById(R.id.textViewDescription);
        priceTextView = findViewById(R.id.textViewPrice);
        durationTextView = findViewById(R.id.textViewDuration);
        scheduleTextView = findViewById(R.id.textViewSchedule);
        availabilityTextView = findViewById(R.id.textViewAvailability);
        bookActivityButton = findViewById(R.id.buttonBookActivity);

        // Get activity data from intent (replace with actual database fetch based on ID)
        int activityId = getIntent().getIntExtra("ACTIVITY_ID", -1);

        if (activityId != -1) {
            new Thread(() -> {
                currentActivity = activityDao.getActivityById(activityId);
                runOnUiThread(() -> {
                    if (currentActivity != null) {
                        activityNameTextView.setText(currentActivity.getActivityName());
                        descriptionTextView.setText(currentActivity.getDescription());
                        priceTextView.setText(String.format(getString(R.string.label_price) + " $%.2f", currentActivity.getPrice()));
                        durationTextView.setText(getString(R.string.label_duration) + " " + currentActivity.getDuration());
                        scheduleTextView.setText(getString(R.string.label_schedule) + " " + currentActivity.getSchedule());
                        availabilityTextView.setText(currentActivity.isAvailable() ? getString(R.string.label_availability) : getString(R.string.label_unavailable));
                        // Load image using Glide or set a local drawable
                        loadActivityImage(currentActivity.getImageUrl());
                        bookActivityButton.setEnabled(currentActivity.isAvailable()); // Disable button if not available
                    } else {
                        // Handle case where activity is not found
                        Toast.makeText(ActivityDetailActivity.this, getString(R.string.msg_item_not_found), Toast.LENGTH_SHORT).show();
                        finish(); // Close activity or show error
                    }
                });
            }).start();
        } else {
            Toast.makeText(this, getString(R.string.error_invalid_activity_id), Toast.LENGTH_SHORT).show();
            finish(); // Close activity if ID is invalid
        }

        bookActivityButton.setOnClickListener(v -> {
            if (currentActivity != null && currentActivity.isAvailable()) {
                // Navigate to booking activity
                Intent intent = new Intent(ActivityDetailActivity.this, BookingActivity.class);
                intent.putExtra("ITEM_ID", currentActivity.getActivityId());
                intent.putExtra("ITEM_TYPE", "activity");
                startActivity(intent);
            } else {
                Toast.makeText(ActivityDetailActivity.this, "This activity is not available for booking.", Toast.LENGTH_SHORT).show();
            }
        });
    }
    
    /**
     * Loads activity image from drawable resource or URL
     * Handles the new local images you can add
     */
    private void loadActivityImage(String imageUrl) {
        if (imageUrl != null && !imageUrl.trim().isEmpty()) {
            // Handle local drawable resources (new pictures)
            if (imageUrl.startsWith("drawable://")) {
                String drawableName = imageUrl.replace("drawable://", "");
                int drawableId = getResources().getIdentifier(drawableName, "drawable", getPackageName());
                
                if (drawableId != 0) {
                    // Successfully found the drawable resource
                    activityImageView.setImageResource(drawableId);
                    activityImageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                } else {
                    // Drawable not found, use placeholder
                    activityImageView.setImageResource(R.drawable.ic_placeholder_activity);
                    Toast.makeText(this, "Image not found: " + drawableName, Toast.LENGTH_SHORT).show();
                }
            } else {
                // Load from URL using Glide (fallback for online images)
                Glide.with(this)
                    .load(imageUrl)
                    .placeholder(R.drawable.ic_placeholder_activity)
                    .error(R.drawable.ic_placeholder_activity)
                    .centerCrop()
                    .into(activityImageView);
            }
        } else {
            // No image URL provided, use placeholder
            activityImageView.setImageResource(R.drawable.ic_placeholder_activity);
        }
    }
}
