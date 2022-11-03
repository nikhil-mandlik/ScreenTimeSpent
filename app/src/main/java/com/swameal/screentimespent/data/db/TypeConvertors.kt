package com.swameal.screentimespent.data.db

import androidx.room.TypeConverter
import com.google.gson.Gson


class TypeConvertors {
    private val gson: Gson = Gson()

    @TypeConverter
    fun convertScreenDwellEventToJson(screenTimeEvent: ScreenTimeEvent): String {
        return gson.toJson(screenTimeEvent)
    }

    @TypeConverter
    fun convertJsonToScreenDwellEvent(json: String): ScreenTimeEvent {
        return gson.fromJson(json, ScreenTimeEvent::class.java)
    }

    @TypeConverter
    fun convertAnyToJson(value: Any?): String {
        return gson.toJson(value)
    }

    @TypeConverter
    fun convertJsonToAny(json: String?): Any {
        return gson.fromJson(json, Any::class.java)
    }


}