package com.kmp.pyr.data

import kotlinx.serialization.Serializable

/**
 * Time is modelled as a synthetic "kingdom day" index (Int) over a 360-day,
 * 12-month calendar — fully deterministic and offline, no clock dependency.
 */

enum class ResourceType(val label: String, val glyphChar: String) {
    GOLD("Gold", "𓋞"),
    GRAIN("Grain", "𓂵"),
    STONE("Stone", "𓊃"),
    LABOR("Labor", "𓀀"),
}

enum class ActivityKind { RECORD, CALCULATION, MILESTONE, PROJECT, BACKUP }

@Serializable
data class ActivityEntry(
    val id: Long,
    val title: String,
    val detail: String,
    val kind: ActivityKind,
    val day: Int,
    val delta: Double = 0.0,
    val resource: ResourceType? = null,
)

@Serializable
data class CalcRecord(
    val id: Long,
    val day: Int,
    val title: String,
    val amounts: Map<ResourceType, Double>,
    val rates: Map<ResourceType, Double>,
    val projectionDays: Int,
    val revenue: Double,
    val cost: Double,
) {
    val net: Double get() = revenue - cost
    val isProfit: Boolean get() = net >= 0.0
}

enum class PyramidStage(val label: String, val order: Int) {
    FOUNDATION("Foundation", 0),
    CORE("Core Blocks", 1),
    CASING("Casing", 2),
    CAPSTONE("Capstone", 3),
    COMPLETE("Sealed", 4);

    companion object {
        fun forProgress(p: Float): PyramidStage = when {
            p >= 1f -> COMPLETE
            p >= 0.8f -> CAPSTONE
            p >= 0.5f -> CASING
            p >= 0.2f -> CORE
            else -> FOUNDATION
        }
    }
}

@Serializable
data class Milestone(
    val id: Long,
    val name: String,
    val dueDay: Int,
    val done: Boolean = false,
)

enum class ProjectCategory(val label: String) {
    MONUMENT("Monuments"),
    IRRIGATION("Irrigation"),
    TRADE("Trade Routes"),
    TEMPLE("Temples"),
}

@Serializable
data class Project(
    val id: Long,
    val name: String,
    val category: ProjectCategory,
    val startDay: Int,
    val endDay: Int,
    val milestones: List<Milestone> = emptyList(),
) {
    val progress: Float
        get() = if (milestones.isEmpty()) 0f
        else milestones.count { it.done } / milestones.size.toFloat()
    val stage: PyramidStage get() = PyramidStage.forProgress(progress)
}

@Serializable
data class Achievement(
    val id: Long,
    val name: String,
    val glyph: String, // EgyptGlyph name
    val unlocked: Boolean,
    val progress: Float,
)

@Serializable
data class DailyStat(
    val day: Int,
    val gold: Double,
    val grain: Double,
    val stone: Double,
    val labor: Double,
    val productivity: Float,
) {
    fun amount(type: ResourceType): Double = when (type) {
        ResourceType.GOLD -> gold
        ResourceType.GRAIN -> grain
        ResourceType.STONE -> stone
        ResourceType.LABOR -> labor
    }
}

@Serializable
data class KingdomPrefs(
    val ornamentation: Boolean = true,
    val richTextures: Boolean = true,
    val currencyGlyph: String = "𓋞",
    val kingdomName: String = "Kingdom of the Two Lands",
    val onboarded: Boolean = false,
    val sealImageBase64: String? = null,
)

/** Root aggregate persisted as a single JSON document. */
@Serializable
data class KingdomSnapshot(
    val today: Int = 360,
    val nextId: Long = 1000,
    val resources: Map<ResourceType, Double> = emptyMap(),
    val rates: Map<ResourceType, Double> = emptyMap(),
    val dailyStats: List<DailyStat> = emptyList(),
    val activities: List<ActivityEntry> = emptyList(),
    val calcRecords: List<CalcRecord> = emptyList(),
    val projects: List<Project> = emptyList(),
    val achievements: List<Achievement> = emptyList(),
    val prefs: KingdomPrefs = KingdomPrefs(),
)
