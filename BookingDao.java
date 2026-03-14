package com.example.ecostayretreat.database;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;
import java.util.List;

@Dao
public interface BookingDao {
    @Query("SELECT * FROM bookings WHERE user_id = :userId")
    List<BookingEntity> getBookingsByUserId(int userId);

    @Query("SELECT * FROM bookings WHERE item_id = :itemId AND item_type = :itemType")
    List<BookingEntity> getBookingsByItem(int itemId, String itemType);

    @Query("SELECT * FROM bookings WHERE item_id = :itemId AND item_type = :itemType AND booking_date = :date")
    List<BookingEntity> getBookingsByItemAndDate(int itemId, String itemType, String date);

    @Insert
    void insertBooking(BookingEntity booking);

    @Update
    void updateBooking(BookingEntity booking);

    @androidx.room.Delete
    void deleteBooking(BookingEntity booking);

    @Query("SELECT * FROM bookings WHERE booking_id = :bookingId")
    BookingEntity getBookingById(int bookingId);
}
