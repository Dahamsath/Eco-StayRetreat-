package com.example.ecostayretreat;

import android.content.Intent;
import android.os.Bundle;
import android.widget.CalendarView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.ecostayretreat.database.AppDatabase;
import com.example.ecostayretreat.database.ActivityDao;
import com.example.ecostayretreat.database.ActivityEntity;
import com.example.ecostayretreat.database.BookingDao;
import com.example.ecostayretreat.database.BookingEntity;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ActivityCalendarActivity extends AppCompatActivity {

    private CalendarView calendarView;
    private TextView selectedDateTextView, availabilityTextView;
    private ListView activitiesForDateListView;
    private AppDatabase database;
    private ActivityDao activityDao;
    private BookingDao bookingDao;
    
    private String selectedDate = "";
    private List<ActivityEntity> activitiesForSelectedDate = new ArrayList<>();
    private ActivityAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar);

        database = AppDatabase.getDatabase(this);
        activityDao = database.activityDao();
        bookingDao = database.bookingDao();

        calendarView = findViewById(R.id.calendarView);
        selectedDateTextView = findViewById(R.id.textViewSelectedDate);
        availabilityTextView = findViewById(R.id.textViewAvailability);
        activitiesForDateListView = findViewById(R.id.listViewActivitiesForDate);

        // Set up adapter
        adapter = new ActivityAdapter(this, R.layout.activity_list_item, activitiesForSelectedDate);
        activitiesForDateListView.setAdapter(adapter);

        // Set initial selected date to today
        Calendar today = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        selectedDate = sdf.format(today.getTime());
        selectedDateTextView.setText("Selected Date: " + selectedDate);
        
        loadActivitiesForDate(selectedDate);

        // Set up calendar date change listener
        calendarView.setOnDateChangeListener((view, year, month, dayOfMonth) -> {
            Calendar selectedCalendar = Calendar.getInstance();
            selectedCalendar.set(year, month, dayOfMonth);
            selectedDate = sdf.format(selectedCalendar.getTime());
            
            selectedDateTextView.setText("Selected Date: " + selectedDate);
            loadActivitiesForDate(selectedDate);
        });

        // Set up click listener for activities
        activitiesForDateListView.setOnItemClickListener((parent, view, position, id) -> {
            ActivityEntity selectedActivity = activitiesForSelectedDate.get(position);
            
            // Navigate to booking activity with pre-selected date
            Intent intent = new Intent(ActivityCalendarActivity.this, BookingActivity.class);
            intent.putExtra("ITEM_ID", selectedActivity.getActivityId());
            intent.putExtra("ITEM_TYPE", "activity");
            intent.putExtra("SELECTED_DATE", selectedDate); // Pass the selected date
            startActivity(intent);
        });
    }

    private void loadActivitiesForDate(String date) {
        new Thread(() -> {
            // Get all available activities
            List<ActivityEntity> allActivities = activityDao.getAllAvailableActivities();
            
            // Get existing bookings for this date
            List<BookingEntity> bookingsForDate = new ArrayList<>();
            for (ActivityEntity activity : allActivities) {
                List<BookingEntity> activityBookings = bookingDao.getBookingsByItemAndDate(
                    activity.getActivityId(), "activity", date);
                bookingsForDate.addAll(activityBookings);
            }
            
            // Create availability information
            StringBuilder availabilityInfo = new StringBuilder();
            availabilityInfo.append("Activity Availability for ").append(date).append(":\n\n");
            
            List<ActivityEntity> availableActivities = new ArrayList<>();
            
            for (ActivityEntity activity : allActivities) {
                // Check if activity has any bookings on this date
                long bookingsCount = bookingsForDate.stream()
                    .filter(booking -> booking.getItemId() == activity.getActivityId())
                    .count();
                
                // For this example, assume activities can have multiple bookings (group activities)
                // In a real scenario, you might have capacity limits
                String activityStatus;
                if (bookingsCount == 0) {
                    activityStatus = "Available";
                    availableActivities.add(activity);
                } else if (bookingsCount < 5) { // Assume max capacity of 5 for group activities
                    activityStatus = "Available (" + (5 - bookingsCount) + " spots left)";
                    availableActivities.add(activity);
                } else {
                    activityStatus = "Fully Booked";
                }
                
                availabilityInfo.append("• ").append(activity.getActivityName())
                    .append(": ").append(activityStatus).append("\n");
            }
            
            final String finalAvailabilityInfo = availabilityInfo.toString();
            
            runOnUiThread(() -> {
                availabilityTextView.setText(finalAvailabilityInfo);
                
                // Update the activities list to show only available activities
                activitiesForSelectedDate.clear();
                activitiesForSelectedDate.addAll(availableActivities);
                adapter.notifyDataSetChanged();
                
                if (availableActivities.isEmpty()) {
                    Toast.makeText(ActivityCalendarActivity.this, 
                        "No activities available on " + date, Toast.LENGTH_SHORT).show();
                }
            });
        }).start();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Refresh data when returning from booking activity
        if (!selectedDate.isEmpty()) {
            loadActivitiesForDate(selectedDate);
        }
    }
}