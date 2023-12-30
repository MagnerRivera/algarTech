package com.example.algartech.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface UserDao {
    @Insert
    fun insert(user: User)

    @Query("SELECT * FROM users WHERE username = :username AND password = :password")
    fun getUserByEmailAndPassword(username: String, password: String): User?
}