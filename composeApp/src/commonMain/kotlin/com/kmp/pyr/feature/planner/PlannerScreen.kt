package com.kmp.pyr.feature.planner

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kmp.pyr.data.Analytics
import com.kmp.pyr.data.Fmt
import com.kmp.pyr.data.KingdomRepository
import com.kmp.pyr.data.Project
import com.kmp.pyr.data.ProjectCategory
import com.kmp.pyr.feature.help.SCREEN_GUIDES
import com.kmp.pyr.navigation.Destination
import com.kmp.pyr.ui.components.EgyptCard
import com.kmp.pyr.ui.components.GoldButton
import com.kmp.pyr.ui.components.HelpDialog
import com.kmp.pyr.ui.components.ScreenIntro
import com.kmp.pyr.ui.components.SectionHeader
import com.kmp.pyr.ui.components.charts.BarChart
import com.kmp.pyr.ui.components.charts.BarEntry
import com.kmp.pyr.ui.components.charts.GanttChart
import com.kmp.pyr.ui.components.charts.GanttRow
import com.kmp.pyr.ui.motif.EgyptGlyph
import com.kmp.pyr.ui.motif.ProgressPyramid
import com.kmp.pyr.ui.theme.EgyptColors

private val categoryColor = mapOf(
    ProjectCategory.MONUMENT to EgyptColors.GoldBright,
    ProjectCategory.TEMPLE to EgyptColors.Turquoise,
    ProjectCategory.IRRIGATION to EgyptColors.Lapis,
    ProjectCategory.TRADE to EgyptColors.Sandstone,
)

@Composable
fun PlannerScreen(repo: KingdomRepository) {
    val snap = repo.snapshot
    var showAdd by remember { mutableStateOf(false) }
    var showHelp by remember { mutableStateOf(false) }
    val guide = SCREEN_GUIDES.getValue(Destination.PLANNER)

    LazyColumn(
        Modifier.fillMaxSize(),
        contentPadding = androidx.compose.foundation.layout.PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        item {
            ScreenIntro(
                eyebrow = "Pyramid Planner",
                title = "Works of the Pharaoh",
                subtitle = guide.subtitle,
                onHelp = { showHelp = true },
            )
        }

        // Overview stat cards
        item {
            val active = snap.projects.count { it.progress < 1f }
            val doneMilestones = snap.projects.sumOf { p -> p.milestones.count { it.done } }
            val totalMilestones = snap.projects.sumOf { it.milestones.size }
            val avg = if (snap.projects.isEmpty()) 0f else snap.projects.map { it.progress }.average().toFloat()
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                MiniStat("Active", active.toString(), Modifier.weight(1f))
                MiniStat("Milestones", "$doneMilestones/$totalMilestones", Modifier.weight(1f))
                MiniStat("Avg progress", Fmt.percent(avg), Modifier.weight(1f))
            }
        }

        // Gantt
        item {
            EgyptCard(Modifier.fillMaxWidth()) {
                SectionHeader("Timeline of Construction", eyebrow = "Gantt", glyph = EgyptGlyph.OBELISK)
                Spacer(Modifier.height(12.dp))
                GanttChart(
                    rows = snap.projects.map { p ->
                        GanttRow(
                            label = p.name,
                            startDay = p.startDay,
                            endDay = p.endDay,
                            progress = p.progress,
                            color = categoryColor[p.category]!!,
                            milestoneDays = p.milestones.map { it.dueDay },
                        )
                    },
                    today = snap.today,
                    modifier = Modifier.fillMaxWidth(),
                )
                Spacer(Modifier.height(6.dp))
                Text("◇ gold dashed line marks today", color = EgyptColors.TextMuted, fontSize = 10.sp)
            }
        }

        // Productivity by category bar chart
        item {
            EgyptCard(Modifier.fillMaxWidth()) {
                SectionHeader("Progress by Domain", eyebrow = "Productivity", glyph = EgyptGlyph.FEATHER)
                Spacer(Modifier.height(12.dp))
                BarChart(
                    entries = ProjectCategory.entries.map { cat ->
                        val list = snap.projects.filter { it.category == cat }
                        val avg = if (list.isEmpty()) 0.0 else list.map { it.progress.toDouble() }.average() * 100
                        BarEntry(cat.label.take(8), listOf(categoryColor[cat]!! to avg))
                    },
                    modifier = Modifier.fillMaxWidth().height(160.dp),
                )
            }
        }

        // Projects grouped by category
        item {
            SectionHeader("Projects", eyebrow = "By Category", glyph = EgyptGlyph.PYRAMID, caption = "Tap a milestone to mark it done — the pyramid fills as you progress")
        }
        ProjectCategory.entries.forEach { cat ->
            val list = snap.projects.filter { it.category == cat }
            if (list.isNotEmpty()) {
                item { SectionHeader(cat.label, eyebrow = "Project Category", glyph = EgyptGlyph.PYRAMID) }
                items(list, repo, snap.today)
            }
        }

        item {
            GoldButton("Charter New Project", onClick = { showAdd = true }, glyph = EgyptGlyph.PYRAMID, modifier = Modifier.fillMaxWidth())
        }
    }

    if (showHelp) HelpDialog(guide.title, guide.points) { showHelp = false }

    if (showAdd) {
        AddProjectDialog(
            onDismiss = { showAdd = false },
            onConfirm = { name, cat, dur, ms ->
                repo.addProject(name, cat, dur, ms)
                showAdd = false
            },
        )
    }
}

private fun androidx.compose.foundation.lazy.LazyListScope.items(
    list: List<Project>,
    repo: KingdomRepository,
    today: Int,
) {
    list.forEach { project ->
        item(key = project.id) { ProjectCard(project, repo, today) }
    }
}

@Composable
private fun ProjectCard(project: Project, repo: KingdomRepository, today: Int) {
    EgyptCard(Modifier.fillMaxWidth()) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            ProgressPyramid(
                progress = project.progress,
                modifier = Modifier.size(64.dp).padding(end = 12.dp),
            )
            Column(Modifier.weight(1f)) {
                Text(project.name, color = EgyptColors.TextOnNight, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                StageBadge(project)
                val forecast = Analytics.forecastFinishDay(project, today)
                Text(
                    if (project.progress >= 1f) "Completed" else "Forecast finish: ${Fmt.date(forecast)}",
                    color = EgyptColors.TextMuted, fontSize = 11.sp,
                )
            }
            Text(Fmt.percent(project.progress), color = EgyptColors.GoldBright, fontWeight = FontWeight.Bold, fontSize = 16.sp)
        }
        Spacer(Modifier.height(10.dp))
        project.milestones.forEach { m ->
            Row(
                Modifier.fillMaxWidth().clickable { repo.toggleMilestone(project.id, m.id) },
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Checkbox(
                    checked = m.done,
                    onCheckedChange = null, // row handles the toggle; avoids a double-fire
                    colors = CheckboxDefaults.colors(
                        checkedColor = EgyptColors.Gold,
                        uncheckedColor = EgyptColors.NightStroke,
                        checkmarkColor = EgyptColors.NightDeep,
                    ),
                )
                Text(
                    m.name,
                    color = if (m.done) EgyptColors.TextMuted else EgyptColors.TextOnNight,
                    fontSize = 13.sp,
                    modifier = Modifier.weight(1f),
                )
                Text(Fmt.date(m.dueDay), color = EgyptColors.TextMuted, fontSize = 10.sp)
            }
        }
    }
}

@Composable
private fun StageBadge(project: Project) {
    Box(
        Modifier.padding(vertical = 3.dp).clip(RoundedCornerShape(6.dp)).background(EgyptColors.NightRaised).padding(horizontal = 8.dp, vertical = 2.dp),
    ) {
        Text("◆ ${project.stage.label}", color = EgyptColors.GoldBright, fontSize = 10.sp, fontWeight = FontWeight.SemiBold)
    }
}

@Composable
private fun MiniStat(label: String, value: String, modifier: Modifier = Modifier) {
    EgyptCard(modifier, contentPadding = 12) {
        Text(value, color = EgyptColors.GoldBright, fontWeight = FontWeight.Bold, fontSize = 18.sp)
        Text(label, color = EgyptColors.TextMuted, fontSize = 11.sp)
    }
}
