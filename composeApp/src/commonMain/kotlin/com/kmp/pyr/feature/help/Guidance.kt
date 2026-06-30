package com.kmp.pyr.feature.help

import com.kmp.pyr.navigation.Destination

/** Plain-language guidance shown by each screen's "?" help button. */
data class ScreenGuide(val title: String, val subtitle: String, val points: List<String>)

val SCREEN_GUIDES: Map<Destination, ScreenGuide> = mapOf(
    Destination.DASHBOARD to ScreenGuide(
        title = "Dashboard",
        subtitle = "Your kingdom at a glance",
        points = listOf(
            "The four cards show your current treasury of Gold, Grain, Stone and Labor — your accumulated stores, with the day-over-day change and a mini trend.",
            "Where does the gold come from? Each day your kingdom produces resources at the rates shown in the “Daily Tribute” card.",
            "Tap “Collect Tribute · Advance a Day” to move one day forward: every store grows by its daily rate and a new point is added to the charts.",
            "The ▲/▼ on each card is exactly that day's production (today minus yesterday).",
            "“New Record” adds a one-off amount to a resource; “View Reports” jumps to analytics.",
            "Tap any point on the line chart to read exact values; tap a bar to highlight a resource.",
            "The scroll at the bottom is your recent activity history.",
        ),
    ),
    Destination.CALCULATOR to ScreenGuide(
        title = "Resource Calculator",
        subtitle = "Plan production and see profit or loss",
        points = listOf(
            "For each resource enter how much you produce per month and its value per unit.",
            "Labor is treated as a cost; Gold, Grain and Stone produce revenue.",
            "Drag the slider to choose how many days to project ahead.",
            "Revenue, cost and net surplus/deficit update instantly as you type.",
            "Tap “Save Projection” to store it — it appears in the history below and in the Archive.",
            "Use the filter and sort chips to browse saved projections.",
        ),
    ),
    Destination.PLANNER to ScreenGuide(
        title = "Pyramid Planner",
        subtitle = "Track projects like pyramids being built",
        points = listOf(
            "Each project is a pyramid that fills as you complete its milestones.",
            "Tap a milestone row to mark it done or undone — progress and stage update automatically.",
            "“Forecast finish” estimates the completion date from your current pace.",
            "The Gantt timeline shows every project across time; the gold dashed line is today.",
            "Tap “Charter New Project” to add one with its own milestones.",
        ),
    ),
    Destination.REPORTS to ScreenGuide(
        title = "Pharaoh Reports",
        subtitle = "Deep analytics across time",
        points = listOf(
            "Switch between Weekly, Monthly and Yearly with the chips at the top.",
            "Charts and totals recalculate for the chosen period.",
            "The comparison bars show this period (gold) versus the previous one (blue).",
            "“Oracle's Insights” summarises growth, strongest/weakest resources and productivity.",
            "The papyrus panel is an export-ready report preview.",
        ),
    ),
    Destination.SETTINGS to ScreenGuide(
        title = "Temple Settings & Archive",
        subtitle = "Preferences, backups and full history",
        points = listOf(
            "Set a Royal Seal portrait with the Camera or Gallery buttons; tap “Remove Seal” to clear it.",
            "Rename your kingdom and toggle ornamentation and textures.",
            "“Backup” shows your data as text you can copy and keep safe.",
            "“Restore” rebuilds your kingdom from a backup you paste in.",
            "“Reset to Seed Data” returns everything to the demo starting point.",
            "“Erase All Data” permanently wipes everything to an empty kingdom — back up first.",
            "Search and filter the Archive to find any past record.",
        ),
    ),
)
