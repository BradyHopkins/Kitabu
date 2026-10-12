
package com.example.kitabu.ui.screens

import androidx.compose.foundation.background
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
import com.example.kitabu.data.relation.BookingWithBook
import com.example.kitabu.viewmodel.KitabuViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.math.ceil

// Use the same colour palette as the redesigned catalogue.
private val RentalNavy = Color(0xFF183153)
private val RentalTeal = Color(0xFF168C83)
private val RentalCream = Color(0xFFF6F8F5)
private val RentalMuted = Color(0xFF687787)
private val RentalPaleTeal = Color(0xFFE3F4F0)
private val RentalPaleRed = Color(0xFFFFE9E7)

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
            .background(RentalCream)
    ) {
        // Matching navy-to-teal header.
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.linearGradient(
                        colors = listOf(RentalNavy, RentalTeal)
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
                text = "KITABU  /  MY LIBRARY",
                color = Color.White.copy(alpha = 0.8f),
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.5.sp
            )

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = "Your reading\njourney.",
                color = Color.White,
                fontSize = 29.sp,
                lineHeight = 35.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Keep track of your books and return dates.",
                color = Color.White.copy(alpha = 0.88f),
                fontSize = 13.sp
            )

            Spacer(modifier = Modifier.height(18.dp))

            // Summary panel in the header.
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = Color.White.copy(alpha = 0.14f)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.18f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("📚", fontSize = 22.sp)
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column {
                        Text(
                            text = "${bookings.size} active rental(s)",
                            color = Color.White,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )

                        Text(
                            text = if (bookings.isEmpty()) {
                                "Ready for your next book?"
                            } else {
                                "Your current borrowed books"
                            },
                            color = Color.White.copy(alpha = 0.85f),
                            fontSize = 12.sp
                        )
                    }
                }
            }
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    horizontal = 20.dp,
                    vertical = 18.dp
                )
        ) {
            Text(
                text = "Current rentals",
                color = RentalNavy,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = "Renew or return a book whenever you're ready.",
                color = RentalMuted,
                fontSize = 12.sp
            )
        }

        if (bookings.isEmpty()) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(28.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(76.dp)
                        .clip(CircleShape)
                        .background(RentalPaleTeal),
                    contentAlignment = Alignment.Center
                ) {
                    Text("📖", fontSize = 34.sp)
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "No active rentals yet",
                    color = RentalNavy,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = "Visit the Catalogue to reserve your first book.",
                    color = RentalMuted,
                    fontSize = 13.sp
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = androidx.compose.foundation.layout.PaddingValues(
                    start = 16.dp,
                    end = 16.dp,
                    bottom = 24.dp
                ),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                items(
                    items = bookings,
                    key = { it.booking.bookingId }
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

    // Display feedback after an operation.
    if (message != null) {
        AlertDialog(
            onDismissRequest = { viewModel.clearMessage() },
            containerColor = Color.White,
            title = {
                Text(
                    text = "Kitabu update",
                    color = RentalNavy,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Text(
                    text = message.orEmpty(),
                    color = RentalMuted
                )
            },
            confirmButton = {
                Button(
                    onClick = { viewModel.clearMessage() },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = RentalTeal
                    )
                ) {
                    Text("Got it")
                }
            }
        )
    }

    // Renewal form.
    renewalBooking?.let { item ->
        AlertDialog(
            onDismissRequest = { renewalBooking = null },
            containerColor = Color.White,
            shape = RoundedCornerShape(24.dp),
            title = {
                Text(
                    text = "Renew your book",
                    color = RentalNavy,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Column {
                    Text(
                        text = item.book.title,
                        color = RentalNavy,
                        fontWeight = FontWeight.SemiBold
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = "Choose how many extra days you need.",
                        color = RentalMuted,
                        fontSize = 13.sp
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = extraDays,
                        onValueChange = { input ->
                            if (input.all(Char::isDigit) && input.length <= 2) {
                                extraDays = input
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text("Extra days (1–30)") },
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = RentalTeal,
                            focusedLabelColor = RentalTeal,
                            cursorColor = RentalTeal
                        )
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
                    enabled = (extraDays.toIntOrNull() ?: 0) in 1..30,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = RentalTeal
                    )
                ) {
                    Text("Confirm renewal")
                }
            },
            dismissButton = {
                TextButton(onClick = { renewalBooking = null }) {
                    Text("Cancel", color = RentalMuted)
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
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // Book title and deadline status.
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Top
            ) {
                Box(
                    modifier = Modifier
                        .size(54.dp)
                        .clip(RoundedCornerShape(13.dp))
                        .background(
                            Brush.linearGradient(
                                colors = listOf(RentalNavy, RentalTeal)
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text("📚", fontSize = 25.sp)
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = book.title,
                        color = RentalNavy,
                        fontSize = 16.sp,
                        lineHeight = 21.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = "by ${book.author}",
                        color = RentalMuted,
                        fontSize = 12.sp
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Surface(
                        shape = CircleShape,
                        color = when {
                            daysRemaining <= 0 -> RentalPaleRed
                            daysRemaining <= 3 -> Color(0xFFFFF0D7)
                            else -> RentalPaleTeal
                        }
                    ) {
                        Text(
                            text = when {
                                daysRemaining < 0 ->
                                    "Overdue by ${-daysRemaining} day(s)"
                                daysRemaining == 0 ->
                                    "Due today"
                                else ->
                                    "$daysRemaining day(s) remaining"
                            },
                            modifier = Modifier.padding(
                                horizontal = 9.dp,
                                vertical = 5.dp
                            ),
                            color = when {
                                daysRemaining <= 0 -> Color(0xFFB8443C)
                                daysRemaining <= 3 -> Color(0xFF966017)
                                else -> RentalTeal
                            },
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Booking dates.
            DateDetail(
                label = "Reservation date",
                value = dateFormatter.format(Date(booking.bookingDate))
            )

            Spacer(modifier = Modifier.height(8.dp))

            DateDetail(
                label = "Return deadline",
                value = dateFormatter.format(Date(booking.returnDeadline))
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Button(
                    onClick = onRenew,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = RentalNavy
                    )
                ) {
                    Text(
                        text = "Renew",
                        fontWeight = FontWeight.Bold
                    )
                }

                Button(
                    onClick = onReturn,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = RentalTeal
                    )
                ) {
                    Text(
                        text = "Return book",
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
private fun DateDetail(
    label: String,
    value: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            color = RentalMuted,
            fontSize = 12.sp
        )

        Spacer(modifier = Modifier.width(8.dp))

        Text(
            text = value,
            color = RentalNavy,
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold
        )
    }
}
