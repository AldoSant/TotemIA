package com.totem.ia.di

import android.content.Context
import androidx.room.Room
import com.totem.ia.data.local.JourneyDao
import com.totem.ia.data.local.TotemDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideTotemDatabase(@ApplicationContext context: Context): TotemDatabase {
        return Room.databaseBuilder(
            context,
            TotemDatabase::class.java,
            "totem_ia_db"
        ).fallbackToDestructiveMigration().build()
    }

    @Provides
    @Singleton
    fun provideJourneyDao(database: TotemDatabase): JourneyDao {
        return database.journeyDao()
    }
}
