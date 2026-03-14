package com.example.ecostayretreat;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.ecostayretreat.database.AppDatabase;
import com.example.ecostayretreat.database.BookingDao;
import com.example.ecostayretreat.database.BookingEntity;
import com.example.ecostayretreat.database.UserDao;
import com.example.ecostayretreat.database.UserEntity;
import com.example.ecostayretreat.database.RoomDao;
import com.example.ecostayretreat.database.RoomEntity;
import com.example.ecostayretreat.database.ActivityDao;
import com.example.ecostayretreat.database.ActivityEntity;
import java.util.List;
import android.widget.ListView;
import android.widget.ImageButton;
import androidx.appcompat.app.AlertDialog;

public class ProfileActivity extends AppCompatActivity {

    private TextView nameTextView, emailTextView, preferencesTextView, travelDatesTextView, avatarTextView; // Booking list replaces bookingHistoryTextView
    private ListView bookingsListView;
    private AppDatabase database;
    private UserDao userDao;
    private BookingDao bookingDao;
    private RoomDao roomDao;
    private ActivityDao activityDao;

    private int currentUserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        // Get current user from session
        currentUserId = AuthActivity.getCurrentUserId(this);
        if (currentUserId == -1) {
            // User not logged in, redirect to auth
            Intent intent = new Intent(this, AuthActivity.class);
            startActivity(intent);
            finish();
            return;
        }
        
        database = AppDatabase.getDatabase(this);
        userDao = database.userDao();
        bookingDao = database.bookingDao(); // Initialize BookingDao
        roomDao = database.roomDao();
        activityDao = database.activityDao();

        nameTextView = findViewById(R.id.textViewProfileName);
        emailTextView = findViewById(R.id.textViewProfileEmail);
        preferencesTextView = findViewById(R.id.textViewPreferences);
        travelDatesTextView = findViewById(R.id.textViewTravelDates);
        bookingsListView = findViewById(R.id.listViewBookings);
        TextView emptyView = findViewById(R.id.textViewNoBookings);
        if (bookingsListView != null && emptyView != null) {
            bookingsListView.setEmptyView(emptyView);
        }
        avatarTextView = findViewById(R.id.textViewAvatar);

        // Inside ProfileActivity.java onCreate method:
        Button editProfileButton = findViewById(R.id.buttonEditProfile); // Assuming you added the button to the layout
        if (editProfileButton != null) { // Check if the button exists in the layout
            editProfileButton.setOnClickListener(v -> {
                Intent intent = new Intent(ProfileActivity.this, EditProfileActivity.class);
                startActivity(intent);
            });
        }
        
        // Add recommendations button
        Button recommendationsButton = findViewById(R.id.buttonRecommendations);
        if (recommendationsButton != null) {
            recommendationsButton.setOnClickListener(v -> {
                Intent intent = new Intent(ProfileActivity.this, RecommendationsActivity.class);
                startActivity(intent);
            });
        }

        loadUserProfile();
        loadUserBookings(); // Load and display bookings
    }

    private void loadUserProfile() {
        new Thread(() -> {
            UserEntity user = userDao.getUserById(currentUserId); // Use the simulated ID
            if (user != null) {
                runOnUiThread(() -> {
                    nameTextView.setText(user.getName());
                    emailTextView.setText(user.getEmail());
                    preferencesTextView.setText(getString(R.string.label_preferences) + " " + (user.getPreferences() != null ? user.getPreferences() : "Not set"));
                    travelDatesTextView.setText(getString(R.string.label_travel_dates) + " " + (user.getTravelDates() != null ? user.getTravelDates() : "Not set"));
                    if (avatarTextView != null) {
                        avatarTextView.setText(getInitials(user.getName(), user.getEmail()));
                    }
                });
            } else {
                runOnUiThread(() -> {
                    Toast.makeText(ProfileActivity.this, getString(R.string.msg_user_profile_not_found), Toast.LENGTH_SHORT).show();
                    // Handle case where user is not found (e.g., logout, navigate back)
                    finish();
                });
            }
        }).start();
    }

    private void loadUserBookings() {
        new Thread(() -> {
            List<BookingEntity> userBookings = bookingDao.getBookingsByUserId(currentUserId);
            List<ProfileBookingAdapter.Item> items = new java.util.ArrayList<>();
            for (BookingEntity booking : userBookings) {
                String title;
                if ("room".equalsIgnoreCase(booking.getItemType())) {
                    RoomEntity room = roomDao.getRoomById(booking.getItemId());
                    title = room != null ? "Room: " + room.getRoomType() : "Room ID " + booking.getItemId();
                } else {
                    ActivityEntity act = activityDao.getActivityById(booking.getItemId());
                    title = act != null ? "Activity: " + act.getActivityName() : "Activity ID " + booking.getItemId();
                }
                String subtitle = booking.getBookingDate();
                if (booking.getStartTime() != null && booking.getEndTime() != null) {
                    subtitle += " • " + booking.getStartTime() + " - " + booking.getEndTime();
                }
                items.add(new ProfileBookingAdapter.Item(booking, title, subtitle));
            }
            runOnUiThread(() -> {
                ProfileBookingAdapter adapter = new ProfileBookingAdapter(ProfileActivity.this, items, b -> confirmAndDeleteBooking(b));
                bookingsListView.setAdapter(adapter);
            });
        }).start();
    }

    private void confirmAndDeleteBooking(BookingEntity booking) {
        new AlertDialog.Builder(this)
                .setTitle(R.string.dialog_delete_booking_title)
                .setMessage(R.string.dialog_delete_booking_message)
                .setPositiveButton(R.string.delete, (d, w) -> deleteBooking(booking))
                .setNegativeButton(R.string.cancel, null)
                .show();
    }

    private void deleteBooking(BookingEntity booking) {
        new Thread(() -> {
            try {
                bookingDao.deleteBooking(booking);
                runOnUiThread(() -> {
                    Toast.makeText(ProfileActivity.this, R.string.msg_booking_deleted, Toast.LENGTH_SHORT).show();
                    loadUserBookings();
                });
            } catch (Exception e) {
                e.printStackTrace();
                runOnUiThread(() -> Toast.makeText(ProfileActivity.this, e.getMessage(), Toast.LENGTH_LONG).show());
            }
        }).start();
    }

    private String getInitials(String name, String email) {
        if (name != null && !name.trim().isEmpty()) {
            String[] parts = name.trim().split(" ");
            if (parts.length == 1) {
                return parts[0].substring(0, 1).toUpperCase();
            } else {
                return (parts[0].substring(0, 1) + parts[parts.length - 1].substring(0, 1)).toUpperCase();
            }
        }
        if (email != null && !email.isEmpty()) {
            return email.substring(0, 1).toUpperCase();
        }
        return "U";
    }
}