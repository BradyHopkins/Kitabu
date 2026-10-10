package com.example.kitabu.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Represents a book stored in the Kitabu database.
 *
 * Each book has a unique ID and an availability status.
 */
@Entity(tableName = "books")
data class BookEntity(

    // Room automatically generates a unique ID for each book.
    @PrimaryKey(autoGenerate = true)
    val bookId: Int = 0,

    // Basic book information.
    val title: String,
    val author: String,
    val category: String,

    // True means the book can currently be reserved.
    val isAvailable: Boolean = true
)