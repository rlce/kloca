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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
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

    Scaffold { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
        ) {
            PlaceholderDemo(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(bottom = 72.dp),
            )

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
private fun PlaceholderDemo(modifier: Modifier = Modifier) {
    var name by remember { mutableStateOf("Alex") }
    var count by remember { mutableStateOf("3") }
    var price by remember { mutableStateOf("9.95") }
    var enabled by remember { mutableStateOf(true) }

    val countArgument = count.toIntOrNull() ?: 0
    val priceArgument = price.toDoubleOrNull() ?: 0.0

    Column(
        modifier = modifier
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Text(
            text = localizedString(SampleStringKeys.PLACEHOLDER_DEMO_TITLE),
            style = MaterialTheme.typography.headlineMedium,
        )
        Text(
            text = localizedString(
                SampleStringKeys.PLACEHOLDER_DEMO_DESCRIPTION,
                name,
                countArgument,
                priceArgument,
                enabled,
            ),
            style = MaterialTheme.typography.bodyMedium,
        )

        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text(localizedString(SampleStringKeys.PLACEHOLDER_DEMO_NAME_LABEL)) },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
        )
        OutlinedTextField(
            value = count,
            onValueChange = { count = it },
            label = { Text(localizedString(SampleStringKeys.PLACEHOLDER_DEMO_COUNT_LABEL)) },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
        )
        OutlinedTextField(
            value = price,
            onValueChange = { price = it },
            label = { Text(localizedString(SampleStringKeys.PLACEHOLDER_DEMO_PRICE_LABEL)) },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(localizedString(SampleStringKeys.PLACEHOLDER_DEMO_ENABLED_LABEL))
            Switch(checked = enabled, onCheckedChange = { enabled = it })
        }

        Card(modifier = Modifier.fillMaxWidth()) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                Text(
                    text = localizedString(
                        SampleStringKeys.PLACEHOLDER_DEMO_SUMMARY,
                        name,
                        countArgument,
                        priceArgument,
                        enabled,
                    ),
                    style = MaterialTheme.typography.titleMedium,
                )
                Text(
                    localizedString(
                        SampleStringKeys.PLACEHOLDER_DEMO_REORDERED,
                        name,
                        countArgument,
                        priceArgument,
                    ),
                )
                Text(localizedString(SampleStringKeys.PLACEHOLDER_DEMO_REPEATED, name))
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
