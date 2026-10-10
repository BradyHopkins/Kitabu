package com.example.kitabu.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.kitabu.data.entity.BookEntity
import com.example.kitabu.data.relation.BookingWithBook
import com.example.kitabu.repository.KitabuRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.launch

class KitabuViewModel(
    private val repository: KitabuRepository
) : ViewModel() {

    // Holds the text entered in the catalogue search field.
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    // Updates the catalogue automatically when the database changes.
    val books: StateFlow<List<BookEntity>> =
        searchQuery
            .flatMapLatest { query ->
                if (query.isBlank()) {
                    repository.getAllBooks()
                } else {
                    repository.searchBooks(query.trim())
                }
            }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = emptyList()
            )

    // Active rentals update reactively through Room Flow.
    val activeBookings: StateFlow<List<BookingWithBook>> =
        repository.getActiveBookings().stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = emptyList()
        )

    // The UI observes this message after an operation completes.
    private val _message = MutableStateFlow<String?>(null)
    val message: StateFlow<String?> = _message.asStateFlow()

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun clearMessage() {
        _message.value = null
    }

    fun reserveBook(
        bookId: Int,
        userName: String,
        rentalDays: Int
    ) {
        performOperation("Book reserved successfully.") {
            repository.reserveBook(bookId, userName, rentalDays)
        }
    }

    fun returnBook(bookingId: Int) {
        performOperation("Book returned successfully.") {
            repository.returnBook(bookingId)
        }
    }

    fun renewBooking(
        bookingId: Int,
        extraDays: Int
    ) {
        performOperation("Rental renewed successfully.") {
            repository.renewBooking(bookingId, extraDays)
        }
    }

    fun cancelPendingBooking(bookingId: Int) {
        performOperation("Reservation cancelled.") {
            repository.cancelPendingBooking(bookingId)
        }
    }

    // Runs database operations away from the main thread and reports errors.
    private fun performOperation(
        successMessage: String,
        operation: suspend () -> Unit
    ) {
        viewModelScope.launch {
            try {
                operation()
                _message.value = successMessage
            } catch (exception: Exception) {
                _message.value =
                    exception.message ?: "Something went wrong."
            }
        }
    }
}