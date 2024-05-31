package com.example.storyappku.data.model.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.storyappku.data.model.response.ListStoryItem

@Database(
    entities = [ListStoryItem::class, RemoteKeys::class],
    version = 3,
    exportSchema = false
)
abstract class CustomStoryDatabase : RoomDatabase() {

    abstract fun storyDao(): StoryDao
    abstract fun remoteKeysDao(): RemoteKeysDao

    companion object {

        @Volatile
        private var INSTANCE: CustomStoryDatabase? = null

        @JvmStatic
        fun getDatabase(context: Context): CustomStoryDatabase {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: buildDatabase(context).also { INSTANCE = it }
            }
        }

        private fun buildDatabase(context: Context): CustomStoryDatabase {
            return Room.databaseBuilder(
                context.applicationContext,
                CustomStoryDatabase::class.java,
                "story_db"
            ).build()
        }
    }
}