package com.example.algartech.room

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(tableName = "users")
data class User(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val password: String,
    val username: String,
    val email: String,
)

@Entity(tableName = "climate_data")
data class ClimateEntity(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val name: String,
    val lat: Double,
    val lon: Double,
    val temperature: Int,
    val description: String,
    val windSpeed: Double,
    val humidity: Int,
    val icon: String
)