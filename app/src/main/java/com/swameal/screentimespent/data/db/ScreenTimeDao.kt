package com.swameal.screentimespent.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy

@Dao
interface ScreenTimeDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(screenTimeEvent: ScreenTimeEvent)
}