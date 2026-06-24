package com.kmp.pyr.feature.planner

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import com.kmp.pyr.data.ProjectCategory
import com.kmp.pyr.ui.components.ChipRow
import com.kmp.pyr.ui.components.GoldButton
import com.kmp.pyr.ui.components.GoldOutlinedButton
import com.kmp.pyr.ui.theme.EgyptColors

@Composable
fun AddProjectDialog(
    onDismiss: () -> Unit,
    onConfirm: (name: String, category: ProjectCategory, durationDays: Int, milestones: Int) -> Unit,
) {
    var name by remember { mutableStateOf("") }
    var category by remember { mutableStateOf(ProjectCategory.MONUMENT) }
    var duration by remember { mutableStateOf(120f) }
    var milestones by remember { mutableStateOf(4f) }
    val focus = LocalFocusManager.current
    val valid = name.isNotBlank()

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = EgyptColors.NightCard,
        titleContentColor = EgyptColors.TextOnNight,
        textContentColor = EgyptColors.TextOnNight,
        title = { Text("Charter a New Project") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Project name") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
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
                Text("Category", color = EgyptColors.TextMuted)
                ChipRow(options = ProjectCategory.entries, selected = category, onSelect = { category = it }, label = { it.label })
                Text("Duration: ${duration.toInt()} days", color = EgyptColors.TextMuted)
                Slider(
                    value = duration, onValueChange = { duration = it }, valueRange = 30f..360f,
                    colors = SliderDefaults.colors(thumbColor = EgyptColors.GoldBright, activeTrackColor = EgyptColors.Gold, inactiveTrackColor = EgyptColors.NightStroke),
                )
                Text("Milestones: ${milestones.toInt()}", color = EgyptColors.TextMuted)
                Slider(
                    value = milestones, onValueChange = { milestones = it }, valueRange = 2f..8f, steps = 5,
                    colors = SliderDefaults.colors(thumbColor = EgyptColors.GoldBright, activeTrackColor = EgyptColors.Gold, inactiveTrackColor = EgyptColors.NightStroke),
                )
            }
        },
        confirmButton = {
            GoldButton("Charter", enabled = valid, onClick = { onConfirm(name.trim(), category, duration.toInt(), milestones.toInt()) })
        },
        dismissButton = { GoldOutlinedButton("Cancel", onClick = onDismiss) },
    )
}
