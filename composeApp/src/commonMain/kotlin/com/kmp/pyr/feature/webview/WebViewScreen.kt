package com.kmp.pyr.feature.webview

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kmp.pyr.PlatformWebView
import com.kmp.pyr.ui.theme.EgyptColors

/** Full-screen in-app browser for the legal pages, themed to match the app. */
@Composable
fun WebViewScreen(url: String, title: String, onNavigateBack: () -> Unit) {
    Column(Modifier.fillMaxSize().background(EgyptColors.Night)) {
        Box(
            Modifier.fillMaxWidth()
                .background(EgyptColors.NightDeep)
                .statusBarsPadding()
                .padding(horizontal = 8.dp, vertical = 4.dp),
        ) {
            TextButton(onClick = onNavigateBack, modifier = Modifier.align(Alignment.CenterStart)) {
                Text("← Back", color = EgyptColors.GoldBright, fontSize = 14.sp)
            }
            Text(
                text = title,
                color = EgyptColors.TextOnNight,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.align(Alignment.Center),
            )
        }
        Box(Modifier.fillMaxWidth().height(1.dp).background(EgyptColors.GoldSoft))
        PlatformWebView(url = url, modifier = Modifier.fillMaxSize())
    }
}
