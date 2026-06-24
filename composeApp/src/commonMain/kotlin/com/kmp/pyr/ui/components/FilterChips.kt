package com.kmp.pyr.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.kmp.pyr.ui.theme.EgyptColors

/** A scrollable row of single-select filter chips themed gold-on-night. */
@Composable
fun <T> ChipRow(
    options: List<T>,
    selected: T,
    onSelect: (T) -> Unit,
    modifier: Modifier = Modifier,
    label: (T) -> String,
) {
    Row(
        modifier.horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        options.forEach { option ->
            val isSel = option == selected
            FilterChip(
                selected = isSel,
                onClick = { onSelect(option) },
                label = { Text(label(option)) },
                colors = FilterChipDefaults.filterChipColors(
                    containerColor = EgyptColors.NightRaised,
                    labelColor = EgyptColors.TextMuted,
                    selectedContainerColor = EgyptColors.Gold,
                    selectedLabelColor = EgyptColors.NightDeep,
                ),
                border = FilterChipDefaults.filterChipBorder(
                    enabled = true,
                    selected = isSel,
                    borderColor = EgyptColors.GoldSoft,
                    selectedBorderColor = EgyptColors.Gold,
                ),
            )
        }
    }
}
