package com.example.ecostayretreat.database;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "rooms")
public class RoomEntity {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "room_id")
    private int roomId;

    @ColumnInfo(name = "room_type")
    private String roomType;

    @ColumnInfo(name = "description")
    private String description;

    @ColumnInfo(name = "image_url")
    private String imageUrl;

    @ColumnInfo(name = "price_per_night")
    private double pricePerNight;

    @ColumnInfo(name = "is_available")
    private boolean isAvailable;

    @ColumnInfo(name = "capacity")
    private int capacity;

    // Constructors
    public RoomEntity() {}
    public RoomEntity(String roomType, String description, double pricePerNight, int capacity) {
        this.roomType = roomType;
        this.description = description;
        this.pricePerNight = pricePerNight;
        this.capacity = capacity;
        this.isAvailable = true; // Default to available
    }

    // Getters and Setters
    public int getRoomId() { return roomId; }
    public void setRoomId(int roomId) { this.roomId = roomId; }
    public String getRoomType() { return roomType; }
    public void setRoomType(String roomType) { this.roomType = roomType; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
    public double getPricePerNight() { return pricePerNight; }
    public void setPricePerNight(double pricePerNight) { this.pricePerNight = pricePerNight; }
    public boolean isAvailable() { return isAvailable; }
    public void setAvailable(boolean available) { isAvailable = available; }
    public int getCapacity() { return capacity; }
    public void setCapacity(int capacity) { this.capacity = capacity; }
}