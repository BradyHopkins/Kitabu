package com.example.kitabu.repository

import com.example.kitabu.data.entity.BookEntity
import com.example.kitabu.data.relation.BookingWithBook
import kotlinx.coroutines.flow.Flow

interface KitabuRepository {

    // Observe all books in the catalogue.
    fun getAllBooks(): Flow<List<BookEntity>>

    // Search for books by title or author.
    fun searchBooks(query: String): Flow<List<BookEntity>>

    // Observe books that are currently available.
    fun getAvailableBooks(): Flow<List<BookEntity>>

    // Observe active rentals together with their book details.
    fun getActiveBookings(): Flow<List<BookingWithBook>>

    // Reserve a book for a specified number of days.
    suspend fun reserveBook(
        bookId: Int,
        userName: String,
        rentalDays: Int
    )

    // Return an active rental.
    suspend fun returnBook(bookingId: Int)

    // Extend an active rental.
    suspend fun renewBooking(
        bookingId: Int,
        extraDays: Int
    )

    // Cancel a pending reservation.
    suspend fun cancelPendingBooking(bookingId: Int)
}