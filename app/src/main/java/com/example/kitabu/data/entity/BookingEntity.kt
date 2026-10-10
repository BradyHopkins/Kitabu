package com.example.kitabu.data.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Represents a student's booking of a book.
 *
 * bookOwnerId links this booking to the corresponding book.
 */
@Entity(
    tableName = "bookings",

    // A booking must refer to an existing book.
    foreignKeys = [
        ForeignKey(
            entity = BookEntity::class,
            parentColumns = ["bookId"],
            childColumns = ["bookOwnerId"],
            onDelete = ForeignKey.CASCADE
        )
    ],

    // Improves lookups by book ID and booking status.
    indices = [
        Index(value = ["bookOwnerId"]),
        Index(value = ["status"])
    ]
)
data class BookingEntity(

    @PrimaryKey(autoGenerate = true)
    val bookingId: Int = 0,

    // ID of the book being booked.
    val bookOwnerId: Int,

    // Name entered by the student.
    val userName: String,

    // Booking creation time, stored as milliseconds.
    val bookingDate: Long,

    // Deadline for returning the book, stored as milliseconds.
    val returnDeadline: Long,

    // Current state of the booking.
    val status: BookingStatus = BookingStatus.PENDING
)