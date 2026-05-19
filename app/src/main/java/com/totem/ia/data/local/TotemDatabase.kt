package com.totem.ia.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(entities = [JourneyEntity::class, ChapterEntity::class], version = 1, exportSchema = false)
@TypeConverters(Converters::class)
abstract class TotemDatabase : RoomDatabase() {
    abstract fun journeyDao(): JourneyDao
}
