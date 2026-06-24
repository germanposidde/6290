package com.kmp.pyr

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import com.kmp.pyr.data.KingdomRepository
import com.kmp.pyr.navigation.RootScaffold
import com.kmp.pyr.ui.theme.EgyptTheme

@Composable
fun AppNavGraph() {
    val repo = remember { KingdomRepository() }
    EgyptTheme {
        RootScaffold(repo)
    }
}
