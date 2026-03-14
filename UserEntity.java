package com.example.ecostayretreat.database;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(tableName = "users",
        indices = {@Index(value = {"email"}, unique = true)}) // <-- Define unique constraint here
public class UserEntity {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "user_id")
    private int userId;

    @ColumnInfo(name = "email") // Remove 'unique = true' from here
    private String email;

    @ColumnInfo(name = "name")
    private String name;

    @ColumnInfo(name = "password") // Note: Storing plain passwords is insecure in real apps
    private String password;

    @ColumnInfo(name = "preferences")
    private String preferences;

    @ColumnInfo(name = "travel_dates")
    private String travelDates;
    
    @ColumnInfo(name = "preferred_room_type")
    private String preferredRoomType; // "Mountain-View", "Eco-Pod", "Standard"
    
    @ColumnInfo(name = "eco_activity_preferences")
    private String ecoActivityPreferences; // JSON string of preferred activities
    
    @ColumnInfo(name = "sustainability_level")
    private int sustainabilityLevel; // 1-5 scale of eco-consciousness
    
    @ColumnInfo(name = "notification_preferences")
    private String notificationPreferences; // JSON string of notification settings

    // Constructors
    public UserEntity() {}
    public UserEntity(String email, String name, String password) {
        this.email = email;
        this.name = name;
        this.password = password;
    }

    // Getters and Setters
    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public String getPreferences() { return preferences; }
    public void setPreferences(String preferences) { this.preferences = preferences; }
    public String getTravelDates() { return travelDates; }
    public void setTravelDates(String travelDates) { this.travelDates = travelDates; }
    
    public String getPreferredRoomType() { return preferredRoomType; }
    public void setPreferredRoomType(String preferredRoomType) { this.preferredRoomType = preferredRoomType; }
    
    public String getEcoActivityPreferences() { return ecoActivityPreferences; }
    public void setEcoActivityPreferences(String ecoActivityPreferences) { this.ecoActivityPreferences = ecoActivityPreferences; }
    
    public int getSustainabilityLevel() { return sustainabilityLevel; }
    public void setSustainabilityLevel(int sustainabilityLevel) { this.sustainabilityLevel = sustainabilityLevel; }
    
    public String getNotificationPreferences() { return notificationPreferences; }
    public void setNotificationPreferences(String notificationPreferences) { this.notificationPreferences = notificationPreferences; }
}
