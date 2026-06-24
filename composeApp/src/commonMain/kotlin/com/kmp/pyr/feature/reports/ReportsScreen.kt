package com.kmp.pyr.feature.reports

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kmp.pyr.data.Analytics
import com.kmp.pyr.data.Fmt
import com.kmp.pyr.data.KingdomRepository
import com.kmp.pyr.data.PeriodReport
import com.kmp.pyr.data.ReportPeriod
import com.kmp.pyr.data.ResourceType
import com.kmp.pyr.feature.help.SCREEN_GUIDES
import com.kmp.pyr.navigation.Destination
import com.kmp.pyr.ui.components.ChipRow
import com.kmp.pyr.ui.components.EgyptCard
import com.kmp.pyr.ui.components.HelpDialog
import com.kmp.pyr.ui.components.ScreenIntro
import com.kmp.pyr.ui.components.SectionHeader
import com.kmp.pyr.ui.components.charts.BarChart
import com.kmp.pyr.ui.components.charts.BarEntry
import com.kmp.pyr.ui.components.charts.LineChart
import com.kmp.pyr.ui.components.charts.LineSeries
import com.kmp.pyr.ui.components.charts.PieChart
import com.kmp.pyr.ui.components.charts.PieSlice
import com.kmp.pyr.ui.motif.EgyptGlyph
import com.kmp.pyr.ui.motif.OrnamentDivider
import com.kmp.pyr.ui.motif.papyrusSurface
import com.kmp.pyr.ui.theme.EgyptColors

private val resColor = mapOf(
    ResourceType.GOLD to EgyptColors.GoldBright,
    ResourceType.GRAIN to EgyptColors.Turquoise,
    ResourceType.STONE to EgyptColors.Sandstone,
    ResourceType.LABOR to EgyptColors.Lapis,
)

@Composable
fun ReportsScreen(repo: KingdomRepository) {
    val snap = repo.snapshot
    var period by remember { mutableStateOf(ReportPeriod.MONTHLY) }
    val report = remember(snap, period) { Analytics.report(snap, period) }
    var showHelp by remember { mutableStateOf(false) }
    val guide = SCREEN_GUIDES.getValue(Destination.REPORTS)

    LazyColumn(
        Modifier.fillMaxSize(),
        contentPadding = androidx.compose.foundation.layout.PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        item {
            ScreenIntro(
                eyebrow = "Pharaoh Reports",
                title = "Annals of the Realm",
                subtitle = guide.subtitle,
                onHelp = { showHelp = true },
            )
        }

        item {
            Column {
                Text("Choose a reporting period:", color = EgyptColors.TextMuted, fontSize = 12.sp)
                Spacer(Modifier.height(6.dp))
                ChipRow(options = ReportPeriod.entries, selected = period, onSelect = { period = it }, label = { it.label })
            }
        }

        // Headline insight cards
        item {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                InsightCard("Total Output", Fmt.compact(report.grandTotal), Fmt.signed(report.growth.toDouble() * 100) + "%", report.growth >= 0, Modifier.weight(1f))
                InsightCard("Productivity", Fmt.percent(report.avgProductivity), deltaPct(report.avgProductivity, report.prevAvgProductivity), report.avgProductivity >= report.prevAvgProductivity, Modifier.weight(1f))
            }
        }

        // Pie — resource distribution
        item {
            EgyptCard(Modifier.fillMaxWidth()) {
                SectionHeader("Distribution", eyebrow = "${period.label} Share", glyph = EgyptGlyph.SUN_DISC)
                Spacer(Modifier.height(12.dp))
                PieChart(
                    slices = ResourceType.entries.map { t -> PieSlice(t.label, report.totals[t] ?: 0.0, resColor[t]!!) },
                    centerLabel = period.label,
                )
            }
        }

        // Line — trend over the period
        item {
            EgyptCard(Modifier.fillMaxWidth()) {
                SectionHeader("Trend", eyebrow = "Over Time", glyph = EgyptGlyph.EYE_OF_HORUS)
                Spacer(Modifier.height(12.dp))
                LineChart(
                    series = listOf(
                        LineSeries("Gold", EgyptColors.GoldBright, report.series.map { it.gold }),
                        LineSeries("Grain", EgyptColors.Turquoise, report.series.map { it.grain }),
                    ),
                    xLabels = report.series.map { Fmt.date(it.day) },
                    modifier = Modifier.fillMaxWidth().height(190.dp),
                )
            }
        }

        // Comparison bar — this period vs previous
        item {
            EgyptCard(Modifier.fillMaxWidth()) {
                SectionHeader("Period Comparison", eyebrow = "Now vs Previous", glyph = EgyptGlyph.OBELISK)
                Spacer(Modifier.height(12.dp))
                BarChart(
                    entries = ResourceType.entries.map { t ->
                        BarEntry(
                            t.label.take(5),
                            listOf(
                                EgyptColors.Gold to (report.totals[t] ?: 0.0),
                                EgyptColors.Lapis to (report.previousTotals[t] ?: 0.0),
                            ),
                        )
                    },
                    legend = listOf("Current" to EgyptColors.Gold, "Previous" to EgyptColors.Lapis),
                    modifier = Modifier.fillMaxWidth().height(180.dp),
                )
            }
        }

        // Trend analysis / insights
        item {
            EgyptCard(Modifier.fillMaxWidth()) {
                SectionHeader("Oracle's Insights", eyebrow = "Analysis", glyph = EgyptGlyph.FEATHER)
                Spacer(Modifier.height(10.dp))
                report.insights().forEach { line ->
                    Row(Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
                        Text("𓂀  ", color = EgyptColors.Gold, fontSize = 13.sp)
                        Text(line, color = EgyptColors.TextOnNight, fontSize = 13.sp)
                    }
                }
            }
        }

        // Export-ready preview
        item {
            SectionHeader("Export Preview", eyebrow = "Royal Decree", glyph = EgyptGlyph.SCARAB, caption = "A print-ready summary; back it up from the Temple screen")
            OrnamentDivider()
            ReportPreview(report, snap.prefs.kingdomName, snap.today)
        }
    }

    if (showHelp) HelpDialog(guide.title, guide.points) { showHelp = false }
}

@Composable
private fun InsightCard(label: String, value: String, change: String, up: Boolean, modifier: Modifier = Modifier) {
    EgyptCard(modifier, contentPadding = 14) {
        Text(label, color = EgyptColors.TextMuted, fontSize = 11.sp)
        Text(value, color = EgyptColors.GoldBright, fontWeight = FontWeight.Bold, fontSize = 22.sp)
        Text(change, color = if (up) EgyptColors.Profit else EgyptColors.Loss, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
    }
}

@Composable
private fun ReportPreview(report: PeriodReport, kingdom: String, today: Int) {
    Column(
        Modifier.fillMaxWidth().papyrusSurface().padding(18.dp),
    ) {
        Text(kingdom, color = EgyptColors.TextOnSand, fontWeight = FontWeight.Bold, fontSize = 16.sp)
        Text("${report.period.label} Report · ${Fmt.date(today)}", color = EgyptColors.GoldDeep, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
        Spacer(Modifier.height(10.dp))
        ResourceType.entries.forEach { t ->
            Row(Modifier.fillMaxWidth().padding(vertical = 2.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(t.label, color = EgyptColors.TextOnSand, fontSize = 13.sp)
                Text(Fmt.number(report.totals[t] ?: 0.0), color = EgyptColors.TextOnSand, fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
            }
        }
        Spacer(Modifier.height(8.dp))
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text("Grand total", color = EgyptColors.TextOnSand, fontWeight = FontWeight.Bold, fontSize = 14.sp)
            Text(Fmt.number(report.grandTotal), color = EgyptColors.TextOnSand, fontWeight = FontWeight.Bold, fontSize = 14.sp)
        }
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text("Avg productivity", color = EgyptColors.TextOnSand, fontSize = 13.sp)
            Text(Fmt.percent(report.avgProductivity), color = EgyptColors.TextOnSand, fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
        }
        Spacer(Modifier.height(8.dp))
        Text("Sealed by the royal scribe ✦", color = EgyptColors.GoldDeep, fontSize = 11.sp, fontWeight = FontWeight.Medium)
    }
}

private fun deltaPct(cur: Float, prev: Float): String {
    if (prev == 0f) return "—"
    val pct = (cur - prev) / prev * 100
    return (if (pct >= 0) "+" else "") + pct.toInt() + "%"
}

private fun PeriodReport.insights(): List<String> = buildList {
    add("Strongest growth in ${bestResource.label} this ${period.label.lowercase()}.")
    add("${worstResource.label} lagged; consider reallocating labor.")
    val g = (growth * 100).toInt()
    add(if (growth >= 0) "Output rose $g% versus the previous period." else "Output fell ${-g}% versus the previous period.")
    val prod = (avgProductivity * 100).toInt()
    add("Average productivity held at $prod% across the period.")
    if (avgProductivity >= prevAvgProductivity) add("Productivity is trending upward — momentum is strong.")
}
