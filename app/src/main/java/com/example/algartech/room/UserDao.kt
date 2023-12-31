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

@Dao
interface ClimateDao {
    @Insert
    fun insert(climateEntity: ClimateEntity)

    @Query("SELECT * FROM climate_data")
    fun getAllClimateData(): List<ClimateEntity>

    @Query("SELECT * FROM climate_data WHERE name = :cityName LIMIT 1")
    fun getClimateDataByName(cityName: String): ClimateEntity?
}