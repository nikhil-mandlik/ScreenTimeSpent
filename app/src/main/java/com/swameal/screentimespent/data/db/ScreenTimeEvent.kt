package com.swameal.screentimespent.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity(tableName = "screen_time_events")
data class ScreenTimeEvent(
    @PrimaryKey(autoGenerate = true)
    @SerializedName("id")
    val id: Int? = null,
    @SerializedName("liveSessionId")
    val liveSessionId: String,
    @SerializedName("meta")
    val meta : Any? = null,
    @SerializedName("screenName")
    val screenName: String,
    @SerializedName("timeSpent")
    val timeSpent : Long
)
