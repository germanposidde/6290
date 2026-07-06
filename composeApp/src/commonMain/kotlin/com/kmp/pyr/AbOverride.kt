package com.kmp.pyr

/**
 * Platform-provided switch that force-disables the loading A/B test.
 * On Android it is wired to the `FORCE_AB_DISABLED` build config flag; other
 * platforms default to `false`.
 */
expect object AbOverride {
    val forceDisabled: Boolean
}