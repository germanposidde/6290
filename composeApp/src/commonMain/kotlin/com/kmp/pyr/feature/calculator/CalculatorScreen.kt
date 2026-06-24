package com.kmp.pyr.feature.calculator

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kmp.pyr.data.CalcRecord
import com.kmp.pyr.data.Fmt
import com.kmp.pyr.data.KingdomRepository
import com.kmp.pyr.data.ResourceType
import com.kmp.pyr.feature.help.SCREEN_GUIDES
import com.kmp.pyr.navigation.Destination
import com.kmp.pyr.ui.components.ChipRow
import com.kmp.pyr.ui.components.EgyptCard
import com.kmp.pyr.ui.components.GoldButton
import com.kmp.pyr.ui.components.HelpDialog
import com.kmp.pyr.ui.components.Hint
import com.kmp.pyr.ui.components.NumberField
import com.kmp.pyr.ui.components.ScreenIntro
import com.kmp.pyr.ui.components.SectionHeader
import com.kmp.pyr.ui.components.charts.LineChart
import com.kmp.pyr.ui.components.charts.LineSeries
import com.kmp.pyr.ui.components.charts.PieChart
import com.kmp.pyr.ui.components.charts.PieSlice
import com.kmp.pyr.ui.components.dismissKeyboardOnTap
import com.kmp.pyr.ui.motif.EgyptGlyph
import com.kmp.pyr.ui.theme.EgyptColors

private val revenueResources = listOf(ResourceType.GOLD, ResourceType.GRAIN, ResourceType.STONE)

private enum class HistFilter(val label: String) { ALL("All"), PROFIT("Profit"), LOSS("Loss") }
private enum class HistSort(val label: String) { NEWEST("Newest"), NET("Net value") }

private val resColor = mapOf(
    ResourceType.GOLD to EgyptColors.GoldBright,
    ResourceType.GRAIN to EgyptColors.Turquoise,
    ResourceType.STONE to EgyptColors.Sandstone,
    ResourceType.LABOR to EgyptColors.Carnelian,
)

@Composable
fun CalculatorScreen(repo: KingdomRepository) {
    val snap = repo.snapshot
    val focus = LocalFocusManager.current

    // Editable inputs, seeded from current kingdom values.
    val amounts = remember {
        mutableStateMapOf<ResourceType, String>().apply {
            ResourceType.entries.forEach { put(it, ((snap.resources[it] ?: 0.0)).toInt().toString()) }
        }
    }
    val rates = remember {
        mutableStateMapOf<ResourceType, String>().apply {
            ResourceType.entries.forEach { put(it, ((snap.rates[it] ?: 1.0)).toInt().toString()) }
        }
    }
    var days by remember { mutableStateOf(120f) }

    fun amt(t: ResourceType) = amounts[t]?.toDoubleOrNull() ?: 0.0
    fun rate(t: ResourceType) = rates[t]?.toDoubleOrNull() ?: 0.0

    val d = days.toInt()
    val revenue = revenueResources.sumOf { amt(it) * rate(it) } * d / 30.0
    val cost = amt(ResourceType.LABOR) * rate(ResourceType.LABOR) * d / 30.0
    val net = revenue - cost
    val perDay = if (d == 0) 0.0 else net / d
    var showHelp by remember { mutableStateOf(false) }
    val guide = SCREEN_GUIDES.getValue(Destination.CALCULATOR)

    LazyColumn(
        Modifier.fillMaxSize().imePadding().dismissKeyboardOnTap(),
        contentPadding = androidx.compose.foundation.layout.PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        item {
            ScreenIntro(
                eyebrow = "Resource Calculator",
                title = "Scales of Ma'at",
                subtitle = guide.subtitle,
                onHelp = { showHelp = true },
            )
        }

        // Inputs
        item {
            EgyptCard(Modifier.fillMaxWidth()) {
                SectionHeader("Ledger Inputs", eyebrow = "Production & Value", glyph = EgyptGlyph.EYE_OF_HORUS, caption = "Left: amount produced per month. Right: value per unit.")
                Spacer(Modifier.height(12.dp))
                ResourceType.entries.forEachIndexed { i, t ->
                    val last = i == ResourceType.entries.lastIndex
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        NumberField(
                            value = amounts[t] ?: "",
                            onValueChange = { amounts[t] = it },
                            label = "${t.label}/mo",
                            modifier = Modifier.weight(1f),
                        )
                        NumberField(
                            value = rates[t] ?: "",
                            onValueChange = { rates[t] = it },
                            label = "Value/unit",
                            modifier = Modifier.weight(1f),
                            imeAction = if (last) ImeAction.Done else ImeAction.Next,
                            keyboardActions = if (last) KeyboardActions(onDone = { focus.clearFocus() }) else KeyboardActions.Default,
                        )
                    }
                    Spacer(Modifier.height(10.dp))
                }
                Hint("Labor counts as a cost; Gold, Grain and Stone earn revenue", glyph = EgyptGlyph.SCARAB)
                Spacer(Modifier.height(8.dp))
                Text("Projection horizon: $d days", color = EgyptColors.TextMuted, fontSize = 13.sp)
                Slider(
                    value = days,
                    onValueChange = { days = it },
                    valueRange = 7f..360f,
                    colors = SliderDefaults.colors(
                        thumbColor = EgyptColors.GoldBright,
                        activeTrackColor = EgyptColors.Gold,
                        inactiveTrackColor = EgyptColors.NightStroke,
                    ),
                )
            }
        }

        // Profit / loss panel
        item {
            EgyptCard(Modifier.fillMaxWidth()) {
                SectionHeader("Judgement of Profit", eyebrow = "Analysis", glyph = EgyptGlyph.FEATHER)
                Spacer(Modifier.height(12.dp))
                AnalysisLine("Projected revenue", Fmt.number(revenue), EgyptColors.Profit)
                AnalysisLine("Projected cost (labor)", Fmt.number(cost), EgyptColors.Loss)
                Spacer(Modifier.height(6.dp))
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Text(if (net >= 0) "Net Surplus" else "Net Deficit", color = EgyptColors.TextOnNight, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    Text(Fmt.signed(net), color = if (net >= 0) EgyptColors.Profit else EgyptColors.Loss, fontWeight = FontWeight.Bold, fontSize = 20.sp)
                }
                Text("≈ ${Fmt.signed(perDay)} per day", color = EgyptColors.TextMuted, fontSize = 12.sp)
            }
        }

        // Revenue mix pie
        item {
            EgyptCard(Modifier.fillMaxWidth()) {
                SectionHeader("Mix of Tribute", eyebrow = "Revenue Share", glyph = EgyptGlyph.SUN_DISC)
                Spacer(Modifier.height(12.dp))
                PieChart(
                    slices = revenueResources.map { t ->
                        PieSlice(t.label, amt(t) * rate(t), resColor[t]!!)
                    },
                    centerLabel = "Value",
                )
            }
        }

        // Projection line
        item {
            EgyptCard(Modifier.fillMaxWidth()) {
                SectionHeader("Path of Fortune", eyebrow = "Cumulative Net", glyph = EgyptGlyph.OBELISK)
                Spacer(Modifier.height(12.dp))
                val steps = 24
                val cumulative = (0..steps).map { i -> perDay * (d * i / steps) }
                LineChart(
                    series = listOf(LineSeries("Net", if (net >= 0) EgyptColors.Profit else EgyptColors.Loss, cumulative)),
                    xLabels = (0..steps).map { "Day ${d * it / steps}" },
                    modifier = Modifier.fillMaxWidth().height(170.dp),
                )
            }
        }

        // Save
        item {
            GoldButton(
                "Save Projection to Archive",
                enabled = revenue > 0 || cost > 0,
                onClick = {
                    repo.addCalcRecord(
                        title = "Projection · ${Fmt.date(snap.today)}",
                        amounts = ResourceType.entries.associateWith { amt(it) },
                        rates = ResourceType.entries.associateWith { rate(it) },
                        projectionDays = d,
                        revenue = revenue,
                        cost = cost,
                    )
                },
                glyph = EgyptGlyph.SCARAB,
                modifier = Modifier.fillMaxWidth(),
            )
        }

        item { HistoryHeaderAndList(snap.calcRecords) }
    }

    if (showHelp) HelpDialog(guide.title, guide.points) { showHelp = false }
}

@Composable
private fun HistoryHeaderAndList(records: List<CalcRecord>) {
    var filter by remember { mutableStateOf(HistFilter.ALL) }
    var sort by remember { mutableStateOf(HistSort.NEWEST) }

    val shown = records
        .filter {
            when (filter) {
                HistFilter.ALL -> true
                HistFilter.PROFIT -> it.isProfit
                HistFilter.LOSS -> !it.isProfit
            }
        }
        .let { list ->
            when (sort) {
                HistSort.NEWEST -> list.sortedByDescending { it.day }
                HistSort.NET -> list.sortedByDescending { it.net }
            }
        }

    Column {
        SectionHeader("Archive of Reckonings", eyebrow = "History", glyph = EgyptGlyph.SCARAB)
        Spacer(Modifier.height(10.dp))
        ChipRow(options = HistFilter.entries, selected = filter, onSelect = { filter = it }, label = { it.label })
        Spacer(Modifier.height(8.dp))
        ChipRow(options = HistSort.entries, selected = sort, onSelect = { sort = it }, label = { "Sort: ${it.label}" })
        Spacer(Modifier.height(12.dp))
        if (shown.isEmpty()) {
            Text("No reckonings recorded.", color = EgyptColors.TextMuted, fontSize = 13.sp)
        } else {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                shown.forEach { RecordRow(it) }
            }
        }
    }
}

@Composable
private fun RecordRow(r: CalcRecord) {
    EgyptCard(Modifier.fillMaxWidth(), contentPadding = 12) {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Column(Modifier.weight(1f)) {
                Text(r.title, color = EgyptColors.TextOnNight, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                Text("${r.projectionDays}-day horizon · rev ${Fmt.compact(r.revenue)} · cost ${Fmt.compact(r.cost)}", color = EgyptColors.TextMuted, fontSize = 11.sp)
            }
            Box(contentAlignment = Alignment.CenterEnd) {
                Text(Fmt.signed(r.net), color = if (r.isProfit) EgyptColors.Profit else EgyptColors.Loss, fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }
        }
    }
}

@Composable
private fun AnalysisLine(label: String, value: String, color: androidx.compose.ui.graphics.Color) {
    Row(Modifier.fillMaxWidth().padding(vertical = 2.dp), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(label, color = EgyptColors.TextMuted, fontSize = 13.sp)
        Text(value, color = color, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
    }
}
