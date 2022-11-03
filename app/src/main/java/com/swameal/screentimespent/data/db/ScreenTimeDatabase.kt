package com.swameal.screentimespent.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import com.google.gson.Gson


@Database(
    entities = [ScreenTimeEvent::class],
    version = 1,
    exportSchema = true
)
@TypeConverters(TypeConvertors::class)
abstract class ScreenTimeDatabase : RoomDatabase() {
    abstract fun screenTimeDao(): ScreenTimeDao
}