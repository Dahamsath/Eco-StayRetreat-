package com.example.ecostayretreat;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.ecostayretreat.database.AppDatabase;
import com.example.ecostayretreat.database.UserDao;
import com.example.ecostayretreat.database.UserEntity;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class EditProfileActivity extends AppCompatActivity {

    private EditText nameEditText, emailEditText, preferencesEditText, travelDatesEditText;
    private Button saveButton;
    private AppDatabase database;
    private UserDao userDao;

    // Simulate current user ID - in a real app, this would come from a session manager
    private int currentUserId = 1; // Example ID

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        database = AppDatabase.getDatabase(this);
        userDao = database.userDao();

        nameEditText = findViewById(R.id.editTextEditName);
        emailEditText = findViewById(R.id.editTextEditEmail);
        preferencesEditText = findViewById(R.id.editTextEditPreferences);
        travelDatesEditText = findViewById(R.id.editTextEditTravelDates);
        saveButton = findViewById(R.id.buttonSaveProfile);

        loadCurrentProfile(); // Load existing profile data into fields

        saveButton.setOnClickListener(v -> saveProfile());
    }

    private void loadCurrentProfile() {
        new Thread(() -> {
            UserEntity user = userDao.getUserById(currentUserId);
            if (user != null) {
                runOnUiThread(() -> {
                    nameEditText.setText(user.getName());
                    emailEditText.setText(user.getEmail());
                    preferencesEditText.setText(user.getPreferences() != null ? user.getPreferences() : "");
                    travelDatesEditText.setText(user.getTravelDates() != null ? user.getTravelDates() : "");
                    emailEditText.setEnabled(false); // Prevent changing email for simplicity, or add validation
                });
            } else {
                runOnUiThread(() -> {
                    Toast.makeText(EditProfileActivity.this, "User profile not found.", Toast.LENGTH_SHORT).show();
                    finish(); // Close activity if user is not found
                });
            }
        }).start();
    }

    private void saveProfile() {
        String newName = nameEditText.getText().toString().trim();
        String newEmail = emailEditText.getText().toString().trim(); // Kept for consistency, might be disabled
        String newPreferences = preferencesEditText.getText().toString().trim();
        String newTravelDates = travelDatesEditText.getText().toString().trim();

        // Enhanced validation
        if (newName.isEmpty()) {
            nameEditText.setError("Name is required");
            return;
        }
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(newEmail).matches()) {
            emailEditText.setError("Please enter a valid email");
            return;
        }
        // NEW: Validate travel dates format (basic check for YYYY-MM-DD to YYYY-MM-DD)
        if (!newTravelDates.isEmpty()) {
            if (!isValidTravelDateRange(newTravelDates)) {
                travelDatesEditText.setError("Invalid date format. Use YYYY-MM-DD to YYYY-MM-DD (e.g., 2024-03-15 to 2024-03-20)");
                return;
            }
            // Optional: Also check if the end date is not before the start date
            if (!isEndDateAfterStartDate(newTravelDates)) {
                travelDatesEditText.setError("End date must be after start date.");
                return;
            }
        }

        new Thread(() -> {
            try {
                UserEntity user = userDao.getUserById(currentUserId);
                if (user != null) {
                    user.setName(newName);
                    // user.setEmail(newEmail); // Only update if logic allows email changes
                    user.setPreferences(newPreferences.isEmpty() ? null : newPreferences); // Store as null if empty string
                    user.setTravelDates(newTravelDates.isEmpty() ? null : newTravelDates); // Store as null if empty string

                    userDao.updateUser(user);

                    runOnUiThread(() -> {
                        Toast.makeText(EditProfileActivity.this, "Profile updated successfully!", Toast.LENGTH_SHORT).show();
                        finish(); // Close the edit activity
                    });
                } else {
                    runOnUiThread(() -> {
                        Toast.makeText(EditProfileActivity.this, "Failed to update profile: User not found.", Toast.LENGTH_LONG).show();
                    });
                }
            } catch (Exception e) {
                e.printStackTrace();
                runOnUiThread(() -> {
                    Toast.makeText(EditProfileActivity.this, "Failed to update profile: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
            }
        }).start();
    }

    // NEW HELPER: Validate travel date range format
    private boolean isValidTravelDateRange(String dateRange) {
        // Pattern: YYYY-MM-DD to YYYY-MM-DD
        String pattern = "^\\d{4}-\\d{2}-\\d{2} to \\d{4}-\\d{2}-\\d{2}$";
        return dateRange.matches(pattern);
    }

    // NEW HELPER: Check if end date is after start date within the range string
    private boolean isEndDateAfterStartDate(String dateRange) {
        if (!isValidTravelDateRange(dateRange)) {
            return false; // If format is invalid, don't proceed with date comparison
        }
        try {
            String[] parts = dateRange.split(" to ");
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            Date startDate = sdf.parse(parts[0]);
            Date endDate = sdf.parse(parts[1]);
            return !endDate.before(startDate); // Returns true if end is after or same as start
        } catch (ParseException e) {
            e.printStackTrace();
            return false; // Parsing failed, consider it invalid
        }
    }
}