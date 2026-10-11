package com.example.kitabu

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.example.kitabu.data.database.KitabuDatabase
import com.example.kitabu.repository.KitabuRepositoryImpl
import com.example.kitabu.ui.navigation.KitabuNavigation
import com.example.kitabu.ui.theme.KitabuTheme
import com.example.kitabu.viewmodel.KitabuViewModelFactory

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Create or retrieve the app's singleton Room database.
        val database = KitabuDatabase.getDatabase(
            applicationContext
        )

        // Connect the repository to the database.
        val repository = KitabuRepositoryImpl(database)

        // Provide the repository to the ViewModel.
        val viewModelFactory = KitabuViewModelFactory(repository)

        setContent {
            KitabuTheme {
                Surface(
                    modifier = Modifier.fillMaxSize()
                ) {
                    KitabuNavigation(
                        viewModelFactory = viewModelFactory
                    )
                }
            }
        }
    }
}