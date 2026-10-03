package com.example.frem

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.frem.models.PlaylistDao
import com.example.frem.models.PlaylistEntity
import com.example.frem.models.PlaylistSongEntity
import com.example.frem.models.SongDao
import com.example.frem.models.SongEntity

@Database(
    entities = [
        SongEntity::class,
        PlaylistEntity::class,
        PlaylistSongEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class FremDatabase : RoomDatabase() {

    abstract fun songDao(): SongDao

    abstract fun playlistDao(): PlaylistDao

    companion object {

        @Volatile
        private var INSTANCE: FremDatabase? = null

        fun getInstance(context: Context): FremDatabase {

            return INSTANCE ?: synchronized(this) {

                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    FremDatabase::class.java,
                    "frem_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()

                INSTANCE = instance

                instance
            }
        }
    }
}