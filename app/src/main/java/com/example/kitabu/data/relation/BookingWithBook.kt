package com.example.kitabu.data.relation

import androidx.room.Embedded
import androidx.room.Relation
import com.example.kitabu.data.entity.BookEntity
import com.example.kitabu.data.entity.BookingEntity

/**
 * Combines a booking with the book associated with it.
 */
data class BookingWithBook(

    @Embedded
    val booking: BookingEntity,

    @Relation(
        parentColumn = "bookOwnerId",
        entityColumn = "bookId"
    )
    val book: BookEntity
)