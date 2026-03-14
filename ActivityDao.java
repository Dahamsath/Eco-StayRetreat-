package com.example.ecostayretreat.database;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;
import java.util.List;

@Dao
public interface ActivityDao {
    @Query("SELECT * FROM activities WHERE is_available = 1")
    List<ActivityEntity> getAllAvailableActivities();

    @Query("SELECT * FROM activities WHERE is_available = 1 ORDER BY activity_id ASC")
    List<ActivityEntity> getAllAvailableActivitiesOrderedById();

    @Query("SELECT * FROM activities WHERE activity_name LIKE '%' || :filter || '%' AND is_available = 1")
    List<ActivityEntity> getFilteredAvailableActivities(String filter);

    @Query("SELECT * FROM activities WHERE activity_id = :activityId")
    ActivityEntity getActivityById(int activityId);

    @Update
    void updateActivity(ActivityEntity activity);

    @Insert
    void insertActivity(ActivityEntity activity);

    @androidx.room.Delete
    void deleteActivity(ActivityEntity activity);
}
