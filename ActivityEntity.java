package com.example.ecostayretreat.database;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "activities")
public class ActivityEntity {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "activity_id")
    private int activityId;

    @ColumnInfo(name = "activity_name")
    private String activityName;

    @ColumnInfo(name = "description")
    private String description;

    @ColumnInfo(name = "image_url")
    private String imageUrl;

    @ColumnInfo(name = "price")
    private double price;

    @ColumnInfo(name = "duration")
    private String duration;

    @ColumnInfo(name = "schedule")
    private String schedule;

    @ColumnInfo(name = "is_available")
    private boolean isAvailable;

    // Constructors
    public ActivityEntity() {}
    public ActivityEntity(String activityName, String description, double price, String duration, String schedule) {
        this.activityName = activityName;
        this.description = description;
        this.price = price;
        this.duration = duration;
        this.schedule = schedule;
        this.isAvailable = true; // Default to available
    }

    // Getters and Setters
    public int getActivityId() { return activityId; }
    public void setActivityId(int activityId) { this.activityId = activityId; }
    public String getActivityName() { return activityName; }
    public void setActivityName(String activityName) { this.activityName = activityName; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }
    public String getDuration() { return duration; }
    public void setDuration(String duration) { this.duration = duration; }
    public String getSchedule() { return schedule; }
    public void setSchedule(String schedule) { this.schedule = schedule; }
    public boolean isAvailable() { return isAvailable; }
    public void setAvailable(boolean available) { isAvailable = available; }
}