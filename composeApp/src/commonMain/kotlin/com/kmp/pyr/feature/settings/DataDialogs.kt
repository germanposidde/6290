package com.kmp.pyr.feature.settings

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import com.kmp.pyr.ui.components.GoldButton
import com.kmp.pyr.ui.components.GoldOutlinedButton
import com.kmp.pyr.ui.theme.EgyptColors

@Composable
fun ConfirmDialog(
    title: String,
    message: String,
    confirmText: String,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = EgyptColors.NightCard,
        titleContentColor = EgyptColors.TextOnNight,
        textContentColor = EgyptColors.TextOnNight,
        title = { Text(title) },
        text = { Text(message, color = EgyptColors.TextMuted) },
        confirmButton = { GoldButton(confirmText, onClick = onConfirm) },
        dismissButton = { GoldOutlinedButton("Cancel", onClick = onDismiss) },
    )
}
