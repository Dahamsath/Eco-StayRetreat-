package com.example.ecostayretreat.database;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

@Dao
public interface UserDao {
    @Query("SELECT * FROM users WHERE email = :email AND password = :password")
    UserEntity getUserByEmailAndPassword(String email, String password);

    @Query("SELECT * FROM users WHERE email = :email")
    UserEntity getUserByEmail(String email);

    @Insert
    void insertUser(UserEntity user);

    @Update
    void updateUser(UserEntity user);

    @Delete
    void deleteUser(UserEntity user);

    @Query("SELECT * FROM users WHERE user_id = :userId")
    UserEntity getUserById(int userId);
    
    @Query("SELECT * FROM users")
    java.util.List<UserEntity> getAllUsers();
}
