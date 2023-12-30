package com.example.algartech.di

import android.app.Application
import androidx.room.Room
import com.example.algartech.room.AlgarTechDatabase
import com.example.algartech.room.UserDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    // Proporciono la instancia única de la base de datos algartech-database
    @Provides
    @Singleton
    fun provideAlgarTechDatabase(application: Application): AlgarTechDatabase {
        return Room.databaseBuilder(
            application,
            AlgarTechDatabase::class.java,
            "algartech-database"
        )
            .fallbackToDestructiveMigration()
            .build()
    }

    @Provides
    @Singleton
    fun provideUserDao(appDatabase: AlgarTechDatabase): UserDao {
        return appDatabase.userDao()
    }
}