package com.example.algartech.room

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [User::class, ClimateEntity::class], version = 2)
abstract class AlgarTechDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun climateDao(): ClimateDao
}