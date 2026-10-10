package com.example.kitabu.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.example.kitabu.data.entity.BookingEntity
import com.example.kitabu.data.entity.BookingStatus
import com.example.kitabu.data.relation.BookingWithBook
import kotlinx.coroutines.flow.Flow

@Dao
interface BookingDao {

    // Create a booking and return its generated ID.
    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertBooking(booking: BookingEntity): Long

    // Observe all bookings with their associated book details.
    @Transaction
    @Query("SELECT * FROM bookings ORDER BY bookingDate DESC")
    fun getAllBookings(): Flow<List<BookingWithBook>>

    // Observe active rentals only.
    @Transaction
    @Query("""
        SELECT * FROM bookings
        WHERE status = 'ACTIVE'
        ORDER BY returnDeadline ASC
    """)
    fun getActiveBookings(): Flow<List<BookingWithBook>>

    // Get a booking by its ID.
    @Query("SELECT * FROM bookings WHERE bookingId = :bookingId LIMIT 1")
    suspend fun getBookingById(bookingId: Int): BookingEntity?

    // Observe bookings for a particular book.
    @Query("""
        SELECT * FROM bookings
        WHERE bookOwnerId = :bookId
        ORDER BY bookingDate DESC
    """)
    fun getBookingsForBook(bookId: Int): Flow<List<BookingEntity>>

    // Change a booking's status.
    @Query("""
        UPDATE bookings
        SET status = :status
        WHERE bookingId = :bookingId
    """)
    suspend fun updateBookingStatus(
        bookingId: Int,
        status: BookingStatus
    )

    // Update a booking's return deadline when renewing.
    @Query("""
        UPDATE bookings
        SET returnDeadline = :newDeadline
        WHERE bookingId = :bookingId
          AND status = 'ACTIVE'
    """)
    suspend fun updateReturnDeadline(
        bookingId: Int,
        newDeadline: Long
    )

    // Update all fields of an existing booking.
    @Update
    suspend fun updateBooking(booking: BookingEntity)

    // Delete a pending booking only.
    @Query("""
        DELETE FROM bookings
        WHERE bookingId = :bookingId
          AND status = 'PENDING'
    """)
    suspend fun deletePendingBooking(bookingId: Int): Int

    // Delete every booking. Use only for controlled testing.
    @Query("DELETE FROM bookings")
    suspend fun deleteAllBookings()
}