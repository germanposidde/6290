package com.kmp.pyr.navigation

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kmp.pyr.data.KingdomRepository
import com.kmp.pyr.feature.calculator.CalculatorScreen
import com.kmp.pyr.feature.dashboard.DashboardScreen
import com.kmp.pyr.feature.planner.PlannerScreen
import com.kmp.pyr.feature.reports.ReportsScreen
import com.kmp.pyr.feature.help.WelcomeDialog
import com.kmp.pyr.feature.settings.SettingsScreen
import com.kmp.pyr.feature.webview.WebViewScreen
import com.kmp.pyr.ui.motif.GlyphIcon
import com.kmp.pyr.ui.motif.NightBackground
import com.kmp.pyr.ui.theme.EgyptColors

@Composable
fun RootScaffold(repo: KingdomRepository) {
    var current by remember { mutableStateOf(Destination.DASHBOARD) }
    var webPage by remember { mutableStateOf<WebPage?>(null) }
    val navigate: (Destination) -> Unit = { current = it }

    // Hide the bottom navigation bar while the soft keyboard is open, otherwise
    // adjustResize pushes the dark-blue bar up so it floats on top of the keyboard.
    val imeVisible = WindowInsets.ime.getBottom(LocalDensity.current) > 0

    Box(Modifier.fillMaxSize()) {
    Scaffold(
        containerColor = Color.Transparent,
        bottomBar = {
            if (!imeVisible) NavigationBar(containerColor = EgyptColors.NightDeep, tonalElevation = 0.dp) {
                Destination.entries.forEach { dest ->
                    val selected = dest == current
                    NavigationBarItem(
                        selected = selected,
                        onClick = { current = dest },
                        icon = {
                            GlyphIcon(
                                dest.glyph,
                                size = 24.dp,
                                tint = if (selected) EgyptColors.NightDeep else EgyptColors.TextMuted,
                                strokeWidth = 2.2f,
                            )
                        },
                        label = { Text(dest.label, fontSize = 10.sp) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = EgyptColors.NightDeep,
                            selectedTextColor = EgyptColors.GoldBright,
                            indicatorColor = EgyptColors.Gold,
                            unselectedIconColor = EgyptColors.TextMuted,
                            unselectedTextColor = EgyptColors.TextMuted,
                        ),
                    )
                }
            }
        },
    ) { padding ->
        NightBackground {
            AnimatedContent(
                targetState = current,
                transitionSpec = {
                    (fadeIn(tween(300)) togetherWith fadeOut(tween(200)))
                },
                modifier = Modifier.fillMaxSize().padding(padding),
                label = "screen",
            ) { dest ->
                Box(Modifier.fillMaxSize()) {
                    when (dest) {
                        Destination.DASHBOARD -> DashboardScreen(repo, navigate)
                        Destination.CALCULATOR -> CalculatorScreen(repo)
                        Destination.PLANNER -> PlannerScreen(repo)
                        Destination.REPORTS -> ReportsScreen(repo)
                        Destination.SETTINGS -> SettingsScreen(
                            repo,
                            onOpenLegal = { url, title -> webPage = WebPage(url, title) },
                        )
                    }
                }
            }
        }
    }

    webPage?.let { page ->
        WebViewScreen(url = page.url, title = page.title, onNavigateBack = { webPage = null })
    }
    }

    if (!repo.snapshot.prefs.onboarded) {
        WelcomeDialog(
            kingdomName = repo.snapshot.prefs.kingdomName,
            onEnter = { repo.markOnboarded() },
        )
    }
}

private data class WebPage(val url: String, val title: String)
