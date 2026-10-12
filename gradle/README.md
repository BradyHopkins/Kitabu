Kitabu — Mobile Library Management System

1. Project Overview

Kitabu is an Android library management application designed to help academic institutions manage
their book catalogue and book rentals. The application allows users to browse books, search by title
or author, reserve available books, view active rentals, renew rentals and return books.

The project was developed as part of the MADB372 Mobile Application Development course.

2. Technologies Used

Language: Kotlin

User Interface: Jetpack Compose

Design System: Material 3

Database: Room / SQLite

Architecture: Model–View–ViewModel (MVVM)

Asynchronous Processing: Kotlin Coroutines and Flow

Navigation: Navigation Compose

Annotation Processing: KSP

Development Environment: Android Studio

3. Main Features

 - Book Catalogue

Displays book titles, authors, categories and availability.

Supports searching by title or author.

Shows whether a book is available to reserve.

Reservations and Rentals

Allows a user to enter a name and choose a rental duration.

Records the booking date and return deadline.

Displays active rentals and the number of days remaining.

Supports renewing and returning active rentals.

 - Database Management

Stores book and booking records in a local Room database.

Uses a foreign key to connect bookings to books.

Uses booking statuses to represent pending, active and returned bookings.

Uses observable data streams to update the interface when database records change.

Includes sample book records for initial testing.

4. Architecture and Project Structure

The project separates database operations, business logic and the user interface.

com.example.kitabu
├── data
│   ── dao
│   ── database
│   ── entity
│   ── relation
├── repository
├── viewmodel
└── ui
├── navigation
├── screens
└── theme

data: Room entities, DAOs, database configuration and booking relationships.

repository: Provides an interface between the database and the ViewModel.

viewmodel: Manages screen state and user actions.

ui: Contains the Compose screens, navigation and theme.

5. Requirements

Android Studio

Android SDK compatible with the project configuration

An Android emulator or physical Android device

The project is configured with a minimum Android SDK version of 26.

6. How to Run the Application

Clone or download this repository.

Open the project in Android Studio.

Allow Gradle to sync and download the required dependencies.

Run the application on an emulator or connected Android device.

Open the Catalogue screen and test searching, reserving, renewing and returning books.

The application uses a local database. Sample books are inserted when the database is first created.