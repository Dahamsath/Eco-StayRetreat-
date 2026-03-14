package com.example.ecostayretreat.database;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;
import java.util.List;

@Dao
public interface RoomDao {
    @Query("SELECT * FROM rooms WHERE is_available = 1")
    List<RoomEntity> getAllAvailableRooms();

    @Query("SELECT * FROM rooms WHERE is_available = 1 ORDER BY room_id ASC")
    List<RoomEntity> getAllAvailableRoomsOrderedById();

    @Query("SELECT * FROM rooms WHERE room_type LIKE '%' || :filter || '%' AND is_available = 1")
    List<RoomEntity> getFilteredAvailableRooms(String filter);

    @Query("SELECT * FROM rooms WHERE room_id = :roomId")
    RoomEntity getRoomById(int roomId);

    @Update
    void updateRoom(RoomEntity room);

    @Insert
    void insertRoom(RoomEntity room);

    @Delete
    void deleteRoom(RoomEntity room);

    @Query("SELECT * FROM rooms WHERE is_available = 1 AND price_per_night BETWEEN :minPrice AND :maxPrice")
    List<RoomEntity> getAvailableRoomsWithinPriceRange(double minPrice, double maxPrice);
}
