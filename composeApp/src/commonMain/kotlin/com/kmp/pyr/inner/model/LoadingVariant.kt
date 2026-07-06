package com.kmp.pyr.inner.model

/**
 * The three loading-screen test variants. [id] is the stable integer used for persistence and in
 * the analytics query string.
 */
enum class LoadingVariant(val id: Int) {
    VARIANT_ONE(1),
    VARIANT_TWO(2),
    VARIANT_THREE(3);

    companion object {
        fun fromId(id: Int): LoadingVariant = entries.firstOrNull { it.id == id } ?: VARIANT_ONE
    }
}
