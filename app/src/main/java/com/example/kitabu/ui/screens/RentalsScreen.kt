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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.kitabu.data.relation.BookingWithBook
import com.example.kitabu.viewmodel.KitabuViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.math.ceil

@Composable
fun RentalsScreen(
    viewModel: KitabuViewModel
) {
    val bookings by viewModel.activeBookings.collectAsStateWithLifecycle()
    val message by viewModel.message.collectAsStateWithLifecycle()

    var renewalBooking by remember {
        mutableStateOf<BookingWithBook?>(null)
    }

    var extraDays by remember {
        mutableStateOf("7")
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "My Rentals",
            style = MaterialTheme.typography.headlineMedium
        )

        Text(
            text = "Manage your current library rentals.",
            style = MaterialTheme.typography.bodyMedium
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "${bookings.size} active rental(s)",
            style = MaterialTheme.typography.titleSmall
        )

        Spacer(modifier = Modifier.height(8.dp))

        if (bookings.isEmpty()) {
            Text(
                text = "You have no active rentals yet.",
                modifier = Modifier.padding(vertical = 24.dp)
            )
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(
                    items = bookings,
                    key = { item -> item.booking.bookingId }
                ) { item ->
                    RentalCard(
                        item = item,
                        onReturn = {
                            viewModel.returnBook(item.booking.bookingId)
                        },
                        onRenew = {
                            extraDays = "7"
                            renewalBooking = item
                        }
                    )
                }
            }
        }
    }

    // Feedback is available on this screen as well.
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

    renewalBooking?.let { item ->
        AlertDialog(
            onDismissRequest = { renewalBooking = null },
            title = { Text("Renew rental") },
            text = {
                Column {
                    Text("Book: ${item.book.title}")

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = extraDays,
                        onValueChange = { input ->
                            if (input.all(Char::isDigit)) {
                                extraDays = input
                            }
                        },
                        label = { Text("Extra days (1–30)") },
                        singleLine = true
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val days = extraDays.toIntOrNull()

                        if (days != null && days in 1..30) {
                            viewModel.renewBooking(
                                bookingId = item.booking.bookingId,
                                extraDays = days
                            )
                            renewalBooking = null
                        }
                    },
                    enabled = (extraDays.toIntOrNull() ?: 0) in 1..30
                ) {
                    Text("Confirm renewal")
                }
            },
            dismissButton = {
                TextButton(onClick = { renewalBooking = null }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
private fun RentalCard(
    item: BookingWithBook,
    onReturn: () -> Unit,
    onRenew: () -> Unit
) {
    val booking = item.booking
    val book = item.book

    val dateFormatter = remember {
        SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
    }

    val today = System.currentTimeMillis()
    val millisecondsPerDay = 24L * 60L * 60L * 1000L

    val daysRemaining = ceil(
        (booking.returnDeadline - today).toDouble() /
                millisecondsPerDay.toDouble()
    ).toInt()

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = book.title,
                style = MaterialTheme.typography.titleMedium
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text("Author: ${book.author}")
            Text("Borrower: ${booking.userName}")
            Text(
                "Reserved: ${
                    dateFormatter.format(Date(booking.bookingDate))
                }"
            )
            Text(
                "Return deadline: ${
                    dateFormatter.format(Date(booking.returnDeadline))
                }"
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = when {
                    daysRemaining < 0 ->
                        "Overdue by ${-daysRemaining} day(s)"
                    daysRemaining == 0 ->
                        "Due today"
                    else ->
                        "$daysRemaining day(s) remaining"
                },
                color = if (daysRemaining <= 0) {
                    MaterialTheme.colorScheme.error
                } else {
                    MaterialTheme.colorScheme.primary
                },
                style = MaterialTheme.typography.labelLarge
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = onRenew,
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Renew")
                }

                Button(
                    onClick = onReturn,
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Return")
                }
            }
        }
    }
}