package com.example.kitabu.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.kitabu.data.dao.BookDao
import com.example.kitabu.data.dao.BookingDao
import com.example.kitabu.data.entity.BookEntity
import com.example.kitabu.data.entity.BookingEntity

/**
 * Main Room database for the Kitabu application.
 */
@Database(
    entities = [
        BookEntity::class,
        BookingEntity::class
    ],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class KitabuDatabase : RoomDatabase() {

    abstract fun bookDao(): BookDao

    abstract fun bookingDao(): BookingDao

    companion object {

        @Volatile
        private var INSTANCE: KitabuDatabase? = null

        fun getDatabase(context: Context): KitabuDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    KitabuDatabase::class.java,
                    "kitabu_database"
                )
                    .addCallback(object : RoomDatabase.Callback() {

                        override fun onCreate(db: SupportSQLiteDatabase) {
                            super.onCreate(db)

                            // Add starter books when the database is first created.
                            db.execSQL("""
                                INSERT INTO books
                                    (title, author, category, isAvailable)
                                VALUES
                                    ('Introduction to Algorithms',
                                     'Thomas H. Cormen',
                                     'Computer Science', 1),

                                    ('Clean Code',
                                     'Robert C. Martin',
                                     'Software Development', 1),

                                    ('Database System Concepts',
                                     'Abraham Silberschatz',
                                     'Databases', 1),

                                    ('The Pragmatic Programmer',
                                     'Andrew Hunt',
                                     'Software Development', 1),

                                    ('Computer Networking: A Top-Down Approach',
                                     'James Kurose',
                                     'Networking', 1),

                                    ('Artificial Intelligence: A Modern Approach',
                                     'Stuart Russell',
                                     'Artificial Intelligence', 1)
                            """.trimIndent())
                        }
                    })
                    // Useful while developing; not intended as a production migration strategy.
                    .fallbackToDestructiveMigration()
                    .build()

                INSTANCE = instance
                instance
            }
        }
    }
}