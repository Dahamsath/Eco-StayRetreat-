package com.example.ecostayretreat.database;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

@Entity(tableName = "bookings",
        foreignKeys = {
                @ForeignKey(entity = UserEntity.class, parentColumns = "user_id", childColumns = "user_id", onDelete = ForeignKey.CASCADE)
        })
public class BookingEntity {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "booking_id")
    private int bookingId;

    @ColumnInfo(name = "user_id", index = true) // Index for faster queries
    private int userId; // Foreign key

    @ColumnInfo(name = "item_id") // Could be roomId or activityId depending on booking type
    private int itemId;

    @ColumnInfo(name = "item_type") // "room" or "activity"
    private String itemType;

    @ColumnInfo(name = "booking_date")
    private String bookingDate; // e.g., "2024-03-15"

    @ColumnInfo(name = "start_time") // For activities
    private String startTime;

    @ColumnInfo(name = "end_time") // For activities
    private String endTime;

    @ColumnInfo(name = "status") // e.g., "confirmed", "pending", "cancelled"
    private String status;

    // Constructors
    public BookingEntity() {}
    public BookingEntity(int userId, int itemId, String itemType, String bookingDate) {
        this.userId = userId;
        this.itemId = itemId;
        this.itemType = itemType;
        this.bookingDate = bookingDate;
        this.status = "pending"; // Default status
    }

    // Getters and Setters
    public int getBookingId() { return bookingId; }
    public void setBookingId(int bookingId) { this.bookingId = bookingId; }
    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }
    public int getItemId() { return itemId; }
    public void setItemId(int itemId) { this.itemId = itemId; }
    public String getItemType() { return itemType; }
    public void setItemType(String itemType) { this.itemType = itemType; }
    public String getBookingDate() { return bookingDate; }
    public void setBookingDate(String bookingDate) { this.bookingDate = bookingDate; }
    public String getStartTime() { return startTime; }
    public void setStartTime(String startTime) { this.startTime = startTime; }
    public String getEndTime() { return endTime; }
    public void setEndTime(String endTime) { this.endTime = endTime; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}