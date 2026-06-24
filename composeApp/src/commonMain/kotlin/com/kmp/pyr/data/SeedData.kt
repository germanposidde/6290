package com.kmp.pyr.data

import kotlin.math.PI
import kotlin.math.sin

/** Builds a rich, deterministic starting kingdom so every screen looks alive on first launch. */
object SeedData {

    private const val TODAY = 360

    fun snapshot(): KingdomSnapshot {
        val rng = Lcg(987654321)
        val stats = ArrayList<DailyStat>(TODAY)
        // base levels grow gently over the year with seasonal waves + noise
        for (day in 0..TODAY) {
            val t = day / TODAY.toFloat()
            val season = sin(day / 360.0 * 2 * PI * 2).toFloat()
            val gold = 1800.0 + day * 9.0 + season * 420 + rng.nextUnit() * 260
            val grain = 3200.0 + day * 5.5 + sin(day / 30.0).toFloat() * 600 + rng.nextUnit() * 300
            val stone = 900.0 + day * 3.2 + rng.nextUnit() * 180
            val labor = 600.0 + sin(day / 45.0).toFloat() * 120 + rng.nextUnit() * 80
            val prod = (0.55f + t * 0.25f + season * 0.08f + rng.nextUnit() * 0.06f).coerceIn(0.2f, 0.99f)
            stats += DailyStat(day, gold, grain, stone, labor, prod)
        }
        val last = stats.last()

        val resources = mapOf(
            ResourceType.GOLD to last.gold,
            ResourceType.GRAIN to last.grain,
            ResourceType.STONE to last.stone,
            ResourceType.LABOR to last.labor,
        )
        val rates = mapOf(
            ResourceType.GOLD to 240.0,
            ResourceType.GRAIN to 180.0,
            ResourceType.STONE to 95.0,
            ResourceType.LABOR to 30.0,
        )

        val activities = listOf(
            ActivityEntry(1, "Tribute collected", "Gold from Nubian caravans", ActivityKind.RECORD, TODAY, 1240.0, ResourceType.GOLD),
            ActivityEntry(2, "Harvest tallied", "Granaries of Memphis", ActivityKind.RECORD, TODAY, 3100.0, ResourceType.GRAIN),
            ActivityEntry(3, "Quarry delivery", "Limestone from Tura", ActivityKind.RECORD, TODAY - 1, 620.0, ResourceType.STONE),
            ActivityEntry(4, "Workforce levied", "Seasonal corvée enrolled", ActivityKind.PROJECT, TODAY - 1, 140.0, ResourceType.LABOR),
            ActivityEntry(5, "Casing milestone", "Great Pyramid casing begun", ActivityKind.MILESTONE, TODAY - 2),
            ActivityEntry(6, "Projection saved", "120-day grain forecast", ActivityKind.CALCULATION, TODAY - 3, 0.0, ResourceType.GRAIN),
            ActivityEntry(7, "Trade route opened", "Punt expedition returns", ActivityKind.RECORD, TODAY - 4, 880.0, ResourceType.GOLD),
            ActivityEntry(8, "Archive backed up", "Royal scroll sealed", ActivityKind.BACKUP, TODAY - 6),
        )

        val calcRecords = listOf(
            CalcRecord(20, TODAY - 2, "Grain reserve plan",
                amounts = mapOf(ResourceType.GRAIN to 3200.0, ResourceType.LABOR to 600.0),
                rates = mapOf(ResourceType.GRAIN to 180.0, ResourceType.LABOR to 30.0),
                projectionDays = 120, revenue = 41600.0, cost = 22400.0),
            CalcRecord(21, TODAY - 9, "Quarry expansion",
                amounts = mapOf(ResourceType.STONE to 900.0, ResourceType.LABOR to 800.0),
                rates = mapOf(ResourceType.STONE to 95.0, ResourceType.LABOR to 30.0),
                projectionDays = 90, revenue = 18900.0, cost = 24600.0),
            CalcRecord(22, TODAY - 15, "Gold tribute outlook",
                amounts = mapOf(ResourceType.GOLD to 5200.0),
                rates = mapOf(ResourceType.GOLD to 240.0),
                projectionDays = 60, revenue = 31200.0, cost = 12800.0),
        )

        val projects = listOf(
            Project(40, "Great Pyramid of Giza", ProjectCategory.MONUMENT, TODAY - 200, TODAY + 160,
                milestones = listOf(
                    Milestone(401, "Survey & foundation", TODAY - 180, true),
                    Milestone(402, "Core blocks placed", TODAY - 60, true),
                    Milestone(403, "Casing stones fitted", TODAY + 40, false),
                    Milestone(404, "Capstone raised", TODAY + 120, false),
                    Milestone(405, "Causeway sealed", TODAY + 160, false),
                )),
            Project(41, "Karnak Hypostyle Hall", ProjectCategory.TEMPLE, TODAY - 120, TODAY + 60,
                milestones = listOf(
                    Milestone(411, "Columns quarried", TODAY - 110, true),
                    Milestone(412, "Columns erected", TODAY - 20, true),
                    Milestone(413, "Architraves set", TODAY + 30, false),
                    Milestone(414, "Reliefs carved", TODAY + 60, false),
                )),
            Project(42, "Faiyum Canal Network", ProjectCategory.IRRIGATION, TODAY - 80, TODAY + 40,
                milestones = listOf(
                    Milestone(421, "Channels surveyed", TODAY - 75, true),
                    Milestone(422, "Main dyke raised", TODAY - 10, true),
                    Milestone(423, "Sluice gates fitted", TODAY + 40, false),
                )),
            Project(43, "Red Sea Trade Route", ProjectCategory.TRADE, TODAY - 40, TODAY + 110,
                milestones = listOf(
                    Milestone(431, "Harbour cleared", TODAY - 30, true),
                    Milestone(432, "Fleet commissioned", TODAY + 50, false),
                    Milestone(433, "First voyage to Punt", TODAY + 110, false),
                )),
        )

        val achievements = listOf(
            Achievement(60, "Golden Hoard", "SUN_DISC", true, 1f),
            Achievement(61, "Master Builder", "PYRAMID", true, 1f),
            Achievement(62, "Eye of Horus", "EYE_OF_HORUS", true, 1f),
            Achievement(63, "Scarab Scribe", "SCARAB", false, 0.7f),
            Achievement(64, "Eternal Ankh", "ANKH", false, 0.45f),
            Achievement(65, "Lotus Bloom", "LOTUS", false, 0.25f),
        )

        return KingdomSnapshot(
            today = TODAY,
            nextId = 1000,
            resources = resources,
            rates = rates,
            dailyStats = stats,
            activities = activities,
            calcRecords = calcRecords,
            projects = projects,
            achievements = achievements,
            prefs = KingdomPrefs(),
        )
    }
}

/** Tiny deterministic linear-congruential generator for reproducible seed data. */
private class Lcg(seed: Int) {
    private var state = seed
    fun next(): Int { state = (state * 1103515245 + 12345) and 0x7fffffff; return state }
    /** value in [-1, 1] */
    fun nextUnit(): Float = (next() % 2000) / 1000f - 1f
}
