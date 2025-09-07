package dev.rlce.kloca.sample

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable

@Composable
fun SampleApp() {
    MaterialTheme {
        MainScreen(
            onLanguageSettingsClick = { /* Not used with bottom sheet */ },
        )
    }
}
