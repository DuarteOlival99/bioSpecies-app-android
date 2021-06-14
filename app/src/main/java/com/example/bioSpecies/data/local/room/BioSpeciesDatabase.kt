package com.example.bioSpecies.data.local.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.bioSpecies.data.entity.*
import com.example.bioSpecies.data.local.room.dao.OperationDao

@Database(entities = arrayOf(Animal::class, Photo::class, Semana::class, EstatisticasDesafios::class), version = 22)
@TypeConverters(value = [DataConverter::class])
abstract class BioSpeciesDatabase : RoomDatabase() {

    abstract fun operationDao() : OperationDao

    companion object {
        private var instance: BioSpeciesDatabase? = null

        fun getInstance(applicationContext: Context): BioSpeciesDatabase {
            synchronized(this) {
                if (instance == null) {
                    instance = Room.databaseBuilder(
                        applicationContext,
                        BioSpeciesDatabase::class.java,
                        "bioSpeciesDatabase_bd"
                    )
                        .fallbackToDestructiveMigration()
                        .build()
                }
                return instance as BioSpeciesDatabase
            }
        }
    }



}