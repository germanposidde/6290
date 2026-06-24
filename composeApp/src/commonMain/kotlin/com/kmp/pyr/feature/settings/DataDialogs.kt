package com.kmp.pyr.feature.settings

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kmp.pyr.ui.components.GoldButton
import com.kmp.pyr.ui.components.GoldOutlinedButton
import com.kmp.pyr.ui.theme.EgyptColors

/** Shows the exported snapshot JSON with a copy-to-clipboard action. */
@Composable
fun BackupDialog(json: String, onDismiss: () -> Unit) {
    val clipboard = LocalClipboardManager.current
    var copied by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = EgyptColors.NightCard,
        titleContentColor = EgyptColors.TextOnNight,
        textContentColor = EgyptColors.TextOnNight,
        title = { Text("Royal Backup Scroll") },
        text = {
            Column {
                Text("Your kingdom is sealed below. Copy and keep it safe.", color = EgyptColors.TextMuted, fontSize = 12.sp)
                Column(
                    Modifier.fillMaxWidth().heightIn(max = 280.dp).verticalScroll(rememberScrollState()),
                ) {
                    Text(json, color = EgyptColors.TextOnNight, fontSize = 10.sp)
                }
            }
        },
        confirmButton = {
            GoldButton(if (copied) "Copied ✓" else "Copy", onClick = {
                clipboard.setText(AnnotatedString(json)); copied = true
            })
        },
        dismissButton = { GoldOutlinedButton("Close", onClick = onDismiss) },
    )
}

/** Paste a backup JSON to restore. Validates before applying. */
@Composable
fun RestoreDialog(onDismiss: () -> Unit, onRestore: (String) -> Boolean) {
    var text by remember { mutableStateOf("") }
    var error by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = EgyptColors.NightCard,
        titleContentColor = EgyptColors.TextOnNight,
        textContentColor = EgyptColors.TextOnNight,
        title = { Text("Restore from Scroll") },
        text = {
            Column {
                Text("Paste a previously exported backup.", color = EgyptColors.TextMuted, fontSize = 12.sp)
                OutlinedTextField(
                    value = text,
                    onValueChange = { text = it; error = false },
                    label = { Text("Backup JSON") },
                    isError = error,
                    modifier = Modifier.fillMaxWidth().heightIn(min = 120.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = EgyptColors.Gold,
                        unfocusedBorderColor = EgyptColors.NightStroke,
                        focusedLabelColor = EgyptColors.GoldBright,
                        cursorColor = EgyptColors.GoldBright,
                        focusedTextColor = EgyptColors.TextOnNight,
                        unfocusedTextColor = EgyptColors.TextOnNight,
                    ),
                )
                if (error) Text("That scroll could not be deciphered.", color = EgyptColors.Loss, fontSize = 11.sp)
            }
        },
        confirmButton = {
            GoldButton("Restore", enabled = text.isNotBlank(), onClick = {
                if (onRestore(text)) onDismiss() else error = true
            })
        },
        dismissButton = { GoldOutlinedButton("Cancel", onClick = onDismiss) },
    )
}

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
