package com.kmp.pyr.feature.dashboard

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kmp.pyr.data.DailyStat
import com.kmp.pyr.data.Fmt
import com.kmp.pyr.data.KingdomRepository
import com.kmp.pyr.data.ResourceType
import com.kmp.pyr.feature.help.SCREEN_GUIDES
import com.kmp.pyr.navigation.Destination
import com.kmp.pyr.ui.components.EgyptCard
import com.kmp.pyr.ui.components.GoldButton
import com.kmp.pyr.ui.components.GoldOutlinedButton
import com.kmp.pyr.ui.components.HelpDialog
import com.kmp.pyr.ui.components.Hint
import com.kmp.pyr.ui.components.ScreenIntro
import com.kmp.pyr.ui.components.ScrollTimeline
import com.kmp.pyr.ui.components.SectionHeader
import com.kmp.pyr.ui.components.StatCard
import com.kmp.pyr.ui.components.charts.BarChart
import com.kmp.pyr.ui.components.charts.BarEntry
import com.kmp.pyr.ui.components.charts.LineChart
import com.kmp.pyr.ui.components.charts.LineSeries
import com.kmp.pyr.ui.components.charts.ProgressRing
import com.kmp.pyr.ui.motif.EgyptGlyph
import com.kmp.pyr.ui.motif.GlyphIcon
import com.kmp.pyr.ui.motif.GlyphMedallion
import com.kmp.pyr.ui.motif.OrnamentDivider
import com.kmp.pyr.ui.theme.EgyptColors

private val resourceGlyph = mapOf(
    ResourceType.GOLD to EgyptGlyph.SUN_DISC,
    ResourceType.GRAIN to EgyptGlyph.FEATHER,
    ResourceType.STONE to EgyptGlyph.PYRAMID,
    ResourceType.LABOR to EgyptGlyph.SCARAB,
)
private val resourceColor = mapOf(
    ResourceType.GOLD to EgyptColors.GoldBright,
    ResourceType.GRAIN to EgyptColors.Turquoise,
    ResourceType.STONE to EgyptColors.Sandstone,
    ResourceType.LABOR to EgyptColors.Lapis,
)

@Composable
fun DashboardScreen(repo: KingdomRepository, navigate: (Destination) -> Unit) {
    val snap = repo.snapshot
    val stats = snap.dailyStats
    val recent = stats.takeLast(30)
    var showRecord by remember { mutableStateOf(false) }
    var showHelp by remember { mutableStateOf(false) }
    val guide = SCREEN_GUIDES.getValue(Destination.DASHBOARD)

    LazyColumn(
        Modifier.fillMaxWidth(),
        contentPadding = androidx.compose.foundation.layout.PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        item {
            ScreenIntro(
                eyebrow = snap.prefs.kingdomName,
                title = "Kingdom Dashboard",
                subtitle = "${guide.subtitle} · ${Fmt.date(snap.today)}",
                onHelp = { showHelp = true },
            )
        }

        // Overview stat cards (2x2)
        item {
            val types = ResourceType.entries
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                types.chunked(2).forEach { rowTypes ->
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        rowTypes.forEach { type ->
                            StatCard(
                                label = type.label,
                                value = Fmt.compact(snap.resources[type] ?: 0.0),
                                delta = deltaFor(stats, type),
                                glyph = resourceGlyph[type]!!,
                                trend = recent.map { it.amount(type) },
                                accent = resourceColor[type]!!,
                                modifier = Modifier.weight(1f),
                            )
                        }
                    }
                }
            }
        }

        // Daily summary with productivity ring
        item {
            EgyptCard(Modifier.fillMaxWidth()) {
                SectionHeader("Today's Decree", eyebrow = "Daily Summary", glyph = EgyptGlyph.LOTUS)
                Spacer(Modifier.height(12.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    ProgressRing(
                        progress = stats.lastOrNull()?.productivity ?: 0f,
                        modifier = Modifier.height(110.dp).padding(end = 16.dp).then(Modifier.fillMaxWidth(0.38f)),
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(Fmt.percent(stats.lastOrNull()?.productivity ?: 0f), color = EgyptColors.GoldBright, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                            Text("Productivity", color = EgyptColors.TextMuted, fontSize = 10.sp)
                        }
                    }
                    Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        SummaryLine("Gold income / day", Fmt.signed(snap.rates[ResourceType.GOLD] ?: 0.0))
                        SummaryLine("Granary balance", Fmt.compact(snap.resources[ResourceType.GRAIN] ?: 0.0))
                        SummaryLine("Active projects", snap.projects.count { it.progress < 1f }.toString())
                        SummaryLine("Open milestones", snap.projects.sumOf { p -> p.milestones.count { !it.done } }.toString())
                    }
                }
            }
        }

        // Production explainer — answers "where does gold come from?"
        item {
            EgyptCard(Modifier.fillMaxWidth()) {
                SectionHeader("Daily Tribute", eyebrow = "Production", glyph = EgyptGlyph.SUN_DISC, caption = "Each day you collect tribute, every store grows by its rate below")
                Spacer(Modifier.height(12.dp))
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    ResourceType.entries.forEach { t ->
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            GlyphIcon(resourceGlyph[t]!!, size = 20.dp, tint = resourceColor[t]!!)
                            Text(Fmt.signed(snap.rates[t] ?: 0.0), color = EgyptColors.TextOnNight, fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                            Text("${t.label}/day", color = EgyptColors.TextMuted, fontSize = 10.sp)
                        }
                    }
                }
                Spacer(Modifier.height(14.dp))
                GoldButton(
                    "Collect Tribute · Advance a Day",
                    onClick = { repo.advanceDay() },
                    glyph = EgyptGlyph.ANKH,
                    modifier = Modifier.fillMaxWidth(),
                )
                Hint("Your treasury and trend update by exactly these amounts each day")
            }
        }

        // Line chart — resource trends
        item {
            EgyptCard(Modifier.fillMaxWidth()) {
                SectionHeader("Flow of the Nile", eyebrow = "30-Day Trend", glyph = EgyptGlyph.EYE_OF_HORUS, caption = "Resource levels over the last 30 days")
                Spacer(Modifier.height(12.dp))
                LineChart(
                    series = listOf(
                        LineSeries("Gold", EgyptColors.GoldBright, recent.map { it.gold }),
                        LineSeries("Grain", EgyptColors.Turquoise, recent.map { it.grain }),
                        LineSeries("Stone", EgyptColors.Sandstone, recent.map { it.stone }),
                    ),
                    xLabels = recent.map { Fmt.date(it.day) },
                    modifier = Modifier.fillMaxWidth().height(200.dp),
                )
                Hint("Tap any point to read exact values for that day")
            }
        }

        // Bar chart — today's resource comparison
        item {
            EgyptCard(Modifier.fillMaxWidth()) {
                SectionHeader("Royal Stores", eyebrow = "Current Holdings", glyph = EgyptGlyph.OBELISK)
                Spacer(Modifier.height(12.dp))
                BarChart(
                    entries = ResourceType.entries.map { t ->
                        BarEntry(t.label, listOf(resourceColor[t]!! to (snap.resources[t] ?: 0.0)))
                    },
                    modifier = Modifier.fillMaxWidth().height(170.dp),
                )
            }
        }

        // Achievements
        item {
            EgyptCard(Modifier.fillMaxWidth()) {
                SectionHeader("Honours of the Gods", eyebrow = "Achievements", glyph = EgyptGlyph.ANKH)
                Spacer(Modifier.height(12.dp))
                Row(
                    Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(14.dp),
                ) {
                    snap.achievements.forEach { a ->
                        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(horizontal = 2.dp)) {
                            GlyphMedallion(glyph = runCatching { EgyptGlyph.valueOf(a.glyph) }.getOrDefault(EgyptGlyph.ANKH), unlocked = a.unlocked)
                            Spacer(Modifier.height(4.dp))
                            Text(if (a.unlocked) "✓" else Fmt.percent(a.progress), color = if (a.unlocked) EgyptColors.GoldBright else EgyptColors.TextMuted, fontSize = 10.sp)
                        }
                    }
                }
            }
        }

        // Quick actions
        item {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                GoldButton("New Record", onClick = { showRecord = true }, glyph = EgyptGlyph.SUN_DISC, modifier = Modifier.weight(1f))
                GoldOutlinedButton("View Reports", onClick = { navigate(Destination.REPORTS) }, glyph = EgyptGlyph.FEATHER, modifier = Modifier.weight(1f))
            }
        }

        // Activity scroll
        item {
            SectionHeader("Scroll of Deeds", eyebrow = "Recent Activity", glyph = EgyptGlyph.SCARAB)
            OrnamentDivider()
            ScrollTimeline(entries = snap.activities.take(8), today = snap.today)
        }
    }

    if (showRecord) {
        QuickRecordDialog(
            onDismiss = { showRecord = false },
            onConfirm = { type, amount ->
                repo.addQuickRecord(type, amount)
                showRecord = false
            },
        )
    }
    if (showHelp) HelpDialog(guide.title, guide.points) { showHelp = false }
}

@Composable
private fun SummaryLine(label: String, value: String) {
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(label, color = EgyptColors.TextMuted, fontSize = 12.sp)
        Text(value, color = EgyptColors.TextOnNight, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
    }
}

private fun deltaFor(stats: List<DailyStat>, type: ResourceType): Double {
    if (stats.size < 2) return 0.0
    return stats.last().amount(type) - stats[stats.size - 2].amount(type)
}
