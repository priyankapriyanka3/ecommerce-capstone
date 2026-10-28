package com.booknest.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.booknest.app.presentation.navigation.MainScreen
import com.booknest.app.presentation.theme.BookNestTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val app = application as BookNestApplication
        val container = app.container

        setContent {
            val userProfile by container.getUserProfileUseCase().collectAsState(initial = null)
            val systemDark = isSystemInDarkTheme()

            val isDarkTheme = when (userProfile?.preferredTheme) {
                "dark" -> true
                "light" -> false
                else -> systemDark
            }

            BookNestTheme(darkTheme = isDarkTheme) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MainScreen(container = container)
                }
            }
        }
    }
}
