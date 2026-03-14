package com.example.ecostayretreat;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.example.ecostayretreat.database.AppDatabase;
import com.example.ecostayretreat.database.UserDao;
import com.example.ecostayretreat.database.UserEntity;
import com.example.ecostayretreat.database.RoomDao;
import com.example.ecostayretreat.database.RoomEntity;
import com.example.ecostayretreat.database.ActivityDao;
import com.example.ecostayretreat.database.ActivityEntity;
import com.example.ecostayretreat.database.BookingDao;
import com.example.ecostayretreat.database.BookingEntity;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class RecommendationsActivity extends AppCompatActivity {

    private TextView welcomeTextView, recommendationsSummaryTextView;
    private ListView recommendedRoomsListView, recommendedActivitiesListView;
    private AppDatabase database;
    private UserDao userDao;
    private RoomDao roomDao;
    private ActivityDao activityDao;
    private BookingDao bookingDao;
    
    private int currentUserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recommendations);
        
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
        roomDao = database.roomDao();
        activityDao = database.activityDao();
        bookingDao = database.bookingDao();

        welcomeTextView = findViewById(R.id.textViewWelcome);
        recommendationsSummaryTextView = findViewById(R.id.textViewRecommendationsSummary);
        recommendedRoomsListView = findViewById(R.id.listViewRecommendedRooms);
        recommendedActivitiesListView = findViewById(R.id.listViewRecommendedActivities);

        loadPersonalizedRecommendations();
    }

    private void loadPersonalizedRecommendations() {
        new Thread(() -> {
            UserEntity user = userDao.getUserById(currentUserId);
            List<RoomEntity> allRooms = roomDao.getAllAvailableRooms();
            List<ActivityEntity> allActivities = activityDao.getAllAvailableActivities();
            List<BookingEntity> userBookings = bookingDao.getBookingsByUserId(currentUserId);

            if (user != null) {
                // Analyze user preferences and booking history for personalized recommendations
                List<RoomEntity> recommendedRooms = getEcoFriendlyRecommendedRooms(user, allRooms, userBookings);
                List<ActivityEntity> recommendedActivities = getSustainableRecommendedActivities(user, allActivities, userBookings);

                runOnUiThread(() -> {
                    welcomeTextView.setText("Welcome back, " + user.getName() + "!");
                    
                    String summary = generateRecommendationsSummary(user, recommendedRooms.size(), recommendedActivities.size());
                    recommendationsSummaryTextView.setText(summary);

                    // Set up room recommendations
                    RoomAdapter roomAdapter = new RoomAdapter(this, R.layout.room_list_item, recommendedRooms);
                    recommendedRoomsListView.setAdapter(roomAdapter);
                    
                    // Set up activity recommendations
                    ActivityAdapter activityAdapter = new ActivityAdapter(this, R.layout.activity_list_item, recommendedActivities);
                    recommendedActivitiesListView.setAdapter(activityAdapter);

                    // Set click listeners for booking
                    recommendedRoomsListView.setOnItemClickListener((parent, view, position, id) -> {
                        RoomEntity selectedRoom = recommendedRooms.get(position);
                        Intent intent = new Intent(RecommendationsActivity.this, RoomDetailActivity.class);
                        intent.putExtra("ROOM_ID", selectedRoom.getRoomId());
                        startActivity(intent);
                    });

                    recommendedActivitiesListView.setOnItemClickListener((parent, view, position, id) -> {
                        ActivityEntity selectedActivity = recommendedActivities.get(position);
                        Intent intent = new Intent(RecommendationsActivity.this, ActivityDetailActivity.class);
                        intent.putExtra("ACTIVITY_ID", selectedActivity.getActivityId());
                        startActivity(intent);
                    });
                });
            }
        }).start();
    }

    private List<RoomEntity> getEcoFriendlyRecommendedRooms(UserEntity user, List<RoomEntity> allRooms, List<BookingEntity> userBookings) {
        List<RoomEntity> recommendations = new ArrayList<>();
        String preferences = user.getPreferences();
        String preferredRoomType = user.getPreferredRoomType();
        int sustainabilityLevel = user.getSustainabilityLevel();
        
        // Create scoring system for recommendations
        List<RoomScore> roomScores = new ArrayList<>();
        
        for (RoomEntity room : allRooms) {
            int score = 0;
            String roomType = room.getRoomType().toLowerCase();
            String description = room.getDescription().toLowerCase();
            
            // Eco-friendly bonus points
            if (roomType.contains("eco") || description.contains("eco") || description.contains("sustainable")) {
                score += 50;
            }
            
            // User preference matching
            if (preferredRoomType != null && !preferredRoomType.isEmpty()) {
                if (roomType.contains(preferredRoomType.toLowerCase())) {
                    score += 40;
                }
            }
            
            // Traditional preference matching
            if (preferences != null && !preferences.isEmpty()) {
                String lowerPrefs = preferences.toLowerCase();
                if (lowerPrefs.contains("mountain") && (roomType.contains("mountain") || description.contains("mountain"))) {
                    score += 30;
                }
                if (lowerPrefs.contains("pod") && roomType.contains("pod")) {
                    score += 30;
                }
                if (lowerPrefs.contains("cabin") && roomType.contains("cabin")) {
                    score += 30;
                }
            }
            
            // Sustainability level matching
            if (sustainabilityLevel >= 4 && (roomType.contains("eco") || roomType.contains("solar") || description.contains("green"))) {
                score += 35;
            }
            
            // Booking history influence
            for (BookingEntity booking : userBookings) {
                if (booking.getItemType().equals("room") && booking.getItemId() != room.getRoomId()) {
                    // Look for similar room types the user has booked before
                    // This would require fetching the previously booked room, simplified here
                    score += 10; // Small bonus for being a returning customer
                }
            }
            
            // Price consideration - prefer mid-range for eco-conscious users
            if (room.getPricePerNight() >= 100 && room.getPricePerNight() <= 200) {
                score += 15;
            }
            
            roomScores.add(new RoomScore(room, score));
        }
        
        // Sort by score and return top recommendations
        roomScores.sort((a, b) -> Integer.compare(b.score, a.score));
        
        for (RoomScore roomScore : roomScores) {
            if (recommendations.size() >= 5) break;
            recommendations.add(roomScore.room);
        }
        
        // Ensure we have at least 3 recommendations
        if (recommendations.size() < 3) {
            for (RoomEntity room : allRooms) {
                if (recommendations.size() >= 3) break;
                if (!recommendations.contains(room)) {
                    recommendations.add(room);
                }
            }
        }
        
        return recommendations;
    }

    private List<ActivityEntity> getSustainableRecommendedActivities(UserEntity user, List<ActivityEntity> allActivities, List<BookingEntity> userBookings) {
        List<ActivityEntity> recommendations = new ArrayList<>();
        String preferences = user.getPreferences();
        String ecoActivityPreferences = user.getEcoActivityPreferences();
        int sustainabilityLevel = user.getSustainabilityLevel();
        
        // Create scoring system for activity recommendations
        List<ActivityScore> activityScores = new ArrayList<>();
        
        for (ActivityEntity activity : allActivities) {
            int score = 0;
            String activityName = activity.getActivityName().toLowerCase();
            String description = activity.getDescription().toLowerCase();
            
            // Eco-friendly activity bonus points
            if (activityName.contains("eco") || description.contains("eco") || 
                activityName.contains("sustainable") || description.contains("sustainable") ||
                activityName.contains("nature") || description.contains("conservation")) {
                score += 50;
            }
            
            // High sustainability users prefer educational activities
            if (sustainabilityLevel >= 4) {
                if (activityName.contains("workshop") || activityName.contains("tour") || 
                    description.contains("learn") || description.contains("educational")) {
                    score += 40;
                }
            }
            
            // Traditional preference matching with eco-focus
            if (preferences != null && !preferences.isEmpty()) {
                String lowerPrefs = preferences.toLowerCase();
                if (lowerPrefs.contains("hike") && (activityName.contains("hike") || description.contains("hike"))) {
                    score += 35;
                }
                if (lowerPrefs.contains("bird") && (activityName.contains("bird") || description.contains("bird"))) {
                    score += 35;
                }
                if (lowerPrefs.contains("workshop") && activityName.contains("workshop")) {
                    score += 30;
                }
                if (lowerPrefs.contains("tour") && activityName.contains("tour")) {
                    score += 30;
                }
            }
            
            // Eco-activity preferences (if available)
            if (ecoActivityPreferences != null && !ecoActivityPreferences.isEmpty()) {
                // In a real app, this would parse JSON preferences
                if (ecoActivityPreferences.contains(activityName.substring(0, Math.min(activityName.length(), 10)))) {
                    score += 45;
                }
            }
            
            // Booking history influence
            for (BookingEntity booking : userBookings) {
                if (booking.getItemType().equals("activity") && booking.getItemId() != activity.getActivityId()) {
                    score += 15; // Bonus for returning activity participant
                }
            }
            
            // Prefer outdoor and nature-based activities for eco-conscious users
            if (activityName.contains("outdoor") || description.contains("outdoor") ||
                activityName.contains("nature") || description.contains("wildlife")) {
                score += 25;
            }
            
            activityScores.add(new ActivityScore(activity, score));
        }
        
        // Sort by score and return top recommendations
        activityScores.sort((a, b) -> Integer.compare(b.score, a.score));
        
        for (ActivityScore activityScore : activityScores) {
            if (recommendations.size() >= 5) break;
            recommendations.add(activityScore.activity);
        }
        
        // Ensure we have at least 3 recommendations
        if (recommendations.size() < 3) {
            for (ActivityEntity activity : allActivities) {
                if (recommendations.size() >= 3) break;
                if (!recommendations.contains(activity)) {
                    recommendations.add(activity);
                }
            }
        }
        
        return recommendations;
    }

    private String generateRecommendationsSummary(UserEntity user, int roomCount, int activityCount) {
        StringBuilder summary = new StringBuilder();
        summary.append("Based on your preferences");
        
        if (user.getPreferences() != null && !user.getPreferences().isEmpty()) {
            summary.append(" (").append(user.getPreferences()).append(")");
        }
        
        summary.append(", we've found ").append(roomCount).append(" recommended rooms and ")
                .append(activityCount).append(" activities for you.");
        
        if (user.getTravelDates() != null && !user.getTravelDates().isEmpty()) {
            summary.append("\n\nFor your planned travel dates: ").append(user.getTravelDates());
        }
        
        summary.append("\n\nTap any item below to view details and book!");
        
        return summary.toString();
    }
    
    // Helper classes for scoring recommendations
    private static class RoomScore {
        RoomEntity room;
        int score;
        
        RoomScore(RoomEntity room, int score) {
            this.room = room;
            this.score = score;
        }
    }
    
    private static class ActivityScore {
        ActivityEntity activity;
        int score;
        
        ActivityScore(ActivityEntity activity, int score) {
            this.activity = activity;
            this.score = score;
        }
    }
}
