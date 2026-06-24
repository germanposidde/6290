package com.kmp.pyr.navigation

import com.kmp.pyr.ui.motif.EgyptGlyph

enum class Destination(val label: String, val glyph: EgyptGlyph) {
    DASHBOARD("Kingdom", EgyptGlyph.SUN_DISC),
    CALCULATOR("Calculator", EgyptGlyph.EYE_OF_HORUS),
    PLANNER("Planner", EgyptGlyph.PYRAMID),
    REPORTS("Reports", EgyptGlyph.FEATHER),
    SETTINGS("Temple", EgyptGlyph.ANKH),
}
