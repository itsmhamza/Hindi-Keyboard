package com.spellchecker.arabickb.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(version =1,entities = [Speaktranslation::class])
abstract  class Speaktranslatedb: RoomDatabase()  {
    abstract fun SpeakDao():SpeakDao

    companion object {

        private var INSTANCE: Speaktranslatedb? = null
        fun getDatabase(context: Context): Speaktranslatedb {
            if (INSTANCE == null) {
                synchronized(this) {
                    INSTANCE = Room.databaseBuilder(
                        context,
                        Speaktranslatedb::class.java,
                        "speaktranslate.db"
                    ).build()
                }
            }
            return INSTANCE!!
        }
    }
}