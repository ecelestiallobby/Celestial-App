package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.model.GuestPage
import com.example.model.OwnerOverlayPanel
import com.example.model.PipelineStage
import com.example.model.PlatformState
import com.example.model.PortalRoute
import com.example.ui.components.CelestialGlassCard
import com.example.ui.components.CelestialTextField
import com.example.ui.components.GoldActionButton
import com.example.ui.components.NoticeBanner
import com.example.ui.components.PortalTopline
import com.example.ui.components.SecondaryGlassButton
import com.example.ui.theme.CelestialColors
import com.example.viewmodel.CelestialUiState
import com.example.viewmodel.CelestialViewModel

@Composable
fun OwnerDashboardScreen(
    state: CelestialUiState,
    viewModel: CelestialViewModel
) {
    BackHandler { viewModel.lockSwitchAccess() }
    val clipboard = LocalClipboardManager.current

    var expandCommand by remember { mutableStateOf(true) }
    var expandOperations by remember { mutableStateOf(true) }
    var expandBeta by remember { mutableStateOf(false) }
    var expandContent by remember { mutableStateOf(false) }
    var expandVault by remember { mutableStateOf(false) }
    var expandAccounts by remember { mutableStateOf(false) }
    var expandPreview by remember { mutableStateOf(false) }
    var expandReference by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Column(modifier = Modifier.widthIn(max = 640.dp)) {
            PortalTopline(
                backLabel = "Lock / Switch Access",
                onBack = viewModel::lockSwitchAccess,
                sectionBadge = "OWNER COMMAND · PRIVATE WORKSPACE"
            )
            Text("AUTHORIZED SESSION", style = MaterialTheme.typography.labelSmall, color = CelestialColors.GoldDeep)
            Text("Welcome, Commander.", style = MaterialTheme.typography.headlineMedium, color = CelestialColors.TextPrimary)
            Text(
                "Owner Preview is active on this device. This workspace is reserved for previewing and managing the portal experience.",
                style = MaterialTheme.typography.bodyMedium,
                color = CelestialColors.TextSecondary
            )
            Spacer(Modifier.height(10.dp))
            CelestialGlassCard(borderColor = CelestialColors.BorderGold) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("✓ OWNER SESSION: ACTIVE", fontWeight = FontWeight.Bold, color = CelestialColors.Emerald, fontSize = 12.sp)
                    Text("LOCAL DEMO SESSION", fontSize = 10.sp, color = CelestialColors.VioletSoft)
                }
            }

            Spacer(Modifier.height(12.dp))
            // Owner Destination Tiles
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OwnerDestTile("✦ CELESTIALS", "Explore realms", Modifier.weight(1f)) {
                    viewModel.navigate(PortalRoute.GUEST, GuestPage.CELESTIAL_OVERVIEW)
                }
                OwnerDestTile("♛ FOUNDERS", "Founding circle", Modifier.weight(1f)) {
                    viewModel.navigate(PortalRoute.GUEST, GuestPage.FOUNDERS)
                }
            }
            Spacer(Modifier.height(8.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OwnerDestTile("◈ DRACONIANS", "Content Studio", Modifier.weight(1f)) { expandContent = true }
                OwnerDestTile("◈ STORE", "Open Store", Modifier.weight(1f)) { viewModel.enterStore() }
            }

            Spacer(Modifier.height(10.dp))
            SecondaryGlassButton("ONBOARDING SESSION →", onClick = viewModel::startOnboarding, testTag = "owner_start_onboarding")

            Spacer(Modifier.height(16.dp))
            Text("MASTER CONTROL PLANE", style = MaterialTheme.typography.labelSmall, color = CelestialColors.GoldLight)
            Spacer(Modifier.height(8.dp))

            // 01 Global Command Strip
            CollapsibleModule("01 · GLOBAL COMMAND STRIP", state.platformState.label.uppercase(), expandCommand, { expandCommand = !expandCommand }) {
                Text("LIVE VIEW SIMULATOR", style = MaterialTheme.typography.labelSmall, color = CelestialColors.VioletSoft)
                Spacer(Modifier.height(6.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    listOf("Owner", "Guest", "Member", "Army").forEach { tier ->
                        ChipButton(tier, state.previewTier == tier, Modifier.weight(1f)) { viewModel.setPreviewTier(tier) }
                    }
                }
                Spacer(Modifier.height(10.dp))
                Text("PLATFORM STATE", style = MaterialTheme.typography.labelSmall, color = CelestialColors.VioletSoft)
                Spacer(Modifier.height(6.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    PlatformState.entries.forEach { ps ->
                        ChipButton(ps.label, state.platformState == ps, Modifier.weight(1f)) { viewModel.setPlatformState(ps) }
                    }
                }
                if (state.platformState != PlatformState.LIVE) {
                    NoticeBanner("${state.platformState.label} preview is active on this device only.")
                }
                Spacer(Modifier.height(10.dp))
                SecondaryGlassButton(
                    text = if (state.editUiMode) "EDIT UI / VISIBILITY: ON" else "EDIT UI / VISIBILITY: OFF",
                    onClick = viewModel::toggleEditUiMode
                )
                if (state.editUiMode) {
                    listOf("header" to "Header & navigation", "footer" to "Footer", "links" to "Navigation links", "sections" to "Content sections").forEach { (id, lbl) ->
                        val hidden = state.visibilityMap["${state.previewTier}:$id"] == true
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth().clickable { viewModel.toggleVisibilityTarget(id) }
                        ) {
                            Checkbox(checked = hidden, onCheckedChange = { viewModel.toggleVisibilityTarget(id) })
                            Text("$lbl (${if (hidden) "Hidden" else "Visible"})", fontSize = 12.sp, color = CelestialColors.TextSecondary)
                        }
                    }
                }
                Spacer(Modifier.height(10.dp))
                CelestialTextField(
                    value = state.bannerText,
                    onValueChange = { viewModel.updateBanner(text = it) },
                    label = "TIER ANNOUNCEMENT (${state.bannerTier.uppercase()})",
                    placeholder = "Announcement text"
                )
                if (state.bannerText.isNotBlank()) {
                    NoticeBanner("LOCAL BANNER PREVIEW · ${state.bannerTier.uppercase()}: ${state.bannerText}")
                }
            }

            // 02 Live Operations & Order Pipeline
            CollapsibleModule(
                "02 · LIVE OPERATIONS & PIPELINE",
                if (state.storeState.activeOrder.orderId.isNotBlank()) "1 ORDER" else "NO ORDER",
                expandOperations,
                { expandOperations = !expandOperations }
            ) {
                val order = state.storeState.activeOrder
                if (order.orderId.isNotBlank()) {
                    Text("${order.orderId} · ${order.status.label.uppercase()}", fontWeight = FontWeight.Bold, color = CelestialColors.GoldLight, fontSize = 13.sp)
                    Spacer(Modifier.height(8.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        PipelineStage.entries.forEach { st ->
                            ChipButton(st.label, state.pipelineStage == st, Modifier.weight(1f)) { viewModel.updatePipelineStage(st) }
                        }
                    }
                    Spacer(Modifier.height(10.dp))
                    Text("PACKAGING SOP CHECKLIST", style = MaterialTheme.typography.labelSmall, color = CelestialColors.VioletSoft)
                    listOf("8×9 bag", "Inner box", "Wrapping", "Decoy item", "Digital scale weight check").forEach { item ->
                        val checked = state.sopChecklist[item] == true
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth().clickable { viewModel.toggleSopChecklist(item) }
                        ) {
                            Checkbox(
                                checked = checked,
                                onCheckedChange = { viewModel.toggleSopChecklist(item) },
                                colors = CheckboxDefaults.colors(checkedColor = CelestialColors.GoldLight)
                            )
                            Text(item, fontSize = 12.sp, color = CelestialColors.TextPrimary)
                        }
                    }
                } else {
                    Text("No order is currently present in the shared demo record.", fontSize = 12.sp, color = CelestialColors.TextSecondary)
                }
            }

            // 03 Beta Key Command
            CollapsibleModule("03 · BETA KEY COMMAND", "${state.betaEntries.size} KEYS", expandBeta, { expandBeta = !expandBeta }) {
                SecondaryGlassButton("PREPARE BETA WELCOME MESSAGE", onClick = {
                    viewModel.updateQuickCopy("Welcome to the CELESTIAL LOBBY beta. Your invitation key is separate from your individual Access ID and Access Code.")
                    viewModel.setOwnerMasterNotice("Welcome message loaded into Session Vault Quick-Copy.")
                })
                Spacer(Modifier.height(8.dp))
                GoldActionButton("GENERATE 20 INVITATION KEYS", onClick = viewModel::generateBetaBatch, testTag = "generate_beta_keys_button")
                Spacer(Modifier.height(8.dp))
                CelestialTextField(
                    value = state.betaSearch,
                    onValueChange = viewModel::updateBetaSearch,
                    label = "SEARCH KEYS",
                    placeholder = "Search code or Access ID"
                )
                Spacer(Modifier.height(8.dp))
                val filtered = state.betaEntries.filter {
                    state.betaSearch.isBlank() ||
                        it.code.contains(state.betaSearch, ignoreCase = true) ||
                        it.accessId.contains(state.betaSearch, ignoreCase = true)
                }.take(8)
                filtered.forEach { entry ->
                    Surface(
                        modifier = Modifier.fillMaxWidth().padding(bottom = 6.dp),
                        shape = RoundedCornerShape(8.dp),
                        color = CelestialColors.DeepSpace,
                        border = BorderStroke(1.dp, CelestialColors.BorderGlass)
                    ) {
                        Row(
                            modifier = Modifier.padding(10.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text("BETA ${entry.slot.toString().padStart(2, '0')} · ${entry.code}", fontFamily = FontFamily.Monospace, fontSize = 12.sp, color = CelestialColors.GoldBright)
                                Text(entry.status, fontSize = 10.sp, color = CelestialColors.VioletSoft)
                            }
                            TextButton(onClick = {
                                viewModel.updateBetaEntry(entry.slot, status = if (entry.status == "Revoked") "Active" else "Revoked")
                            }) {
                                Text(if (entry.status == "Revoked") "RESTORE" else "KILL", fontSize = 11.sp, color = CelestialColors.Rose)
                            }
                        }
                    }
                }
            }

            // 04 Dweller CMS
            CollapsibleModule("04 · DYNAMIC CONTENT & LORE CMS", "${state.dwellers.size} DWELLERS", expandContent, { expandContent = !expandContent }) {
                state.dwellers.forEachIndexed { idx, dweller ->
                    Column(modifier = Modifier.padding(bottom = 10.dp)) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                            Text(dweller.name, fontWeight = FontWeight.Bold, color = CelestialColors.GoldBright, fontSize = 13.sp)
                            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                listOf("Available", "Dormant", "Hidden").forEach { st ->
                                    ChipButton(st, dweller.status == st, Modifier) { viewModel.updateDweller(idx, status = st) }
                                }
                            }
                        }
                        Spacer(Modifier.height(4.dp))
                        CelestialTextField(
                            value = dweller.lore,
                            onValueChange = { viewModel.updateDweller(idx, lore = it) },
                            label = "",
                            placeholder = "Edit ${dweller.name} lore...",
                            singleLine = false,
                            minLines = 2
                        )
                    }
                }
            }

            // 05 Session Vault
            CollapsibleModule("05 · SYSTEM & SESSION VAULT", "PRIVATE", expandVault, { expandVault = !expandVault }) {
                CelestialTextField(
                    value = state.vaultNotes,
                    onValueChange = viewModel::updateVaultNotes,
                    label = "OWNER NOTES & SOP REFERENCES",
                    placeholder = "Private operational notes, SOP reminders...",
                    singleLine = false,
                    minLines = 3
                )
                Spacer(Modifier.height(8.dp))
                CelestialTextField(
                    value = state.quickCopySnippet,
                    onValueChange = viewModel::updateQuickCopy,
                    label = "QUICK-COPY SNIPPET",
                    singleLine = false,
                    minLines = 2
                )
                Spacer(Modifier.height(8.dp))
                SecondaryGlassButton("COPY SNIPPET", onClick = {
                    clipboard.setText(AnnotatedString(state.quickCopySnippet))
                    viewModel.setOwnerMasterNotice("Snippet copied.")
                })
                Spacer(Modifier.height(8.dp))
                GoldActionButton("END ALL DEVICE-LOCAL SESSIONS", onClick = viewModel::endAllLocalSessions)
                NoticeBanner(state.ownerMasterNotice, isSuccess = true)
            }

            // Universal Account Generator
            CollapsibleModule("ACCOUNT MANAGEMENT · GENERATOR", "${state.ownerAccounts.size} RECORDS", expandAccounts, { expandAccounts = !expandAccounts }) {
                Text("ACCOUNT ENVIRONMENT", style = MaterialTheme.typography.labelSmall, color = CelestialColors.VioletSoft)
                Spacer(Modifier.height(6.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    listOf("regular-client" to "Client", "celestial-army" to "Army", "beta-tester" to "Beta", "guest-visitor" to "Guest").forEach { (k, lbl) ->
                        ChipButton(lbl, state.accountEnvironment == k, Modifier.weight(1f)) {
                            viewModel.updateAccountGeneratorFields(env = k)
                        }
                    }
                }
                Spacer(Modifier.height(8.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    listOf("generate" to "Generate", "reset-code" to "Reset Code", "regenerate" to "Regenerate").forEach { (k, lbl) ->
                        ChipButton(lbl, state.accountAction == k, Modifier.weight(1f)) {
                            viewModel.updateAccountGeneratorFields(action = k)
                        }
                    }
                }
                if (state.accountAction != "generate") {
                    Spacer(Modifier.height(8.dp))
                    CelestialTextField(
                        value = state.accountIdLookup,
                        onValueChange = { viewModel.updateAccountGeneratorFields(lookupId = it) },
                        label = "EXISTING ACCESS ID",
                        placeholder = "Enter Access ID"
                    )
                }
                if (state.accountAction == "reset-code") {
                    Spacer(Modifier.height(8.dp))
                    CelestialTextField(
                        value = state.accountCodeReset,
                        onValueChange = { viewModel.updateAccountGeneratorFields(resetCode = it) },
                        label = "NEW ACCESS CODE (OPTIONAL)",
                        placeholder = "8 alphanumeric chars"
                    )
                }
                Spacer(Modifier.height(10.dp))
                GoldActionButton("EXECUTE CREDENTIAL ACTION", onClick = viewModel::executeAccountAction, testTag = "execute_account_action_button")
                NoticeBanner(state.accountMessage, isSuccess = true)

                if (state.ownerAccounts.isNotEmpty()) {
                    HorizontalDivider(color = CelestialColors.BorderGlass, modifier = Modifier.padding(vertical = 10.dp))
                    state.ownerAccounts.forEach { acc ->
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text("${acc.accessId} · ${acc.accessCode}", fontFamily = FontFamily.Monospace, fontSize = 12.sp, color = CelestialColors.GoldLight)
                                Text("${acc.environment} · ${acc.status}", fontSize = 10.sp, color = CelestialColors.VioletSoft)
                            }
                            TextButton(onClick = {
                                val nextStatus = if (acc.status == "Active") "Suspended" else "Active"
                                viewModel.updateOwnerAccountStatus(acc.accessId, nextStatus)
                            }) {
                                Text(if (acc.status == "Active") "Suspend" else "Activate", fontSize = 11.sp, color = CelestialColors.GoldBright)
                            }
                        }
                    }
                }

                HorizontalDivider(color = CelestialColors.BorderGlass, modifier = Modifier.padding(vertical = 10.dp))
                CelestialTextField(
                    value = state.ownerEnvironmentPrefix,
                    onValueChange = viewModel::updateOwnerPrefix,
                    label = "OWNER ENVIRONMENT PREFIX (2-3 CHARS)",
                    placeholder = "e.g. OWN"
                )
                Spacer(Modifier.height(8.dp))
                SecondaryGlassButton("SAVE OWNER PREFIX", onClick = viewModel::saveOwnerEnvironmentPrefix)
                NoticeBanner(state.ownerPrefixMessage, isSuccess = true)
            }

            // Preview & Reference
            CollapsibleModule("PREVIEW WORKSPACE", "SIMULATE", expandPreview, { expandPreview = !expandPreview }) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    GoldActionButton("Guest Preview", onClick = { viewModel.navigate(PortalRoute.GUEST) }, modifier = Modifier.weight(1f))
                    SecondaryGlassButton("Client Preview", onClick = { viewModel.navigate(PortalRoute.REGULAR_HOME) }, modifier = Modifier.weight(1f))
                }
            }

            CollapsibleModule("OWNER REFERENCE CENTER", "NOTES", expandReference, { expandReference = !expandReference }) {
                CelestialTextField(
                    value = state.referenceNotes,
                    onValueChange = viewModel::updateReferenceNotes,
                    label = "OPERATIONAL NOTES",
                    placeholder = "Operational procedures, navigation references...",
                    singleLine = false,
                    minLines = 3
                )
            }
        }
    }
}

@Composable
fun OwnerOverlayDialogs(state: CelestialUiState, viewModel: CelestialViewModel) {
    when (state.ownerOverlayPanel) {
        OwnerOverlayPanel.NOTEPAD -> {
            Dialog(onDismissRequest = { viewModel.setOwnerOverlayPanel(OwnerOverlayPanel.NONE) }) {
                CelestialGlassCard(borderColor = CelestialColors.BorderGold, backgroundColor = CelestialColors.MidnightBg) {
                    Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                            Text("OWNER NOTEPAD & FEEDBACK", style = MaterialTheme.typography.labelSmall, color = CelestialColors.GoldLight)
                            TextButton(onClick = { viewModel.setOwnerOverlayPanel(OwnerOverlayPanel.NONE) }) {
                                Text("CLOSE", color = CelestialColors.GoldBright, fontSize = 11.sp)
                            }
                        }
                        CelestialTextField(
                            value = state.ownerNotes,
                            onValueChange = viewModel::updateOwnerNotes,
                            label = "WORKING NOTES",
                            singleLine = false,
                            minLines = 4
                        )
                        HorizontalDivider(color = CelestialColors.BorderGlass, modifier = Modifier.padding(vertical = 10.dp))
                        Text("PAGE FEEDBACK", style = MaterialTheme.typography.labelSmall, color = CelestialColors.GoldLight)
                        Spacer(Modifier.height(6.dp))
                        CelestialTextField(
                            value = state.feedbackPage,
                            onValueChange = { viewModel.updateFeedbackFields(page = it) },
                            label = "PAGE OR ROUTE",
                            placeholder = "e.g. Guest Environment"
                        )
                        Spacer(Modifier.height(6.dp))
                        CelestialTextField(
                            value = state.feedbackIssue,
                            onValueChange = { viewModel.updateFeedbackFields(issue = it) },
                            label = "WHAT IS WRONG?",
                            singleLine = false,
                            minLines = 2
                        )
                        Spacer(Modifier.height(6.dp))
                        CelestialTextField(
                            value = state.feedbackChange,
                            onValueChange = { viewModel.updateFeedbackFields(change = it) },
                            label = "WHAT SHOULD CHANGE?",
                            singleLine = false,
                            minLines = 2
                        )
                        Spacer(Modifier.height(8.dp))
                        GoldActionButton(
                            text = "Save Feedback (${state.feedbackItems.size})",
                            enabled = state.feedbackPage.isNotBlank() && state.feedbackIssue.isNotBlank(),
                            onClick = viewModel::saveFeedbackItem
                        )
                        state.feedbackItems.take(5).forEach { item ->
                            Surface(
                                modifier = Modifier.fillMaxWidth().padding(top = 6.dp),
                                shape = RoundedCornerShape(8.dp),
                                color = CelestialColors.DeepSpace,
                                border = BorderStroke(1.dp, CelestialColors.BorderGlass)
                            ) {
                                Row(modifier = Modifier.padding(8.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(item.page, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = CelestialColors.GoldLight)
                                        Text(item.issue, fontSize = 11.sp, color = CelestialColors.TextSecondary)
                                    }
                                    TextButton(onClick = { viewModel.deleteFeedbackItem(item.id) }) {
                                        Text("Del", fontSize = 10.sp, color = CelestialColors.Rose)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        OwnerOverlayPanel.TOOLS -> {
            Dialog(onDismissRequest = { viewModel.setOwnerOverlayPanel(OwnerOverlayPanel.NONE) }) {
                CelestialGlassCard(borderColor = CelestialColors.BorderGold, backgroundColor = CelestialColors.MidnightBg) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                        Text("OWNER OVERRIDE CONTROLS", style = MaterialTheme.typography.labelSmall, color = CelestialColors.GoldLight)
                        TextButton(onClick = { viewModel.setOwnerOverlayPanel(OwnerOverlayPanel.NONE) }) {
                            Text("CLOSE", color = CelestialColors.GoldBright, fontSize = 11.sp)
                        }
                    }
                    Spacer(Modifier.height(8.dp))
                    SecondaryGlassButton("Return to Owner Workspace", onClick = {
                        viewModel.setOwnerOverlayPanel(OwnerOverlayPanel.NONE)
                        viewModel.navigate(PortalRoute.OWNER_PREVIEW)
                    })
                    Spacer(Modifier.height(8.dp))
                    SecondaryGlassButton("Reset Onboarding & Open Guest", onClick = viewModel::resetOnboardingForOwner)
                    Spacer(Modifier.height(8.dp))
                    SecondaryGlassButton("Open Guest Environment", onClick = {
                        viewModel.setOwnerOverlayPanel(OwnerOverlayPanel.NONE)
                        viewModel.navigate(PortalRoute.GUEST)
                    })
                    Spacer(Modifier.height(8.dp))
                    GoldActionButton("End Owner Override Session", onClick = viewModel::exitOwnerOverride)
                }
            }
        }
        OwnerOverlayPanel.NONE -> {}
    }
}

@Composable
private fun OwnerDestTile(title: String, sub: String, modifier: Modifier, onClick: () -> Unit) {
    CelestialGlassCard(
        modifier = modifier.clickable(onClick = onClick),
        backgroundColor = CelestialColors.GlassCardAlt
    ) {
        Text(title, fontWeight = FontWeight.Bold, color = CelestialColors.GoldBright, fontSize = 13.sp)
        Text(sub, fontSize = 11.sp, color = CelestialColors.VioletSoft)
    }
}

@Composable
private fun CollapsibleModule(
    title: String,
    badge: String,
    expanded: Boolean,
    onToggle: () -> Unit,
    content: @Composable () -> Unit
) {
    CelestialGlassCard(modifier = Modifier.padding(bottom = 10.dp)) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .defaultMinSize(minHeight = 40.dp)
                .clickable(onClick = onToggle),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(title, style = MaterialTheme.typography.labelSmall, color = CelestialColors.GoldBright)
            Text("$badge ${if (expanded) "−" else "+"}", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = CelestialColors.VioletSoft)
        }
        if (expanded) {
            HorizontalDivider(color = CelestialColors.BorderGlass, modifier = Modifier.padding(vertical = 10.dp))
            content()
        }
    }
}

@Composable
private fun ChipButton(label: String, active: Boolean, modifier: Modifier = Modifier, onClick: () -> Unit) {
    Surface(
        shape = RoundedCornerShape(8.dp),
        color = if (active) CelestialColors.GoldLight else CelestialColors.SurfaceDark,
        border = BorderStroke(1.dp, if (active) CelestialColors.GoldLight else CelestialColors.BorderGlass),
        modifier = modifier
            .defaultMinSize(minHeight = 40.dp)
            .clickable(onClick = onClick)
    ) {
        Box(contentAlignment = Alignment.Center, modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp)) {
            Text(
                label,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = if (active) CelestialColors.MidnightBg else CelestialColors.TextSecondary,
                maxLines = 1
            )
        }
    }
}
