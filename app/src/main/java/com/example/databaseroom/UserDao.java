package com.example.databaseroom;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface UserDao {
    @Query("SELECT * FROM users")
    List<User> getAllUsers();


    @Query("SELECT * FROM users WHERE id = :userId")
    User getUserById(int userId);

    @Insert
    void insertUser(User user);

    @Update
    void updateUser(User user);

    // Другие методы для обновления, удаления и т.д.
}



