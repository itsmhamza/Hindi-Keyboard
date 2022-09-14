package dev.patrickgold.florisboard.ime.suggesstions.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [SuggesstionEntity::class], version = 1, exportSchema = false)
abstract class SuggesstionsDatabase : RoomDatabase() {

    abstract fun getSuggesstionDao(): SuggesstionDao

    companion object {

        private var instance: SuggesstionsDatabase? = null

        fun getInstance(context: Context): SuggesstionsDatabase {
            return instance ?: synchronized(this)
            {
                instance ?: Room.databaseBuilder(
                    context.applicationContext,
                    SuggesstionsDatabase::class.java,
                    "suggestion_database"
                ).createFromAsset("database/LearnHindiDB.sqlite").fallbackToDestructiveMigration()
                    .allowMainThreadQueries().build().also { instance = it }
            }
        }

        fun destroyInstance() {
            instance = null
        }
    }
}