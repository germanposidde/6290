package com.kmp.pyr.feature.dashboard

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.kmp.pyr.data.ResourceType
import com.kmp.pyr.ui.components.ChipRow
import com.kmp.pyr.ui.components.GoldButton
import com.kmp.pyr.ui.components.GoldOutlinedButton
import com.kmp.pyr.ui.theme.EgyptColors

@Composable
fun QuickRecordDialog(
    onDismiss: () -> Unit,
    onConfirm: (ResourceType, Double) -> Unit,
) {
    var type by remember { mutableStateOf(ResourceType.GOLD) }
    var amountText by remember { mutableStateOf("") }
    val amount = amountText.toDoubleOrNull()
    val valid = amount != null && amount > 0
    val focus = LocalFocusManager.current

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = EgyptColors.NightCard,
        titleContentColor = EgyptColors.TextOnNight,
        textContentColor = EgyptColors.TextOnNight,
        title = { Text("Inscribe a New Record") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text("Resource", color = EgyptColors.TextMuted)
                ChipRow(
                    options = ResourceType.entries,
                    selected = type,
                    onSelect = { type = it },
                    label = { it.label },
                )
                OutlinedTextField(
                    value = amountText,
                    onValueChange = { s -> amountText = s.filter { it.isDigit() || it == '.' } },
                    label = { Text("Amount") },
                    singleLine = true,
                    isError = amountText.isNotEmpty() && !valid,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal, imeAction = ImeAction.Done),
                    keyboardActions = KeyboardActions(onDone = { focus.clearFocus() }),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = EgyptColors.Gold,
                        unfocusedBorderColor = EgyptColors.NightStroke,
                        focusedLabelColor = EgyptColors.GoldBright,
                        cursorColor = EgyptColors.GoldBright,
                        focusedTextColor = EgyptColors.TextOnNight,
                        unfocusedTextColor = EgyptColors.TextOnNight,
                    ),
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        },
        confirmButton = {
            GoldButton("Inscribe", enabled = valid, onClick = { onConfirm(type, amount ?: 0.0) })
        },
        dismissButton = {
            GoldOutlinedButton("Cancel", onClick = onDismiss)
        },
    )
}
