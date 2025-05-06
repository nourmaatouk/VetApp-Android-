package com.example.vetapp;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
/**
 * Data Access Object (DAO) interface for the User entity.
 * This interface defines the database operations that can be performed
 * on the 'users' table using Room ORM.
 */
@Dao
public interface UserDao {
    /**
     * Inserts a new user into the 'users' table.
     *
     * @param user The User object to insert.
     */
    @Insert
    void insert(User user);
    /**
     * Authenticates a user by checking if a user exists
     * in the database with the provided username and password.
     *
     * @param username The username to match.
     * @param password The password to match.
     * @return The matching User object if found, otherwise null.
     */
    @Query("SELECT * FROM users WHERE username = :username AND password = :password")
    User login(String username, String password);
    /**
     * Searches for a user by their username.
     * This is useful for checking if a username already exists during registration.
     *
     * @param username The username to look for.
     * @return The User object if found, otherwise null.
     */
    @Query("SELECT * FROM users WHERE username = :username")
    User findByUsername(String username);
}
