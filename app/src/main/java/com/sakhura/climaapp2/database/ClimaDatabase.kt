package com.sakhura.climaapp2.database.dao

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.sakhura.climaapp2.entities.ClimaEntity
import com.sakhura.climaapp2.entities.Converters
import com.sakhura.climaapp2.entities.PronosticoEntity

@Database(
    entities = [ClimaEntity::class, PronosticoEntity::class],
    version = 1,
        exportSchema = false
)

@TypeConverters(Converters::class)
abstract class ClimaDatabase : RoomDatabase() {
    abstract fun climaDao(): ClimaDao

    companion object{
        @Volatile
        private var INSTANCE: ClimaDatabase? = null

        fun getDatabase(context: Context): ClimaDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    ClimaDatabase::class.java,
                    "clima_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
            }
    }
}