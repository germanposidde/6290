package com.kmp.pyr.feature.settings

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kmp.pyr.data.ActivityKind
import com.kmp.pyr.data.Fmt
import com.kmp.pyr.data.KingdomRepository
import com.kmp.pyr.data.ResourceType
import com.kmp.pyr.feature.help.SCREEN_GUIDES
import com.kmp.pyr.legal.Legal
import com.kmp.pyr.media.decodeSealBitmap
import com.kmp.pyr.media.rememberMediaController
import com.kmp.pyr.navigation.Destination
import com.kmp.pyr.ui.components.ChipRow
import com.kmp.pyr.ui.components.DangerButton
import com.kmp.pyr.ui.components.EgyptCard
import com.kmp.pyr.ui.components.GoldButton
import com.kmp.pyr.ui.components.GoldOutlinedButton
import com.kmp.pyr.ui.components.HelpDialog
import com.kmp.pyr.ui.components.ScreenIntro
import com.kmp.pyr.ui.components.SectionHeader
import com.kmp.pyr.ui.components.dismissKeyboardOnTap
import com.kmp.pyr.ui.motif.EgyptGlyph
import com.kmp.pyr.ui.motif.GlyphIcon
import com.kmp.pyr.ui.motif.OrnamentDivider
import com.kmp.pyr.ui.theme.EgyptColors

private enum class ArchiveFilter(val label: String, val kind: ActivityKind?) {
    ALL("All", null),
    RECORDS("Records", ActivityKind.RECORD),
    CALCS("Calculations", ActivityKind.CALCULATION),
    MILESTONES("Milestones", ActivityKind.MILESTONE),
    PROJECTS("Projects", ActivityKind.PROJECT),
    BACKUPS("Backups", ActivityKind.BACKUP),
}

@Composable
fun SettingsScreen(repo: KingdomRepository, onOpenLegal: (url: String, title: String) -> Unit) {
    val snap = repo.snapshot
    var showBackup by remember { mutableStateOf(false) }
    var showRestore by remember { mutableStateOf(false) }
    var showReset by remember { mutableStateOf(false) }
    var showErase by remember { mutableStateOf(false) }

    var query by remember { mutableStateOf("") }
    var filter by remember { mutableStateOf(ArchiveFilter.ALL) }
    var newestFirst by remember { mutableStateOf(true) }
    var showHelp by remember { mutableStateOf(false) }
    var showCamDenied by remember { mutableStateOf(false) }
    val guide = SCREEN_GUIDES.getValue(Destination.SETTINGS)
    val focus = LocalFocusManager.current
    val media = rememberMediaController(
        onImagePicked = { bytes -> repo.setSealImage(bytes) },
        onCameraDenied = { showCamDenied = true },
    )

    val archive = snap.activities
        .filter { filter.kind == null || it.kind == filter.kind }
        .filter { query.isBlank() || it.title.contains(query, true) || it.detail.contains(query, true) }
        .let { if (newestFirst) it.sortedByDescending { a -> a.day } else it.sortedBy { a -> a.day } }

    LazyColumn(
        Modifier.fillMaxSize().imePadding().dismissKeyboardOnTap(),
        contentPadding = androidx.compose.foundation.layout.PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        item {
            ScreenIntro(
                eyebrow = "Temple Settings & Archive",
                title = "Hall of Records",
                subtitle = guide.subtitle,
                onHelp = { showHelp = true },
            )
        }

        // Royal Seal — camera / gallery portrait
        item {
            EgyptCard(Modifier.fillMaxWidth()) {
                SectionHeader("Royal Seal", eyebrow = "Portrait", glyph = EgyptGlyph.ANKH, caption = "Add a portrait from your camera or photo library")
                Spacer(Modifier.height(14.dp))
                val seal = remember(snap.prefs.sealImageBase64) { decodeSealBitmap(snap.prefs.sealImageBase64) }
                Box(
                    Modifier.align(Alignment.CenterHorizontally).size(96.dp).clip(CircleShape)
                        .background(EgyptColors.NightRaised)
                        .border(2.dp, EgyptColors.Gold, CircleShape),
                    contentAlignment = Alignment.Center,
                ) {
                    if (seal != null) {
                        Image(seal, contentDescription = "Royal seal", modifier = Modifier.matchParentSize(), contentScale = ContentScale.Crop)
                    } else {
                        GlyphIcon(EgyptGlyph.SCARAB, size = 44.dp, tint = EgyptColors.GoldBright)
                    }
                }
                Spacer(Modifier.height(14.dp))
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    GoldButton("Camera", onClick = { media.captureFromCamera() }, glyph = EgyptGlyph.EYE_OF_HORUS, modifier = Modifier.weight(1f))
                    GoldOutlinedButton("Gallery", onClick = { media.pickFromGallery() }, modifier = Modifier.weight(1f))
                }
                if (seal != null) {
                    Spacer(Modifier.height(8.dp))
                    DangerButton("Remove Seal", onClick = { repo.setSealImage(null) }, modifier = Modifier.fillMaxWidth())
                }
            }
        }

        // Preferences
        item {
            EgyptCard(Modifier.fillMaxWidth()) {
                SectionHeader("Preferences", eyebrow = "Customization", glyph = EgyptGlyph.ANKH)
                Spacer(Modifier.height(12.dp))
                OutlinedTextField(
                    value = snap.prefs.kingdomName,
                    onValueChange = { repo.updatePrefs(snap.prefs.copy(kingdomName = it)) },
                    label = { Text("Name") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                    keyboardActions = KeyboardActions(onDone = { focus.clearFocus() }),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = EgyptColors.Gold,
                        unfocusedBorderColor = EgyptColors.NightStroke,
                        focusedLabelColor = EgyptColors.GoldBright,
                        cursorColor = EgyptColors.GoldBright,
                        focusedTextColor = EgyptColors.TextOnNight,
                        unfocusedTextColor = EgyptColors.TextOnNight,
                    ),
                    modifier = Modifier.fillMaxWidth(),
                )
                Spacer(Modifier.height(8.dp))
                ToggleRow("Golden ornamentation", snap.prefs.ornamentation) { repo.updatePrefs(snap.prefs.copy(ornamentation = it)) }
                ToggleRow("Rich sandstone textures", snap.prefs.richTextures) { repo.updatePrefs(snap.prefs.copy(richTextures = it)) }
                Spacer(Modifier.height(8.dp))
                Text("Currency glyph", color = EgyptColors.TextMuted, fontSize = 12.sp)
                Spacer(Modifier.height(6.dp))
                ChipRow(
                    options = listOf("𓋞", "☥", "✦", "𓂀"),
                    selected = snap.prefs.currencyGlyph,
                    onSelect = { repo.updatePrefs(snap.prefs.copy(currencyGlyph = it)) },
                    label = { it },
                )
            }
        }

        // Statistics summary
        item {
            EgyptCard(Modifier.fillMaxWidth()) {
                SectionHeader("Realm Statistics", eyebrow = "Lifetime Summary", glyph = EgyptGlyph.SUN_DISC)
                Spacer(Modifier.height(12.dp))
                val totalGold = snap.dailyStats.sumOf { it.gold }
                StatLine("Days chronicled", snap.dailyStats.size.toString())
                StatLine("Lifetime gold flow", Fmt.compact(totalGold))
                StatLine("Projects chartered", snap.projects.size.toString())
                StatLine("Milestones reached", snap.projects.sumOf { p -> p.milestones.count { it.done } }.toString())
                StatLine("Reckonings saved", snap.calcRecords.size.toString())
                StatLine("Activities logged", snap.activities.size.toString())
            }
        }

        // Backup / restore
        item {
            EgyptCard(Modifier.fillMaxWidth()) {
                SectionHeader("Data Vault", eyebrow = "Backup & Restore", glyph = EgyptGlyph.SCARAB, caption = "Back up to copyable text, or restore from a backup you paste in")
                Spacer(Modifier.height(12.dp))
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    GoldButton("Backup", onClick = { repo.recordBackup(); showBackup = true }, modifier = Modifier.weight(1f))
                    GoldOutlinedButton("Restore", onClick = { showRestore = true }, modifier = Modifier.weight(1f))
                }
                Spacer(Modifier.height(8.dp))
                GoldOutlinedButton("Reset to Seed Data", onClick = { showReset = true }, modifier = Modifier.fillMaxWidth())
                Spacer(Modifier.height(8.dp))
                DangerButton("Erase All Data", onClick = { showErase = true }, modifier = Modifier.fillMaxWidth())
            }
        }

        // Legal
        item {
            EgyptCard(Modifier.fillMaxWidth()) {
                SectionHeader("Legal", eyebrow = "Policies", glyph = EgyptGlyph.FEATHER)
                Spacer(Modifier.height(8.dp))
                LegalRow("Privacy Policy") { onOpenLegal(Legal.privacyPolicyUrl, "Privacy Policy") }
                val terms = Legal.termsOfUseUrl
                if (Legal.isIos && terms != null) {
                    LegalRow("Terms of Use") { onOpenLegal(terms, "Terms of Use") }
                }
            }
        }

        // Archive
        item {
            SectionHeader("Historical Archive", eyebrow = "Scroll Repository", glyph = EgyptGlyph.OBELISK, caption = "Every record ever logged — search, filter and sort below")
            OrnamentDivider()
        }
        item {
            OutlinedTextField(
                value = query,
                onValueChange = { query = it },
                label = { Text("Search the archive") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                keyboardActions = KeyboardActions(onSearch = { focus.clearFocus() }),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = EgyptColors.Gold,
                    unfocusedBorderColor = EgyptColors.NightStroke,
                    focusedLabelColor = EgyptColors.GoldBright,
                    cursorColor = EgyptColors.GoldBright,
                    focusedTextColor = EgyptColors.TextOnNight,
                    unfocusedTextColor = EgyptColors.TextOnNight,
                ),
                modifier = Modifier.fillMaxWidth(),
            )
        }
        item {
            ChipRow(options = ArchiveFilter.entries, selected = filter, onSelect = { filter = it }, label = { it.label })
        }
        item {
            GoldOutlinedButton(
                if (newestFirst) "Sort: Newest first" else "Sort: Oldest first",
                onClick = { newestFirst = !newestFirst },
            )
        }

        if (archive.isEmpty()) {
            item { Text("No scrolls match your search.", color = EgyptColors.TextMuted, fontSize = 13.sp) }
        } else {
            archive.forEach { entry ->
                item(key = entry.id) {
                    EgyptCard(Modifier.fillMaxWidth(), contentPadding = 12) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            GlyphIcon(glyphForKind(entry.kind), size = 20.dp, tint = EgyptColors.GoldBright)
                            Spacer(Modifier.height(0.dp))
                            Column(Modifier.weight(1f).padding(start = 10.dp)) {
                                Text(entry.title, color = EgyptColors.TextOnNight, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                                Text(entry.detail, color = EgyptColors.TextMuted, fontSize = 11.sp)
                            }
                            Column(horizontalAlignment = Alignment.End) {
                                if (entry.delta != 0.0) {
                                    Text(Fmt.signed(entry.delta), color = if (entry.delta >= 0) EgyptColors.Profit else EgyptColors.Loss, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                                }
                                Text(Fmt.relativeDay(entry.day, snap.today), color = EgyptColors.TextMuted, fontSize = 10.sp)
                            }
                        }
                    }
                }
            }
        }
    }

    if (showBackup) {
        // Serialize the snapshot off the main thread; the dialog shows "Preparing…" until ready.
        var backupJson by remember { mutableStateOf<String?>(null) }
        LaunchedEffect(Unit) {
            backupJson = withContext(Dispatchers.Default) { repo.exportJson() }
        }
        BackupDialog(json = backupJson, onDismiss = { showBackup = false })
    }
    if (showRestore) RestoreDialog(
        onDismiss = { showRestore = false },
        onRestore = { json -> repo.restoreFrom(json) },
    )
    if (showReset) ConfirmDialog(
        title = "Reset to Seed Data?",
        message = "This replaces all current records with the default kingdom. This cannot be undone.",
        confirmText = "Reset",
        onConfirm = { repo.resetToSeed(); showReset = false },
        onDismiss = { showReset = false },
    )
    if (showErase) ConfirmDialog(
        title = "Erase All Data?",
        message = "This permanently deletes every record, projection, project and setting, leaving an empty kingdom. This cannot be undone — back up first if unsure.",
        confirmText = "Erase Everything",
        onConfirm = { repo.eraseAll(); showErase = false },
        onDismiss = { showErase = false },
    )
    if (showHelp) HelpDialog(guide.title, guide.points) { showHelp = false }
    if (showCamDenied) HelpDialog(
        "Camera Unavailable",
        listOf("The camera permission is needed to take a portrait. You can still choose an image from your gallery."),
    ) { showCamDenied = false }
}

@Composable
private fun ToggleRow(label: String, checked: Boolean, onChange: (Boolean) -> Unit) {
    Row(Modifier.fillMaxWidth().padding(vertical = 4.dp), verticalAlignment = Alignment.CenterVertically) {
        Text(label, color = EgyptColors.TextOnNight, fontSize = 14.sp, modifier = Modifier.weight(1f))
        Switch(
            checked = checked,
            onCheckedChange = onChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = EgyptColors.NightDeep,
                checkedTrackColor = EgyptColors.Gold,
                uncheckedThumbColor = EgyptColors.TextMuted,
                uncheckedTrackColor = EgyptColors.NightRaised,
            ),
        )
    }
}

@Composable
private fun StatLine(label: String, value: String) {
    Row(Modifier.fillMaxWidth().padding(vertical = 3.dp), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(label, color = EgyptColors.TextMuted, fontSize = 13.sp)
        Text(value, color = EgyptColors.GoldBright, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
    }
}

@Composable
private fun LegalRow(label: String, onClick: () -> Unit) {
    Row(
        Modifier.fillMaxWidth().clickable(onClick = onClick).padding(vertical = 14.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(label, color = EgyptColors.TextOnNight, fontSize = 15.sp)
        Text("→", color = EgyptColors.GoldBright, fontSize = 16.sp)
    }
}

private fun glyphForKind(kind: ActivityKind): EgyptGlyph = when (kind) {
    ActivityKind.RECORD -> EgyptGlyph.SUN_DISC
    ActivityKind.CALCULATION -> EgyptGlyph.EYE_OF_HORUS
    ActivityKind.MILESTONE -> EgyptGlyph.PYRAMID
    ActivityKind.PROJECT -> EgyptGlyph.OBELISK
    ActivityKind.BACKUP -> EgyptGlyph.SCARAB
}
