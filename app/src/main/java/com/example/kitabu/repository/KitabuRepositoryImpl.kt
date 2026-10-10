package com.example.kitabu.repository

import androidx.room.withTransaction
import com.example.kitabu.data.database.KitabuDatabase
import com.example.kitabu.data.entity.BookingEntity
import com.example.kitabu.data.entity.BookingStatus
import com.example.kitabu.data.entity.BookEntity
import com.example.kitabu.data.relation.BookingWithBook
import kotlinx.coroutines.flow.Flow

class KitabuRepositoryImpl(
    private val database: KitabuDatabase
) : KitabuRepository {

    private val bookDao = database.bookDao()
    private val bookingDao = database.bookingDao()

    override fun getAllBooks(): Flow<List<BookEntity>> {
        return bookDao.getAllBooks()
    }

    override fun searchBooks(query: String): Flow<List<BookEntity>> {
        return bookDao.searchBooks(query)
    }

    override fun getAvailableBooks(): Flow<List<BookEntity>> {
        return bookDao.getAvailableBooks()
    }

    override fun getActiveBookings(): Flow<List<BookingWithBook>> {
        return bookingDao.getActiveBookings()
    }

    override suspend fun reserveBook(
        bookId: Int,
        userName: String,
        rentalDays: Int
    ) {
        // Validate the information before writing to the database.
        require(userName.isNotBlank()) {
            "Please enter your name."
        }

        require(rentalDays in 1..30) {
            "Rental duration must be between 1 and 30 days."
        }

        val now = System.currentTimeMillis()
        val deadline = now + rentalDays * 24L * 60L * 60L * 1000L

        // Keep availability and booking changes in one transaction.
        database.withTransaction {
            val book = bookDao.getBookById(bookId)
                ?: throw IllegalArgumentException("Book not found.")

            if (!book.isAvailable) {
                throw IllegalStateException(
                    "This book is no longer available."
                )
            }

            val booking = BookingEntity(
                bookOwnerId = bookId,
                userName = userName.trim(),
                bookingDate = now,
                returnDeadline = deadline,
                status = BookingStatus.ACTIVE
            )

            bookingDao.insertBooking(booking)
            bookDao.updateAvailability(bookId, false)
        }
    }

    override suspend fun returnBook(bookingId: Int) {
        database.withTransaction {
            val booking = bookingDao.getBookingById(bookingId)
                ?: throw IllegalArgumentException("Rental not found.")

            require(booking.status == BookingStatus.ACTIVE) {
                "Only active rentals can be returned."
            }

            bookingDao.updateBookingStatus(
                bookingId,
                BookingStatus.RETURNED
            )

            bookDao.updateAvailability(
                booking.bookOwnerId,
                true
            )
        }
    }

    override suspend fun renewBooking(
        bookingId: Int,
        extraDays: Int
    ) {
        require(extraDays in 1..30) {
            "Renewal must be between 1 and 30 days."
        }

        database.withTransaction {
            val booking = bookingDao.getBookingById(bookingId)
                ?: throw IllegalArgumentException("Rental not found.")

            require(booking.status == BookingStatus.ACTIVE) {
                "Only active rentals can be renewed."
            }

            val now = System.currentTimeMillis()
            val startingPoint = maxOf(booking.returnDeadline, now)
            val newDeadline =
                startingPoint + extraDays * 24L * 60L * 60L * 1000L

            bookingDao.updateReturnDeadline(
                bookingId,
                newDeadline
            )
        }
    }

    override suspend fun cancelPendingBooking(bookingId: Int) {
        database.withTransaction {
            val booking = bookingDao.getBookingById(bookingId)
                ?: throw IllegalArgumentException("Reservation not found.")

            require(booking.status == BookingStatus.PENDING) {
                "Only pending reservations can be cancelled."
            }

            val deletedRows =
                bookingDao.deletePendingBooking(bookingId)

            if (deletedRows > 0) {
                bookDao.updateAvailability(
                    booking.bookOwnerId,
                    true
                )
            }
        }
    }
}