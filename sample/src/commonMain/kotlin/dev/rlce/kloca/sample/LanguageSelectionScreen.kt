package dev.rlce.kloca.sample

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import dev.rlce.kloca.generated.SampleStringKeys
import dev.rlce.kloca.runtime.Kloca
import dev.rlce.kloca.runtime.compose.localizedString
import dev.rlce.kloca.runtime.compose.useLanguageChange
import dev.rlce.kloca.runtime.compose.useLanguageModeChange

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LanguageSelectionScreen(
    onBackClick: () -> Unit,
) {
    var showSnackbar by remember { mutableStateOf(false) }
    var snackbarMessage by remember { mutableStateOf("") }

    // Use reactive language hooks for real-time updates
    val currentLanguage = useLanguageChange()
    val currentMode = useLanguageModeChange()
    val isUsingSystem = currentMode == Kloca.LanguageMode.SYSTEM
    val availableLanguages = remember { Kloca.getAvailableLanguages() }

    // Show snackbar when language changes
    if (showSnackbar) {
        LaunchedEffect(showSnackbar) {
            kotlinx.coroutines.delay(2000)
            showSnackbar = false
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(localizedString(SampleStringKeys.LANGUAGE_TITLE))
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Text("←") // Simple back arrow
                    }
                },
            )
        },
        snackbarHost = {
            if (showSnackbar) {
                Snackbar(
                    modifier = Modifier.padding(16.dp),
                ) {
                    Text(snackbarMessage)
                }
            }
        },
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
        ) {
            // Current language display
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                ),
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                ) {
                    Text(
                        text = localizedString(
                            SampleStringKeys.LANGUAGE_CURRENT,
                            currentLanguage ?: "System",
                        ),
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = localizedString(
                            if (isUsingSystem) {
                                SampleStringKeys.LANGUAGE_SYSTEM
                            } else {
                                "Mode: User Preference"
                            },
                        ),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f),
                    )
                }
            }

            // Language selection list
            Text(
                text = localizedString(SampleStringKeys.LANGUAGE_TITLE),
                style = MaterialTheme.typography.headlineSmall,
                modifier = Modifier.padding(bottom = 16.dp),
            )

            LazyColumn(
                modifier = Modifier.selectableGroup(),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                // System default option
                item {
                    LanguageOption(
                        languageCode = "system",
                        displayName = localizedString(SampleStringKeys.LANGUAGE_SYSTEM),
                        isSelected = isUsingSystem,
                        onSelect = {
                            Kloca.useSystemLanguage()
                            val systemText = Kloca.getString(SampleStringKeys.LANGUAGE_SYSTEM)
                            snackbarMessage = Kloca.getString(
                                SampleStringKeys.LANGUAGE_CHANGE_SUCCESS,
                                systemText,
                            )
                            showSnackbar = true
                        },
                    )
                }

                // Available languages from UserLanguageProvider
                items(availableLanguages) { languageCode ->
                    LanguageOption(
                        languageCode = languageCode,
                        displayName = languageCode.uppercase(),
                        isSelected = !isUsingSystem && currentLanguage == languageCode,
                        onSelect = {
                            Kloca.setUserLanguage(languageCode)
                            snackbarMessage = Kloca.getString(
                                SampleStringKeys.LANGUAGE_CHANGE_SUCCESS,
                                languageCode,
                            )
                            showSnackbar = true
                        },
                    )
                }
            }
        }
    }
}

@Composable
private fun LanguageOption(
    languageCode: String?,
    displayName: String,
    isSelected: Boolean,
    onSelect: () -> Unit,
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .selectable(
                selected = isSelected,
                onClick = onSelect,
                role = Role.RadioButton,
            ),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) {
                MaterialTheme.colorScheme.secondaryContainer
            } else {
                MaterialTheme.colorScheme.surface
            },
        ),
        border = if (isSelected) {
            CardDefaults.outlinedCardBorder().copy(
                width = 2.dp,
            )
        } else {
            CardDefaults.outlinedCardBorder()
        },
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            RadioButton(
                selected = isSelected,
                onClick = null, // handled by card
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(
                    text = displayName,
                    style = MaterialTheme.typography.bodyLarge,
                    color = if (isSelected) {
                        MaterialTheme.colorScheme.onSecondaryContainer
                    } else {
                        MaterialTheme.colorScheme.onSurface
                    },
                )
                if (languageCode != null) {
                    Text(
                        text = languageCode,
                        style = MaterialTheme.typography.bodySmall,
                        color = if (isSelected) {
                            MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.7f)
                        } else {
                            MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                        },
                    )
                }
            }
        }
    }
}
