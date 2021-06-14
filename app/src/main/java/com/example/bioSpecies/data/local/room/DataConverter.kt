package com.example.bioSpecies.data.local.room

import androidx.room.TypeConverter
import com.example.bioSpecies.data.entity.Coordenadas
import com.example.bioSpecies.data.entity.Photo
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class DataConverter {

    @TypeConverter
    fun fromCoordenadasList(value: List<Coordenadas>): String {
        val gson = Gson()
        val type = object : TypeToken<List<Coordenadas>>() {}.type
        return gson.toJson(value, type)
    }

    @TypeConverter
    fun toCoordenadasList(value: String?): List<Coordenadas>? {
        if (value == null) {
            return null
        }
        val gson = Gson()
        return gson.fromJson(value, Array<Coordenadas>::class.java).asList()
    }

}