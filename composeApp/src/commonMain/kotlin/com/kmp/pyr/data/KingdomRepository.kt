package com.kmp.pyr.data

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi
import kotlin.math.sin

/**
 * Single source of truth for the whole app. Holds the [KingdomSnapshot] as Compose
 * state (so reads recompose) and persists after every mutation.
 */
class KingdomRepository(private val persistence: Persistence = Persistence()) {

    var snapshot: KingdomSnapshot by mutableStateOf(persistence.load() ?: SeedData.snapshot())
        private set

    init {
        // Ensure first-run seed is persisted.
        if (persistence.load() == null) persistence.save(snapshot)
    }

    private fun update(transform: (KingdomSnapshot) -> KingdomSnapshot) {
        val next = transform(snapshot)
        snapshot = next
        persistence.save(next)
    }

    private fun nextId(): Long = snapshot.nextId

    // ---- Mutations ---------------------------------------------------------

    fun addCalcRecord(
        title: String,
        amounts: Map<ResourceType, Double>,
        rates: Map<ResourceType, Double>,
        projectionDays: Int,
        revenue: Double,
        cost: Double,
    ) = update { s ->
        val id = s.nextId
        val record = CalcRecord(id, s.today, title, amounts, rates, projectionDays, revenue, cost)
        val activity = ActivityEntry(
            id + 1, "Projection saved", title, ActivityKind.CALCULATION, s.today, revenue - cost,
        )
        s.copy(
            nextId = id + 2,
            calcRecords = listOf(record) + s.calcRecords,
            activities = listOf(activity) + s.activities,
        )
    }

    fun addQuickRecord(resource: ResourceType, amount: Double) = update { s ->
        val id = s.nextId
        val activity = ActivityEntry(
            id, "${resource.label} recorded", "Manual ledger entry",
            ActivityKind.RECORD, s.today, amount, resource,
        )
        val newResources = s.resources.toMutableMap().apply {
            this[resource] = (this[resource] ?: 0.0) + amount
        }
        s.copy(nextId = id + 1, activities = listOf(activity) + s.activities, resources = newResources)
    }

    fun toggleMilestone(projectId: Long, milestoneId: Long) = update { s ->
        var changedName = ""
        val projects = s.projects.map { p ->
            if (p.id != projectId) p else p.copy(
                milestones = p.milestones.map { m ->
                    if (m.id != milestoneId) m else {
                        changedName = m.name
                        m.copy(done = !m.done)
                    }
                }
            )
        }
        val id = s.nextId
        val activity = ActivityEntry(
            id, "Milestone updated", changedName, ActivityKind.MILESTONE, s.today,
        )
        s.copy(nextId = id + 1, projects = projects, activities = listOf(activity) + s.activities)
    }

    fun addProject(name: String, category: ProjectCategory, durationDays: Int, milestoneCount: Int) = update { s ->
        val id = s.nextId
        val milestones = (0 until milestoneCount).map { i ->
            Milestone(
                id = id + 1 + i,
                name = "Phase ${i + 1}",
                dueDay = s.today + (durationDays * (i + 1) / milestoneCount),
            )
        }
        val project = Project(id, name, category, s.today, s.today + durationDays, milestones)
        val activity = ActivityEntry(
            id + milestoneCount + 1, "Project chartered", name, ActivityKind.PROJECT, s.today,
        )
        s.copy(
            nextId = id + milestoneCount + 2,
            projects = s.projects + project,
            activities = listOf(activity) + s.activities,
        )
    }

    fun updatePrefs(prefs: KingdomPrefs) = update { it.copy(prefs = prefs) }

    fun markOnboarded() = update { it.copy(prefs = it.prefs.copy(onboarded = true)) }

    /** Store (or clear, with null) the kingdom's seal image as Base64. */
    @OptIn(ExperimentalEncodingApi::class)
    fun setSealImage(bytes: ByteArray?) = update {
        it.copy(prefs = it.prefs.copy(sealImageBase64 = bytes?.let { b -> Base64.encode(b) }))
    }

    /**
     * Advance the kingdom one day: each resource gains its daily production rate,
     * the treasury and a new [DailyStat] are recorded, and the gain is logged.
     * This is where resource numbers actually come from over time.
     */
    fun advanceDay() = update { s ->
        val newDay = s.today + 1
        val newResources = ResourceType.entries.associateWith { t ->
            (s.resources[t] ?: 0.0) + (s.rates[t] ?: 0.0)
        }
        val productivity = (0.62f + 0.30f * sin(newDay / 30f)).coerceIn(0.25f, 0.99f)
        val stat = DailyStat(
            day = newDay,
            gold = newResources.getValue(ResourceType.GOLD),
            grain = newResources.getValue(ResourceType.GRAIN),
            stone = newResources.getValue(ResourceType.STONE),
            labor = newResources.getValue(ResourceType.LABOR),
            productivity = productivity,
        )
        val id = s.nextId
        val activity = ActivityEntry(
            id, "Tribute collected", "Daily production for ${Fmt.date(newDay)}",
            ActivityKind.RECORD, newDay, s.rates[ResourceType.GOLD] ?: 0.0, ResourceType.GOLD,
        )
        s.copy(
            today = newDay,
            nextId = id + 1,
            resources = newResources,
            dailyStats = s.dailyStats + stat,
            activities = listOf(activity) + s.activities,
        )
    }

    fun recordBackup() = update { s ->
        val id = s.nextId
        s.copy(
            nextId = id + 1,
            activities = listOf(
                ActivityEntry(id, "Archive backed up", "Royal scroll sealed", ActivityKind.BACKUP, s.today)
            ) + s.activities,
        )
    }

    // ---- Backup / restore --------------------------------------------------

    fun exportJson(): String = persistence.export(snapshot)

    fun restoreFrom(json: String): Boolean {
        val parsed = persistence.import(json) ?: return false
        update { parsed }
        return true
    }

    fun resetToSeed() = update { SeedData.snapshot() }

    /** Wipe everything to an empty kingdom (keeps the user past onboarding). */
    fun eraseAll() = update {
        KingdomSnapshot(
            today = 0,
            nextId = 1000,
            prefs = KingdomPrefs(onboarded = true),
        )
    }
}
