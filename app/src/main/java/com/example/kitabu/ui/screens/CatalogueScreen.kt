package com.example.kitabu.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.kitabu.data.entity.BookEntity
import com.example.kitabu.viewmodel.KitabuViewModel

// Kitabu's custom colours.
private val Navy = Color(0xFF183153)
private val Teal = Color(0xFF168C83)
private val Cream = Color(0xFFF6F8F5)
private val MutedText = Color(0xFF687787)
private val PaleTeal = Color(0xFFE3F4F0)
private val PaleRed = Color(0xFFFFE9E7)

@Composable
fun CatalogueScreen(
    viewModel: KitabuViewModel
) {
    val books by viewModel.books.collectAsStateWithLifecycle()
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
    val message by viewModel.message.collectAsStateWithLifecycle()

    var selectedBook by remember {
        mutableStateOf<BookEntity?>(null)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Cream)
    ) {
        // Coloured header section.
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.linearGradient(
                        colors = listOf(Navy, Teal)
                    )
                )
                .padding(
                    start = 20.dp,
                    end = 20.dp,
                    top = 24.dp,
                    bottom = 24.dp
                )
        ) {
            Text(
                text = "KITABU  /  YOUR LIBRARY",
                color = Color.White.copy(alpha = 0.8f),
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.5.sp
            )

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = "Find your next book.",
                color = Color.White,
                fontSize = 29.sp,
                lineHeight = 35.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Explore, discover and reserve learning resources.",
                color = Color.White.copy(alpha = 0.88f),
                style = MaterialTheme.typography.bodyMedium
            )

            Spacer(modifier = Modifier.height(18.dp))

            // Search field sits inside the coloured header.
            OutlinedTextField(
                value = searchQuery,
                onValueChange = viewModel::updateSearchQuery,
                modifier = Modifier.fillMaxWidth(),
                placeholder = {
                    Text("Search titles or authors...")
                },
                leadingIcon = {
                    Text(
                        text = "⌕",
                        fontSize = 28.sp,
                        color = Navy
                    )
                },
                singleLine = true,
                shape = RoundedCornerShape(16.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White,
                    focusedBorderColor = Color.White,
                    unfocusedBorderColor = Color.White,
                    focusedTextColor = Navy,
                    unfocusedTextColor = Navy,
                    focusedPlaceholderColor = MutedText,
                    unfocusedPlaceholderColor = MutedText
                )
            )
        }

        // Catalogue results.
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    horizontal = 20.dp,
                    vertical = 18.dp
                ),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Explore the collection",
                    color = Navy,
                    fontSize = 19.sp,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = "Find a resource for your studies",
                    color = MutedText,
                    style = MaterialTheme.typography.bodySmall
                )
            }

            Surface(
                shape = RoundedCornerShape(12.dp),
                color = PaleTeal
            ) {
                Text(
                    text = "${books.size} books",
                    modifier = Modifier.padding(
                        horizontal = 12.dp,
                        vertical = 8.dp
                    ),
                    color = Teal,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp
                )
            }
        }

        if (books.isEmpty()) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(28.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("📚", fontSize = 42.sp)

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "No books found",
                    color = Navy,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = "Try a different title or author.",
                    color = MutedText
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = androidx.compose.foundation.layout.PaddingValues(
                    start = 16.dp,
                    end = 16.dp,
                    bottom = 20.dp
                ),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(
                    items = books,
                    key = { it.bookId }
                ) { book ->
                    BookCard(
                        book = book,
                        onReserveClick = {
                            selectedBook = book
                        }
                    )
                }
            }
        }
    }

    // Display success or error messages from the ViewModel.
    if (message != null) {
        AlertDialog(
            onDismissRequest = { viewModel.clearMessage() },
            containerColor = Color.White,
            title = {
                Text(
                    text = "Kitabu update",
                    color = Navy,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Text(
                    text = message.orEmpty(),
                    color = MutedText
                )
            },
            confirmButton = {
                Button(
                    onClick = { viewModel.clearMessage() },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Teal
                    )
                ) {
                    Text("Got it")
                }
            }
        )
    }

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
    // Give different categories different cover colours.
    val coverColour = when (book.category.lowercase()) {
        "computer science" -> Color(0xFF4169A1)
        "software development" -> Color(0xFF8B5E91)
        "databases" -> Color(0xFFB66A43)
        "networking" -> Color(0xFF358C83)
        "artificial intelligence" -> Color(0xFF6B70B8)
        else -> Color(0xFF547C64)
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Decorative book-cover illustration.
            Box(
                modifier = Modifier
                    .width(96.dp)
                    .height(132.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                coverColour,
                                coverColour.copy(alpha = 0.75f)
                            )
                        )
                    )
                    .padding(10.dp)
            ) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "K",
                        color = Color.White.copy(alpha = 0.85f),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Black
                    )

                    Column {
                        Box(
                            modifier = Modifier
                                .width(42.dp)
                                .height(3.dp)
                                .background(Color.White.copy(alpha = 0.9f))
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        Box(
                            modifier = Modifier
                                .width(30.dp)
                                .height(3.dp)
                                .background(Color.White.copy(alpha = 0.65f))
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        Text(
                            text = book.category.uppercase(),
                            color = Color.White,
                            fontSize = 8.sp,
                            lineHeight = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = book.category,
                    color = Teal,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(5.dp))

                Text(
                    text = book.title,
                    color = Navy,
                    fontSize = 16.sp,
                    lineHeight = 21.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(5.dp))

                Text(
                    text = "by ${book.author}",
                    color = MutedText,
                    fontSize = 12.sp,
                    lineHeight = 17.sp
                )

                Spacer(modifier = Modifier.height(10.dp))

                Surface(
                    shape = CircleShape,
                    color = if (book.isAvailable) PaleTeal else PaleRed
                ) {
                    Text(
                        text = if (book.isAvailable) {
                            "● Available"
                        } else {
                            "● Borrowed"
                        },
                        modifier = Modifier.padding(
                            horizontal = 9.dp,
                            vertical = 5.dp
                        ),
                        color = if (book.isAvailable) Teal else Color(0xFFB8443C),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                Button(
                    onClick = onReserveClick,
                    enabled = book.isAvailable,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Teal,
                        disabledContainerColor = Color(0xFFE1E5E8),
                        disabledContentColor = MutedText
                    )
                ) {
                    Text(
                        text = if (book.isAvailable) "Reserve book" else "Unavailable",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
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
    var studentName by remember(book.bookId) {
        mutableStateOf("")
    }

    var rentalDays by remember(book.bookId) {
        mutableIntStateOf(7)
    }

    var validationMessage by remember(book.bookId) {
        mutableStateOf<String?>(null)
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = Color.White,
        shape = RoundedCornerShape(24.dp),
        title = {
            Column {
                Text(
                    text = "Complete your reservation",
                    color = Navy,
                    fontSize = 21.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(5.dp))

                Text(
                    text = "Enter your details to borrow this book.",
                    color = MutedText,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Normal
                )
            }
        },
        text = {
            Column {
                // Selected book summary.
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    color = Cream
                ) {
                    Column(
                        modifier = Modifier.padding(14.dp)
                    ) {
                        Text(
                            text = "YOUR SELECTED BOOK",
                            color = Teal,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        )

                        Spacer(modifier = Modifier.height(5.dp))

                        Text(
                            text = book.title,
                            color = Navy,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )

                        Text(
                            text = "by ${book.author}",
                            color = MutedText,
                            fontSize = 12.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                Text(
                    text = "STUDENT INFORMATION",
                    color = Navy,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = studentName,
                    onValueChange = {
                        studentName = it
                        validationMessage = null
                    },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Full name") },
                    placeholder = { Text("Enter your full name") },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    isError = validationMessage != null,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Teal,
                        focusedLabelColor = Teal,
                        cursorColor = Teal
                    )
                )

                Spacer(modifier = Modifier.height(18.dp))

                Text(
                    text = "CHOOSE RENTAL DURATION",
                    color = Navy,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )

                Spacer(modifier = Modifier.height(5.dp))

                Text(
                    text = "Select how long you need the book.",
                    color = MutedText,
                    fontSize = 12.sp
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Rental options arranged in a grid-like layout.
                listOf(7, 14, 21, 30).chunked(2).forEach { rowDays ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        rowDays.forEach { days ->
                            val selected = rentalDays == days

                            Surface(
                                onClick = {
                                    rentalDays = days
                                },
                                modifier = Modifier
                                    .weight(1f)
                                    .padding(bottom = 8.dp)
                                    .border(
                                        width = 1.dp,
                                        color = if (selected) Teal else Color(0xFFDDE4E5),
                                        shape = RoundedCornerShape(12.dp)
                                    ),
                                shape = RoundedCornerShape(12.dp),
                                color = if (selected) PaleTeal else Color.White
                            ) {
                                Column(
                                    modifier = Modifier.padding(12.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text(
                                        text = "$days days",
                                        color = if (selected) Teal else Navy,
                                        fontWeight = FontWeight.Bold
                                    )

                                    Text(
                                        text = if (selected) "Selected" else "Rental period",
                                        color = MutedText,
                                        fontSize = 10.sp
                                    )
                                }
                            }
                        }
                    }
                }

                validationMessage?.let { error ->
                    Text(
                        text = error,
                        color = MaterialTheme.colorScheme.error,
                        fontSize = 12.sp
                    )
                }

                Text(
                    text = "Your return deadline will be calculated automatically.",
                    color = MutedText,
                    fontSize = 11.sp,
                    lineHeight = 15.sp
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (studentName.isBlank()) {
                        validationMessage = "Please enter your full name."
                    } else {
                        onReserve(studentName.trim(), rentalDays)
                    }
                },
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Teal
                )
            ) {
                Text(
                    text = "Confirm reservation",
                    fontWeight = FontWeight.Bold
                )
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(
                    text = "Cancel",
                    color = MutedText
                )
            }
        }
    )
}