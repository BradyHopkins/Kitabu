package com.example.kitabu.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.kitabu.data.entity.BookEntity
import com.example.kitabu.viewmodel.KitabuViewModel

@Composable
fun CatalogueScreen(
    viewModel: KitabuViewModel
) {
    val books by viewModel.books.collectAsStateWithLifecycle()
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
    val message by viewModel.message.collectAsStateWithLifecycle()

    var selectedBook by remember { mutableStateOf<BookEntity?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Kitabu Library",
            style = MaterialTheme.typography.headlineMedium
        )

        Text(
            text = "Discover and reserve your next book.",
            style = MaterialTheme.typography.bodyMedium
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = searchQuery,
            onValueChange = viewModel::updateSearchQuery,
            modifier = Modifier.fillMaxWidth(),
            label = { Text("Search by title or author") },
            singleLine = true
        )

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = "${books.size} books found",
            style = MaterialTheme.typography.titleSmall
        )

        Spacer(modifier = Modifier.height(8.dp))

        if (books.isEmpty()) {
            Text(
                text = "No books found. Try another search.",
                modifier = Modifier.padding(vertical = 24.dp)
            )
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(
                    items = books,
                    key = { book -> book.bookId }
                ) { book ->
                    BookCard(
                        book = book,
                        onReserveClick = { selectedBook = book }
                    )
                }
            }
        }
    }

    // Show operation feedback.
    if (message != null) {
        AlertDialog(
            onDismissRequest = { viewModel.clearMessage() },
            title = { Text("Kitabu") },
            text = { Text(message.orEmpty()) },
            confirmButton = {
                TextButton(onClick = { viewModel.clearMessage() }) {
                    Text("OK")
                }
            }
        )
    }

    // Display the reservation form for the selected book.
    selectedBook?.let { book ->
        ReservationDialog(
            book = book,
            onDismiss = { selectedBook = null },
            onReserve = { name, days ->
                viewModel.reserveBook(
                    bookId = book.bookId,
                    userName = name,
                    rentalDays = days
                )
                selectedBook = null
            }
        )
    }
}

@Composable
private fun BookCard(
    book: BookEntity,
    onReserveClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = book.title,
                style = MaterialTheme.typography.titleMedium
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "Author: ${book.author}",
                style = MaterialTheme.typography.bodyMedium
            )

            Text(
                text = "Category: ${book.category}",
                style = MaterialTheme.typography.bodySmall
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (book.isAvailable) {
                        "Available"
                    } else {
                        "Currently borrowed"
                    },
                    color = if (book.isAvailable) {
                        MaterialTheme.colorScheme.primary
                    } else {
                        MaterialTheme.colorScheme.error
                    },
                    style = MaterialTheme.typography.labelLarge
                )

                Button(
                    onClick = onReserveClick,
                    enabled = book.isAvailable
                ) {
                    Text("Reserve")
                }
            }
        }
    }
}

@Composable
private fun ReservationDialog(
    book: BookEntity,
    onDismiss: () -> Unit,
    onReserve: (String, Int) -> Unit
) {
    var userName by remember(book.bookId) {
        mutableStateOf("")
    }

    var rentalDays by remember(book.bookId) {
        mutableIntStateOf(7)
    }

    var validationMessage by remember {
        mutableStateOf<String?>(null)
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Reserve book") },
        text = {
            Column {
                Text(
                    text = book.title,
                    style = MaterialTheme.typography.titleMedium
                )

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = userName,
                    onValueChange = {
                        userName = it
                        validationMessage = null
                    },
                    label = { Text("Student name") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text("Rental duration")

                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    listOf(7, 14, 21, 30).forEach { days ->
                        TextButton(
                            onClick = { rentalDays = days }
                        ) {
                            Text(
                                text = if (rentalDays == days) {
                                    "$days days ✓"
                                } else {
                                    "$days days"
                                }
                            )
                        }
                    }
                }

                validationMessage?.let { error ->
                    Text(
                        text = error,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (userName.isBlank()) {
                        validationMessage = "Please enter your name."
                    } else {
                        onReserve(userName.trim(), rentalDays)
                    }
                }
            ) {
                Text("Confirm reservation")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}