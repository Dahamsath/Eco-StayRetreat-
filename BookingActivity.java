package com.example.ecostayretreat;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar; // Import ProgressBar
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.example.ecostayretreat.database.AppDatabase;
import com.example.ecostayretreat.database.BookingDao;
import com.example.ecostayretreat.database.BookingEntity;
import com.example.ecostayretreat.database.RoomDao;
import com.example.ecostayretreat.database.RoomEntity;
import com.example.ecostayretreat.database.ActivityDao;
import com.example.ecostayretreat.database.ActivityEntity;
import com.example.ecostayretreat.database.UserDao;
import com.example.ecostayretreat.database.UserEntity;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date; // Import Date class
import java.util.List;
import java.util.Locale;

// NEW: Add imports for notifications and permissions
import android.Manifest;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;

public class BookingActivity extends AppCompatActivity {

    private TextView itemDetailsTextView, startTimeLabel, endTimeLabel;
    private EditText dateEditText, startTimeEditText, endTimeEditText; // endTime for activities
    private Button bookButton, datePickerButton, startTimePickerButton, endTimePickerButton;
    private ProgressBar progressBarBooking; // NEW: Add ProgressBar
    private int itemId;
    private String itemType; // "room" or "activity"
    private AppDatabase database;
    private BookingDao bookingDao;
    private RoomDao roomDao;
    private ActivityDao activityDao;
    private UserDao userDao;

    private Calendar selectedDate = Calendar.getInstance();

    // NEW: Add a constant for the permission request code
    private static final int REQUEST_POST_NOTIFICATIONS = 1001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking);

        database = AppDatabase.getDatabase(this);
        bookingDao = database.bookingDao();
        roomDao = database.roomDao();
        activityDao = database.activityDao();
        userDao = database.userDao();

        itemDetailsTextView = findViewById(R.id.textViewItemDetails);
        dateEditText = findViewById(R.id.editTextDate);
        startTimeEditText = findViewById(R.id.editTextStartTime); // Relevant for activities
        endTimeEditText = findViewById(R.id.editTextEndTime); // Relevant for activities
        startTimeLabel = findViewById(R.id.textViewStartTimeLabel);
        endTimeLabel = findViewById(R.id.textViewEndTimeLabel);
        bookButton = findViewById(R.id.buttonBook);
        datePickerButton = findViewById(R.id.buttonDatePicker);
        startTimePickerButton = findViewById(R.id.buttonStartTimePicker);
        endTimePickerButton = findViewById(R.id.buttonEndTimePicker);
        progressBarBooking = findViewById(R.id.progressBarBooking); // NEW: Initialize ProgressBar

        // Get item data from intent
        itemId = getIntent().getIntExtra("ITEM_ID", -1);
        itemType = getIntent().getStringExtra("ITEM_TYPE");
        
        // Check if date was pre-selected from calendar
        String preSelectedDate = getIntent().getStringExtra("SELECTED_DATE");
        if (preSelectedDate != null && !preSelectedDate.isEmpty()) {
            dateEditText.setText(preSelectedDate);
            // Parse and set the selected date
            try {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                Date parsedDate = sdf.parse(preSelectedDate);
                selectedDate.setTime(parsedDate);
            } catch (java.text.ParseException e) {
                e.printStackTrace();
            }
        }

        if (itemId == -1 || itemType == null) {
            Toast.makeText(this, getString(R.string.error_invalid_booking_request), Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Load item details based on type
        loadItemDetails();

        // Set up date picker
        datePickerButton.setOnClickListener(v -> {
            DatePickerDialog datePickerDialog = new DatePickerDialog(
                    BookingActivity.this,
                    (view, year, month, dayOfMonth) -> {
                        selectedDate.set(year, month, dayOfMonth);
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                        dateEditText.setText(sdf.format(selectedDate.getTime()));
                    },
                    selectedDate.get(Calendar.YEAR),
                    selectedDate.get(Calendar.MONTH),
                    selectedDate.get(Calendar.DAY_OF_MONTH)
            );
            datePickerDialog.show();
        });

        // Set up time picker buttons
        startTimePickerButton.setOnClickListener(v -> showTimePickerDialog(startTimeEditText));
        endTimePickerButton.setOnClickListener(v -> showTimePickerDialog(endTimeEditText));
        
        // Set up booking button
        bookButton.setOnClickListener(v -> performBookingWithCheck());
    }

    private void loadItemDetails() {
        new Thread(() -> {
            String details;
            if ("room".equals(itemType)) {
                RoomEntity room = roomDao.getRoomById(itemId);
                if (room != null) {
                    details = getString(R.string.label_room_type) + " " + room.getRoomType() + "\n" +
                            getString(R.string.label_description) + " " + room.getDescription() + "\n" +
                            getString(R.string.label_price) + " $" + room.getPricePerNight() + " per night";
                    runOnUiThread(() -> {
                        itemDetailsTextView.setText(details);
                        // Show time fields for rooms as check-in/check-out times
                        startTimeEditText.setVisibility(android.view.View.VISIBLE);
                        endTimeEditText.setVisibility(android.view.View.VISIBLE);
                        startTimeLabel.setVisibility(android.view.View.VISIBLE);
                        endTimeLabel.setVisibility(android.view.View.VISIBLE);
                        startTimeLabel.setText("Check-in Time:");
                        endTimeLabel.setText("Check-out Time:");
                        startTimeEditText.setHint("Check-in time (e.g., 15:00)");
                        endTimeEditText.setHint("Check-out time (e.g., 11:00)");
                    });
                } else {
                    details = "";
                }
            } else if ("activity".equals(itemType)) {
                ActivityEntity activity = activityDao.getActivityById(itemId);
                if (activity != null) {
                    details = getString(R.string.label_activity_name) + " " + activity.getActivityName() + "\n" +
                            getString(R.string.label_description) + " " + activity.getDescription() + "\n" +
                            getString(R.string.label_price) + " $" + activity.getPrice() + "\n" +
                            getString(R.string.label_duration) + " " + activity.getDuration() + "\n" +
                            getString(R.string.label_schedule) + " " + activity.getSchedule();
                    runOnUiThread(() -> {
                        itemDetailsTextView.setText(details);
                        // Show time fields for activities
                        startTimeEditText.setVisibility(android.view.View.VISIBLE);
                        endTimeEditText.setVisibility(android.view.View.VISIBLE);
                        startTimeLabel.setVisibility(android.view.View.VISIBLE);
                        endTimeLabel.setVisibility(android.view.View.VISIBLE);
                        startTimeLabel.setText("Start Time:");
                        endTimeLabel.setText("End Time:");
                        startTimeEditText.setHint("Start time (e.g., 09:00)");
                        endTimeEditText.setHint("End time (e.g., 11:00)");
                    });
                } else {
                    details = "";
                }
            } else {
                details = "";
            }
            if (details.isEmpty()) {
                runOnUiThread(() -> {
                    Toast.makeText(BookingActivity.this, getString(R.string.msg_item_not_found), Toast.LENGTH_SHORT).show();
                    finish(); // Close activity if details are not found
                });
            }
        }).start();
    }

    // NEW: Updated method to check for conflicts before booking with enhanced validation
    private void performBookingWithCheck() {
        String date = dateEditText.getText().toString().trim();
        String startTime = startTimeEditText.getText().toString().trim();
        String endTime = endTimeEditText.getText().toString().trim(); // Can be empty for rooms

        // Basic validation
        if (date.isEmpty()) {
            dateEditText.setError(getString(R.string.error_date_required));
            return;
        }

        // NEW: Validate date is not in the past
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            Date selectedDateObj = sdf.parse(date);
            Date today = new Date(System.currentTimeMillis());
            if (selectedDateObj.before(today)) {
                dateEditText.setError("Date cannot be in the past.");
                return;
            }
        } catch (java.text.ParseException e) {
            e.printStackTrace();
            dateEditText.setError("Invalid date format. Please use YYYY-MM-DD.");
            return;
        }

        // Validate times for both rooms and activities
        if (startTime.isEmpty()) {
            if ("room".equals(itemType)) {
                startTimeEditText.setError("Check-in time is required");
            } else {
                startTimeEditText.setError(getString(R.string.error_start_time_required));
            }
            return;
        }
        if (endTime.isEmpty()) {
            if ("room".equals(itemType)) {
                endTimeEditText.setError("Check-out time is required");
            } else {
                endTimeEditText.setError(getString(R.string.error_end_time_required));
            }
            return;
        }
        
        // Validate time format (HH:MM) for both rooms and activities
        if (!isValidTimeFormat(startTime) || !isValidTimeFormat(endTime)) {
            if ("room".equals(itemType)) {
                startTimeEditText.setError("Invalid time format. Use HH:MM (e.g., 15:00).");
                endTimeEditText.setError("Invalid time format. Use HH:MM (e.g., 11:00).");
            } else {
                startTimeEditText.setError("Invalid time format. Use HH:MM (e.g., 09:00).");
                endTimeEditText.setError("Invalid time format. Use HH:MM (e.g., 11:00).");
            }
            return;
        }
        
        // Special validation for room check-in/check-out times
        if ("room".equals(itemType)) {
            // For rooms, check-out time can be the next day, so we need more flexible validation
            // Check-in should typically be afternoon (14:00-18:00) and check-out morning (08:00-12:00)
            if (isEndTimeBeforeStartTime(startTime, endTime)) {
                // This is actually OK for rooms - check-out next day
                // We could add a note or allow this
            }
        } else {
            // For activities, end time must be after start time on the same day
            if (isEndTimeBeforeStartTime(startTime, endTime)) {
                startTimeEditText.setError("Start time must be before end time.");
                endTimeEditText.setError("End time must be after start time.");
                return;
            }
        }

        // Get current user ID from session
        int currentUserId = AuthActivity.getCurrentUserId(this);
        if (currentUserId == -1) {
            // User not logged in, redirect to auth
            runOnUiThread(() -> {
                Toast.makeText(BookingActivity.this, "Please log in to make a booking.", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(this, AuthActivity.class);
                startActivity(intent);
                finish();
            });
            return;
        }

        // NEW: Show progress bar and disable button
        progressBarBooking.setVisibility(android.view.View.VISIBLE);
        bookButton.setEnabled(false);

        new Thread(() -> {
            // Ensure the logged-in user still exists in the database (e.g., after schema reset)
            UserEntity existingUser = userDao.getUserById(currentUserId);
            if (existingUser == null) {
                runOnUiThread(() -> {
                    progressBarBooking.setVisibility(android.view.View.GONE);
                    bookButton.setEnabled(true);
                    Toast.makeText(BookingActivity.this, "Session expired. Please log in again.", Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(BookingActivity.this, AuthActivity.class);
                    startActivity(intent);
                    finish();
                });
                return;
            }

            // Check for existing bookings for the *same item* on the *same date*
            List<BookingEntity> existingBookings = bookingDao.getBookingsByItemAndDate(itemId, itemType, date);

            // For simplicity, just check if any booking exists for this item on this date for this user
            // In a real app, you'd check for time conflicts too if applicable (e.g., for activities)
            boolean hasConflict = false;
            for (BookingEntity existingBooking : existingBookings) {
                if (existingBooking.getUserId() == currentUserId) { // Check if current user already booked
                    // Check time conflict if times are provided (for activities)
                    if ("activity".equals(itemType) && existingBooking.getStartTime() != null && existingBooking.getEndTime() != null) {
                        // Basic overlap check (simplified, assumes HH:MM format)
                        // startTime/endTime are user inputs, existingBooking.getStartTime/EndTime are from DB
                        if (isTimeOverlapping(startTime, endTime, existingBooking.getStartTime(), existingBooking.getEndTime())) {
                            hasConflict = true;
                            break;
                        }
                    } else {
                        // If it's a room, or no times provided, just check if user already booked the item for that date
                        hasConflict = true;
                        break;
                    }
                }
            }

            if (hasConflict) {
                runOnUiThread(() -> {
                    // NEW: Hide progress bar and re-enable button
                    progressBarBooking.setVisibility(android.view.View.GONE);
                    bookButton.setEnabled(true);

                    String conflictMessage = "You already have a booking for this " + itemType + " on " + date;
                    if ("activity".equals(itemType)) {
                        conflictMessage += " at the selected time.";
                    } else {
                        conflictMessage += ".";
                    }
                    Toast.makeText(BookingActivity.this, conflictMessage, Toast.LENGTH_LONG).show();
                });
            } else {
                // No conflict, proceed with booking
                try {
                    BookingEntity newBooking = new BookingEntity(currentUserId, itemId, itemType, date);
                    newBooking.setStartTime(startTime.isEmpty() ? null : startTime); // Set null if empty
                    newBooking.setEndTime(endTime.isEmpty() ? null : endTime); // Set null if empty
                    // Status is set to "pending" by default in the constructor

                    // Check availability (this is a simplified check)
                    // In a real app, you'd check the booking table for conflicts against total capacity
                    // For now, we assume the item is available as per the detail screen check

                    bookingDao.insertBooking(newBooking);

                    runOnUiThread(() -> {
                        // NEW: Hide progress bar and re-enable button
                        progressBarBooking.setVisibility(android.view.View.GONE);
                        bookButton.setEnabled(true);

                        // Create confirmation message with times
                        String confirmationMessage;
                        if ("room".equals(itemType)) {
                            confirmationMessage = "Room booking confirmed for " + date + "\nCheck-in: " + startTime + "\nCheck-out: " + endTime;
                        } else {
                            confirmationMessage = "Activity booking confirmed for " + date + "\nTime: " + startTime + " - " + endTime;
                        }
                        Toast.makeText(BookingActivity.this, confirmationMessage, Toast.LENGTH_LONG).show();

                        // NEW: Send a local notification about the booking (with permission check)
                        sendBookingConfirmationNotification(date, itemType, startTime, endTime); // Add this call

                        // Optionally navigate back or to a confirmation screen
                        finish(); // Close booking activity
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                    runOnUiThread(() -> {
                        // NEW: Hide progress bar and re-enable button
                        progressBarBooking.setVisibility(android.view.View.GONE);
                        bookButton.setEnabled(true);

                        Toast.makeText(BookingActivity.this, getString(R.string.msg_booking_failed, e.getMessage()), Toast.LENGTH_LONG).show();
                    });
                }
            }
        }).start();
    }

    // NEW HELPER: Validate time format HH:MM
    private boolean isValidTimeFormat(String time) {
        return time.matches("^([0-1]?[0-9]|2[0-3]):[0-5][0-9]$");
    }

    // NEW HELPER: Check if end time is before start time
    private boolean isEndTimeBeforeStartTime(String startTime, String endTime) {
        // Assuming HH:MM format and valid input from isValidTimeFormat check
        String[] startParts = startTime.split(":");
        String[] endParts = endTime.split(":");
        int startHour = Integer.parseInt(startParts[0]);
        int startMin = Integer.parseInt(startParts[1]);
        int endHour = Integer.parseInt(endParts[0]);
        int endMin = Integer.parseInt(endParts[1]);

        if (endHour < startHour) {
            return true;
        } else if (endHour == startHour) {
            return endMin < startMin;
        }
        return false;
    }

    // NEW METHOD: Send a local notification (with permission check)
    private void sendBookingConfirmationNotification(String date, String itemType, String startTime, String endTime) {
        // Check if we have the POST_NOTIFICATIONS permission (Required for Android 13+)
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED) {
            // We have permission, proceed with notification
            createAndSendNotification(date, itemType, startTime, endTime);
        } else {
            // We don't have permission, request it
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.POST_NOTIFICATIONS},
                    REQUEST_POST_NOTIFICATIONS);
        }
    }

    // NEW METHOD: Helper method to create and send the notification
    private void createAndSendNotification(String date, String itemType, String startTime, String endTime) {
        // Intent to open MainActivity when notification is tapped
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_IMMUTABLE); // Use FLAG_IMMUTABLE for Android 6+

        // Create detailed content text with time information
        String contentText;
        if ("room".equals(itemType)) {
            contentText = "Your " + itemType + " booking for " + date + " is confirmed. Check-in: " + startTime + ", Check-out: " + endTime;
        } else {
            contentText = "Your " + itemType + " booking for " + date + " is confirmed. Time: " + startTime + " - " + endTime;
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "EcoStayChannelId") // Use the channel ID created in MainActivity
                .setSmallIcon(R.drawable.ic_notification_icon) // Add a notification icon to your drawable folder (e.g., ic_notification_icon.png)
                .setContentTitle("Booking Confirmed!")
                .setContentText(contentText)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(contentText)) // Allow longer text
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent) // Set the intent
                .setAutoCancel(true); // Dismiss notification when tapped

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);

        // notificationId is a unique int for the notification (required on older devices)
        int notificationId = 1; // You might want to generate a unique ID based on the booking
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        notificationManager.notify(notificationId, builder.build());
    }

    // NEW METHOD: Handle the permission request result
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_POST_NOTIFICATIONS) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, now send the notification
                // Note: The notification was triggered *before* the permission was granted.
                // The user will see the notification only if they grant the permission *before* the booking confirmation happens.
                // If they deny it, the notification won't be sent.
                // You could potentially store the booking details and send the notification later if permission is granted elsewhere.
                // For simplicity here, we just log or show a toast.
                Toast.makeText(this, "Notification permission granted.", Toast.LENGTH_SHORT).show();
            } else {
                // Permission denied, show a message
                Toast.makeText(this, "Notification permission denied. You won't receive booking confirmations.", Toast.LENGTH_LONG).show();
            }
        }
    }

    // Helper method to check time overlap (simplified HH:MM format)
    private boolean isTimeOverlapping(String start1, String end1, String start2, String end2) {
        // This is a basic string comparison and might not work perfectly for all time formats.
        // A more robust solution would parse times into minutes since midnight or use proper date/time libraries.
        // Example: "09:00" vs "10:00" vs "11:00"
        return start1.compareTo(end2) < 0 && end1.compareTo(start2) > 0;
    }
    
    // NEW: Method to show time picker dialog
    private void showTimePickerDialog(EditText timeEditText) {
        Calendar currentTime = Calendar.getInstance();
        int hour = currentTime.get(Calendar.HOUR_OF_DAY);
        int minute = currentTime.get(Calendar.MINUTE);
        
        // Check if there's already a time in the field
        String currentTimeText = timeEditText.getText().toString().trim();
        if (!currentTimeText.isEmpty() && isValidTimeFormat(currentTimeText)) {
            String[] timeParts = currentTimeText.split(":");
            hour = Integer.parseInt(timeParts[0]);
            minute = Integer.parseInt(timeParts[1]);
        }
        
        TimePickerDialog timePickerDialog = new TimePickerDialog(
                BookingActivity.this,
                (view, selectedHour, selectedMinute) -> {
                    String time = String.format(Locale.getDefault(), "%02d:%02d", selectedHour, selectedMinute);
                    timeEditText.setText(time);
                },
                hour, minute, true // Use 24-hour format
        );
        timePickerDialog.show();
    }
}
