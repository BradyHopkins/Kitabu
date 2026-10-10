package com.example.kitabu.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.kitabu.data.entity.BookEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface BookDao {

    // Insert one or more books.
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertBooks(books: List<BookEntity>)

    // Get every book. Flow updates the UI when the table changes.
    @Query("SELECT * FROM books ORDER BY title ASC")
    fun getAllBooks(): Flow<List<BookEntity>>

    // Search by title or author.
    @Query("""
        SELECT * FROM books
        WHERE title LIKE '%' || :searchQuery || '%'
           OR author LIKE '%' || :searchQuery || '%'
        ORDER BY title ASC
    """)
    fun searchBooks(searchQuery: String): Flow<List<BookEntity>>

    // Get only books that are available.
    @Query("SELECT * FROM books WHERE isAvailable = 1 ORDER BY title ASC")
    fun getAvailableBooks(): Flow<List<BookEntity>>

    // Get one book by its ID.
    @Query("SELECT * FROM books WHERE bookId = :bookId LIMIT 1")
    suspend fun getBookById(bookId: Int): BookEntity?

    // Change the availability of a book.
    @Query("UPDATE books SET isAvailable = :available WHERE bookId = :bookId")
    suspend fun updateAvailability(bookId: Int, available: Boolean)

    // Count the books already in the database.
    @Query("SELECT COUNT(*) FROM books")
    suspend fun getBookCount(): Int

    // Update a book's details.
    @Update
    suspend fun updateBook(book: BookEntity)

    // Delete a book by ID.
    @Query("DELETE FROM books WHERE bookId = :bookId")
    suspend fun deleteBook(bookId: Int)
}