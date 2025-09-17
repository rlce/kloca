package dev.rlce.kloca.sample

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.intl.Locale
import androidx.compose.ui.text.toUpperCase
import androidx.compose.ui.unit.dp
import dev.rlce.kloca.generated.SampleStringKeys
import dev.rlce.kloca.runtime.Kloca
import dev.rlce.kloca.runtime.compose.localizedString
import dev.rlce.kloca.runtime.compose.useLanguageChange
import dev.rlce.kloca.runtime.compose.useLanguageModeChange

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    onLanguageSettingsClick: () -> Unit,
) {
    var showBottomSheet by remember { mutableStateOf(false) }
    val bottomSheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true,
    )
    val scope = rememberCoroutineScope()

    Scaffold { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
        ) {
            // Main content centered
            Column(
                modifier = Modifier.align(Alignment.Center),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
            ) {
                Text(
                    text = localizedString(SampleStringKeys.GREETING_HELLO_WITH_NAME, "World"),
                    style = MaterialTheme.typography.headlineLarge,
                    modifier = Modifier.padding(32.dp),
                )
            }

            // Bottom button
            Button(
                onClick = { showBottomSheet = true },
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .padding(16.dp),
            ) {
                Text(localizedString(SampleStringKeys.NAVIGATION_LANGUAGE))
            }
        }

        // Language chooser bottom sheet
        if (showBottomSheet) {
            ModalBottomSheet(
                onDismissRequest = { showBottomSheet = false },
                sheetState = bottomSheetState,
            ) {
                LanguageChooserContent(
                    onLanguageSelected = {
                        showBottomSheet = false
                    },
                )
            }
        }
    }
}

@Composable
private fun LanguageChooserContent(
    onLanguageSelected: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
    ) {
        Text(
            text = localizedString(SampleStringKeys.LANGUAGE_TITLE),
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.padding(bottom = 24.dp),
        )

        LanguageSelectionOptions(
            onLanguageSelected = onLanguageSelected,
        )
    }
}

@Composable
private fun LanguageSelectionOptions(
    onLanguageSelected: () -> Unit,
) {
    val currentLanguage = useLanguageChange()
    val currentMode = useLanguageModeChange()
    val isUsingSystem = currentMode == Kloca.LanguageMode.SYSTEM
    val availableLanguages = remember { Kloca.getAvailableLanguages() }

    Column(
        modifier = Modifier.selectableGroup(),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        // System language option
        LanguageOption(
            languageCode = "system",
            displayName = localizedString(SampleStringKeys.LANGUAGE_SYSTEM),
            isSelected = isUsingSystem,
            onSelect = {
                Kloca.useSystemLanguage()
                onLanguageSelected()
            },
        )

        // Available user languages
        for (languageCode in availableLanguages) {
            LanguageOption(
                languageCode = languageCode,
                displayName = languageCode.toUpperCase(Locale.current),
                isSelected = !isUsingSystem && currentLanguage == languageCode,
                onSelect = {
                    Kloca.setUserLanguage(languageCode)
                    onLanguageSelected()
                },
            )
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
private fun LanguageOption(
    languageCode: String,
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
                MaterialTheme.colorScheme.primaryContainer
            } else {
                MaterialTheme.colorScheme.surface
            },
        ),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            RadioButton(
                selected = isSelected,
                onClick = null,
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(
                    text = displayName,
                    style = MaterialTheme.typography.bodyLarge,
                    color = if (isSelected) {
                        MaterialTheme.colorScheme.onPrimaryContainer
                    } else {
                        MaterialTheme.colorScheme.onSurface
                    },
                )
                if (languageCode != "system") {
                    Text(
                        text = languageCode,
                        style = MaterialTheme.typography.bodySmall,
                        color = if (isSelected) {
                            MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
                        } else {
                            MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                        },
                    )
                }
            }
        }
    }
}
