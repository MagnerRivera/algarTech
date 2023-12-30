package com.example.algartech.room

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [User::class], version = 1)
abstract class AlgarTechDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao

}