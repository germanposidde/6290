package com.kmp.pyr.ui.components

import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import com.kmp.pyr.ui.theme.EgyptColors

/** Themed numeric field that only accepts digits and a single dot. */
@Composable
fun NumberField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    imeAction: ImeAction = ImeAction.Next,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
) {
    OutlinedTextField(
        value = value,
        onValueChange = { s -> onValueChange(sanitizeNumber(s)) },
        label = { Text(label) },
        singleLine = true,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal, imeAction = imeAction),
        keyboardActions = keyboardActions,
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = EgyptColors.Gold,
            unfocusedBorderColor = EgyptColors.NightStroke,
            focusedLabelColor = EgyptColors.GoldBright,
            unfocusedLabelColor = EgyptColors.TextMuted,
            cursorColor = EgyptColors.GoldBright,
            focusedTextColor = EgyptColors.TextOnNight,
            unfocusedTextColor = EgyptColors.TextOnNight,
        ),
        modifier = modifier,
    )
}

private fun sanitizeNumber(s: String): String {
    val filtered = s.filter { it.isDigit() || it == '.' }
    val firstDot = filtered.indexOf('.')
    if (firstDot == -1) return filtered
    // keep only first dot
    return filtered.substring(0, firstDot + 1) + filtered.substring(firstDot + 1).replace(".", "")
}
