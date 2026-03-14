package com.example.ecostayretreat;

import android.app.NotificationManager;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class NotificationsActivity extends AppCompatActivity {

    private TextView titleTextView;
    private ListView notificationsListView;
    private List<NotificationItem> notifications = new ArrayList<>();
    private NotificationAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notifications);

        titleTextView = findViewById(R.id.textViewNotificationsTitle);
        notificationsListView = findViewById(R.id.listViewNotifications);

        // Set up adapter
        adapter = new NotificationAdapter(this, R.layout.notification_list_item, notifications);
        notificationsListView.setAdapter(adapter);

        loadNotifications();
        
        // Set up click listener
        notificationsListView.setOnItemClickListener((parent, view, position, id) -> {
            NotificationItem selectedNotification = notifications.get(position);
            handleNotificationClick(selectedNotification);
        });
    }

    private void loadNotifications() {
        // Clear existing notifications
        notifications.clear();

        // Add eco-friendly events and special offers
        loadEcoFriendlyEvents();
        loadSpecialOffers();
        loadSustainabilityDiscounts();
        loadSeasonalPromotions();

        adapter.notifyDataSetChanged();
        
        titleTextView.setText("Notifications (" + notifications.size() + ")");
    }

    private void loadEcoFriendlyEvents() {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd", Locale.getDefault());

        // This weekend's eco-tour
        calendar.add(Calendar.DAY_OF_MONTH, 2);
        notifications.add(new NotificationItem(
            "Special Eco-Tour This Weekend",
            "Join our guided eco-tour on " + dateFormat.format(calendar.getTime()) + 
            ". Learn about local flora and sustainable practices!",
            "eco_event",
            calendar.getTimeInMillis(),
            false
        ));

        // Bird watching workshop
        calendar.add(Calendar.DAY_OF_MONTH, 5);
        notifications.add(new NotificationItem(
            "Bird Watching Workshop",
            "Advanced bird watching techniques workshop on " + dateFormat.format(calendar.getTime()) + 
            ". Binoculars provided!",
            "eco_event",
            calendar.getTimeInMillis(),
            false
        ));

        // Sustainability workshop
        calendar.add(Calendar.DAY_OF_MONTH, 3);
        notifications.add(new NotificationItem(
            "Sustainability Workshop",
            "Learn to create your own eco-friendly products on " + dateFormat.format(calendar.getTime()) + 
            ". All materials included.",
            "eco_event",
            calendar.getTimeInMillis(),
            false
        ));
    }

    private void loadSpecialOffers() {
        notifications.add(new NotificationItem(
            "Early Bird Special - 25% Off",
            "Book your eco-pod accommodation 30 days in advance and save 25%! " +
            "Valid for stays until end of next month.",
            "special_offer",
            System.currentTimeMillis(),
            true
        ));

        notifications.add(new NotificationItem(
            "Weekend Getaway Package",
            "2-night weekend package including meals, activities, and spa treatments. " +
            "Starting from $299 per person.",
            "special_offer",
            System.currentTimeMillis() - 24 * 60 * 60 * 1000, // Yesterday
            true
        ));

        notifications.add(new NotificationItem(
            "Family Adventure Package",
            "Special family rates for groups of 4+. Includes kids' nature activities " +
            "and family room upgrades.",
            "special_offer",
            System.currentTimeMillis() - 2 * 24 * 60 * 60 * 1000, // 2 days ago
            false
        ));
    }

    private void loadSustainabilityDiscounts() {
        notifications.add(new NotificationItem(
            "Eco-Warrior Discount",
            "Show proof of using renewable energy at home and get 15% off your stay! " +
            "Bringing reusable items gets you another 5% off.",
            "sustainability_discount",
            System.currentTimeMillis(),
            true
        ));

        notifications.add(new NotificationItem(
            "Carbon-Neutral Travel Reward",
            "Arrived by bike, electric vehicle, or public transport? " +
            "Enjoy 20% off all activities during your stay!",
            "sustainability_discount",
            System.currentTimeMillis() - 12 * 60 * 60 * 1000, // 12 hours ago
            false
        ));

        notifications.add(new NotificationItem(
            "Green Certification Bonus",
            "Guests with environmental certifications or memberships in " +
            "green organizations get exclusive access to VIP eco-tours.",
            "sustainability_discount",
            System.currentTimeMillis() - 36 * 60 * 60 * 1000, // 36 hours ago
            false
        ));
    }

    private void loadSeasonalPromotions() {
        Calendar calendar = Calendar.getInstance();
        int month = calendar.get(Calendar.MONTH);
        
        if (month >= Calendar.SEPTEMBER && month <= Calendar.NOVEMBER) {
            // Fall promotions
            notifications.add(new NotificationItem(
                "Autumn Leaf Festival",
                "Join us for guided autumn hikes and leaf identification workshops. " +
                "Hot cocoa and eco-friendly snacks included!",
                "seasonal_promo",
                System.currentTimeMillis(),
                true
            ));
        } else if (month >= Calendar.DECEMBER || month <= Calendar.FEBRUARY) {
            // Winter promotions
            notifications.add(new NotificationItem(
                "Winter Wonderland Package",
                "Experience sustainable winter activities including snowshoeing " +
                "and wildlife tracking. Warm eco-friendly accommodations.",
                "seasonal_promo",
                System.currentTimeMillis(),
                true
            ));
        } else if (month >= Calendar.MARCH && month <= Calendar.MAY) {
            // Spring promotions
            notifications.add(new NotificationItem(
                "Spring Renewal Retreat",
                "Celebrate Earth Day with special planting activities and " +
                "garden-to-table dining experiences.",
                "seasonal_promo",
                System.currentTimeMillis(),
                true
            ));
        } else {
            // Summer promotions
            notifications.add(new NotificationItem(
                "Summer Solar Special",
                "Learn about solar power while enjoying extended daylight activities. " +
                "Solar-powered cabin upgrades available!",
                "seasonal_promo",
                System.currentTimeMillis(),
                true
            ));
        }
    }

    private void handleNotificationClick(NotificationItem notification) {
        switch (notification.getType()) {
            case "eco_event":
                // Navigate to activities list or calendar
                Intent activityIntent = new Intent(this, ActivityCalendarActivity.class);
                startActivity(activityIntent);
                break;
                
            case "special_offer":
            case "sustainability_discount":
                // Navigate to rooms list to book with discount
                Intent roomIntent = new Intent(this, RoomListActivity.class);
                startActivity(roomIntent);
                break;
                
            case "seasonal_promo":
                // Navigate to green initiatives for more info
                Intent greenIntent = new Intent(this, GreenInitiativesActivity.class);
                startActivity(greenIntent);
                break;
                
            default:
                Toast.makeText(this, "More details coming soon!", Toast.LENGTH_SHORT).show();
                break;
        }

        // Mark as read
        notification.setRead(true);
        adapter.notifyDataSetChanged();
    }

    // Inner class for notification items
    public static class NotificationItem {
        private String title;
        private String message;
        private String type;
        private long timestamp;
        private boolean isHighPriority;
        private boolean isRead;

        public NotificationItem(String title, String message, String type, long timestamp, boolean isHighPriority) {
            this.title = title;
            this.message = message;
            this.type = type;
            this.timestamp = timestamp;
            this.isHighPriority = isHighPriority;
            this.isRead = false;
        }

        // Getters and setters
        public String getTitle() { return title; }
        public String getMessage() { return message; }
        public String getType() { return type; }
        public long getTimestamp() { return timestamp; }
        public boolean isHighPriority() { return isHighPriority; }
        public boolean isRead() { return isRead; }
        public void setRead(boolean read) { isRead = read; }
    }

    // Simple adapter for notifications (you might want to create a separate file for this)
    private static class NotificationAdapter extends android.widget.ArrayAdapter<NotificationItem> {
        private android.content.Context context;
        private List<NotificationItem> notifications;

        public NotificationAdapter(android.content.Context context, int resource, List<NotificationItem> notifications) {
            super(context, resource, notifications);
            this.context = context;
            this.notifications = notifications;
        }

        @Override
        public android.view.View getView(int position, android.view.View convertView, android.view.ViewGroup parent) {
            if (convertView == null) {
                convertView = android.view.LayoutInflater.from(context).inflate(R.layout.notification_list_item, parent, false);
            }

            NotificationItem notification = notifications.get(position);
            
            TextView titleView = convertView.findViewById(R.id.textViewNotificationTitle);
            TextView messageView = convertView.findViewById(R.id.textViewNotificationMessage);
            TextView timestampView = convertView.findViewById(R.id.textViewNotificationTime);

            titleView.setText(notification.getTitle());
            messageView.setText(notification.getMessage());
            
            // Format timestamp
            SimpleDateFormat timeFormat = new SimpleDateFormat("MMM dd, HH:mm", Locale.getDefault());
            timestampView.setText(timeFormat.format(notification.getTimestamp()));

            // Style based on read status and priority
            if (!notification.isRead()) {
                convertView.setBackgroundColor(0xFFE3F2FD); // Light blue for unread
                titleView.setTypeface(null, android.graphics.Typeface.BOLD);
            } else {
                convertView.setBackgroundColor(0xFFFFFFFF); // White for read
                titleView.setTypeface(null, android.graphics.Typeface.NORMAL);
            }

            if (notification.isHighPriority()) {
                titleView.setTextColor(0xFF1976D2); // Blue for high priority
            } else {
                titleView.setTextColor(0xFF424242); // Gray for normal
            }

            return convertView;
        }
    }
}