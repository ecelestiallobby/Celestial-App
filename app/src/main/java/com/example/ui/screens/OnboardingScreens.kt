package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.CatalogRepository
import com.example.model.PortalRoute
import com.example.ui.components.CelestialGlassCard
import com.example.ui.components.GoldActionButton
import com.example.ui.components.PortalTopline
import com.example.ui.components.SecondaryGlassButton
import com.example.ui.theme.CelestialColors
import com.example.viewmodel.CelestialUiState
import com.example.viewmodel.CelestialViewModel
import java.text.DateFormat
import java.util.Date

@Composable
fun OnboardingScreen(
    state: CelestialUiState,
    viewModel: CelestialViewModel
) {
    BackHandler {
        viewModel.requestOnboardingExit()
    }

    val steps = CatalogRepository.onboardingSteps
    val stepIndex = state.onboardStep.coerceIn(0, steps.lastIndex)
    val step = steps[stepIndex]
    val completed = state.onboardingRecord?.completedSteps ?: emptyList()

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp, vertical = 20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Column(modifier = Modifier.widthIn(max = 600.dp)) {
            PortalTopline(
                backLabel = "Leave Onboarding",
                onBack = viewModel::requestOnboardingExit,
                sectionBadge = "NEW CLIENT JOURNEY"
            )

            LinearProgressIndicator(
                progress = { (stepIndex + 1) / 8f },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp),
                color = CelestialColors.GoldLight,
                trackColor = CelestialColors.SurfaceDark
            )
            Spacer(Modifier.height(8.dp))
            Text(
                text = "STEP ${stepIndex + 1} OF 8 · ${completed.size} COMPLETED",
                style = MaterialTheme.typography.labelSmall,
                color = CelestialColors.GoldDeep
            )
            Spacer(Modifier.height(12.dp))

            // 8-step completion indicator grid
            steps.chunked(2).forEachIndexed { rowIdx, pair ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 6.dp),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    pair.forEachIndexed { colIdx, item ->
                        val idx = rowIdx * 2 + colIdx
                        val isDone = completed.contains(idx)
                        val isCurr = idx == stepIndex
                        Surface(
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(8.dp),
                            color = when {
                                isDone -> CelestialColors.EmeraldBg
                                isCurr -> CelestialColors.AmberBg
                                else -> CelestialColors.SurfaceDark
                            },
                            border = BorderStroke(
                                1.dp,
                                when {
                                    isDone -> CelestialColors.Emerald.copy(alpha = 0.4f)
                                    isCurr -> CelestialColors.GoldLight.copy(alpha = 0.6f)
                                    else -> CelestialColors.BorderGlass
                                }
                            )
                        ) {
                            Column(modifier = Modifier.padding(8.dp)) {
                                Text(
                                    text = when {
                                        isDone -> "✓ COMPLETED"
                                        isCurr -> "● CURRENT"
                                        else -> "○ UPCOMING"
                                    },
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = when {
                                        isDone -> CelestialColors.Emerald
                                        isCurr -> CelestialColors.GoldLight
                                        else -> CelestialColors.VioletMuted
                                    }
                                )
                                Text(
                                    text = "${idx + 1}. ${item.title}",
                                    fontSize = 11.sp,
                                    color = Color.White,
                                    maxLines = 1
                                )
                            }
                        }
                    }
                }
            }

            Spacer(Modifier.height(14.dp))
            CelestialGlassCard(borderColor = CelestialColors.BorderGold) {
                Text(step.title, style = MaterialTheme.typography.headlineMedium, color = CelestialColors.TextPrimary)
                Spacer(Modifier.height(10.dp))
                Text(step.copy, style = MaterialTheme.typography.bodyLarge, color = CelestialColors.TextPrimary)
                Spacer(Modifier.height(12.dp))
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = CelestialColors.DeepSpace.copy(alpha = 0.7f),
                    border = BorderStroke(1.dp, CelestialColors.BorderGlass)
                ) {
                    Text(
                        text = step.extra,
                        fontSize = 13.sp,
                        lineHeight = 19.sp,
                        color = CelestialColors.TextSecondary,
                        modifier = Modifier.padding(12.dp)
                    )
                }

                if (stepIndex == 7) {
                    Spacer(Modifier.height(14.dp))
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .defaultMinSize(minHeight = 48.dp)
                            .clickable { viewModel.setPurchaseIntent("now") }
                            .testTag("intent_ready_option")
                    ) {
                        RadioButton(
                            selected = state.purchaseIntent == "now",
                            onClick = { viewModel.setPurchaseIntent("now") },
                            colors = RadioButtonDefaults.colors(selectedColor = CelestialColors.GoldLight)
                        )
                        Text("I am ready to explore the store", fontSize = 13.sp, color = CelestialColors.GoldBright)
                    }
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .defaultMinSize(minHeight = 48.dp)
                            .clickable { viewModel.setPurchaseIntent("later") }
                            .testTag("intent_later_option")
                    ) {
                        RadioButton(
                            selected = state.purchaseIntent == "later",
                            onClick = { viewModel.setPurchaseIntent("later") },
                            colors = RadioButtonDefaults.colors(selectedColor = CelestialColors.GoldLight)
                        )
                        Text("I am not ready to purchase right now", fontSize = 13.sp, color = CelestialColors.VioletSoft)
                    }
                    Text(
                        "Choosing not to proceed exits to the Guest Environment homepage and activates the 24-hour viewing-only restriction.",
                        fontSize = 11.sp,
                        color = CelestialColors.VioletMuted
                    )
                }
            }

            Spacer(Modifier.height(16.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                SecondaryGlassButton(
                    text = "Back",
                    onClick = viewModel::onboardingBack,
                    enabled = stepIndex > 0,
                    modifier = Modifier.weight(1f),
                    testTag = "onboarding_back_button"
                )
                GoldActionButton(
                    text = if (stepIndex == 7) {
                        if (state.purchaseIntent == "later") "Exit Home" else "Continue to Store"
                    } else "Continue",
                    onClick = viewModel::onboardingContinue,
                    modifier = Modifier.weight(1f),
                    testTag = "onboarding_continue_button"
                )
            }
        }
    }

    if (state.showExitWarning) {
        OnboardingExitDialog(
            onStay = viewModel::dismissOnboardingExitWarning,
            onLeave = viewModel::confirmOnboardingExit
        )
    }
}

@Composable
fun OnboardingTermsScreen(
    state: CelestialUiState,
    viewModel: CelestialViewModel
) {
    BackHandler { viewModel.requestOnboardingExit() }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp, vertical = 20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Column(modifier = Modifier.widthIn(max = 600.dp)) {
            PortalTopline(backLabel = null, onBack = null, sectionBadge = "NEW CLIENT JOURNEY · FINAL REVIEW")
            CelestialGlassCard(borderColor = CelestialColors.BorderGold) {
                Text("Congratulations on completing onboarding", style = MaterialTheme.typography.headlineMedium, color = CelestialColors.TextPrimary)
                Spacer(Modifier.height(6.dp))
                Text("Before entering the Guest Store, review the CELESTIAL LOBBY Terms and Policies.", style = MaterialTheme.typography.bodyMedium, color = CelestialColors.TextSecondary)
                Spacer(Modifier.height(12.dp))
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = CelestialColors.DeepSpace.copy(alpha = 0.7f),
                    border = BorderStroke(1.dp, CelestialColors.BorderGlass)
                ) {
                    Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text("Please review these subjects:", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = CelestialColors.GoldLight)
                        Text("• Pricing and item information", fontSize = 12.sp, color = CelestialColors.TextPrimary)
                        Text("• Accepted payment methods", fontSize = 12.sp, color = CelestialColors.TextPrimary)
                        Text("• Ordering methods and processes", fontSize = 12.sp, color = CelestialColors.TextPrimary)
                        Text("• Applicable policies, restrictions and service guidelines", fontSize = 12.sp, color = CelestialColors.TextPrimary)
                    }
                }
                Spacer(Modifier.height(12.dp))
                Row(
                    verticalAlignment = Alignment.Top,
                    modifier = Modifier
                        .fillMaxWidth()
                        .defaultMinSize(minHeight = 48.dp)
                        .clickable { viewModel.setTermsAccepted(!state.termsAccepted) }
                        .testTag("terms_accept_checkbox")
                ) {
                    Checkbox(
                        checked = state.termsAccepted,
                        onCheckedChange = viewModel::setTermsAccepted,
                        colors = CheckboxDefaults.colors(checkedColor = CelestialColors.GoldLight)
                    )
                    Text(
                        "I have read and understand the CELESTIAL LOBBY Terms and Policies, including pricing, payment methods, ordering processes, restrictions and service guidelines.",
                        fontSize = 13.sp,
                        color = CelestialColors.TextPrimary,
                        modifier = Modifier.padding(top = 10.dp)
                    )
                }
                Spacer(Modifier.height(14.dp))
                GoldActionButton(
                    text = "Continue to Store",
                    enabled = state.termsAccepted,
                    onClick = viewModel::acceptTermsAndEnterStore,
                    testTag = "terms_continue_to_store"
                )
                Spacer(Modifier.height(8.dp))
                SecondaryGlassButton(
                    text = "I Do Not Agree — Leave",
                    onClick = { viewModel.applyOnboardingRestriction("You declined the final Terms and Policies agreement.") },
                    testTag = "terms_decline_button"
                )
            }
        }
    }

    if (state.showExitWarning) {
        OnboardingExitDialog(
            onStay = viewModel::dismissOnboardingExitWarning,
            onLeave = viewModel::confirmOnboardingExit
        )
    }
}

@Composable
fun RestrictionScreen(
    state: CelestialUiState,
    viewModel: CelestialViewModel
) {
    BackHandler { viewModel.navigate(PortalRoute.GUEST) }
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Column(modifier = Modifier.widthIn(max = 560.dp)) {
            PortalTopline(backLabel = null, onBack = null, sectionBadge = "GUEST ENVIRONMENT · VIEWING-ONLY STATUS")
            CelestialGlassCard(borderColor = CelestialColors.BorderGold) {
                Text("Your Journey Is Temporarily Paused", style = MaterialTheme.typography.headlineMedium, color = CelestialColors.TextPrimary)
                Spacer(Modifier.height(8.dp))
                Text(state.restrictionReason, fontSize = 13.sp, color = CelestialColors.GoldBright)
                Spacer(Modifier.height(8.dp))
                Text(
                    "A 24-hour viewing-only restriction is active. You may browse permitted public Guest Environment information, but cannot enter the Store or continue onboarding until the restriction expires.",
                    fontSize = 13.sp,
                    color = CelestialColors.TextSecondary
                )
                state.onboardingRecord?.restrictionUntil?.let { until ->
                    Spacer(Modifier.height(8.dp))
                    Text(
                        "Restriction ends: ${DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.SHORT).format(Date(until))}",
                        fontSize = 12.sp,
                        color = CelestialColors.GoldLight
                    )
                }
                Spacer(Modifier.height(14.dp))
                if (state.onboardingRecord?.helpUsed == true) {
                    Text(
                        "Your one-time Help explanation has already been used in this demo.",
                        fontSize = 11.sp,
                        color = CelestialColors.VioletMuted
                    )
                } else {
                    SecondaryGlassButton(
                        text = "Why can't I continue? — One-time Help",
                        onClick = viewModel::revealOneTimeRestrictionHelp,
                        testTag = "restriction_help_button"
                    )
                }
                if (state.showRestrictionHelp) {
                    Spacer(Modifier.height(10.dp))
                    CelestialGlassCard {
                        Text("Why this restriction exists", fontWeight = FontWeight.Bold, color = CelestialColors.GoldLight, fontSize = 13.sp)
                        Spacer(Modifier.height(4.dp))
                        Text(
                            "The onboarding process is designed to help customers become informed buyers who understand CELESTIAL LOBBY's procedures, conditions and service guidelines before proceeding.",
                            fontSize = 12.sp,
                            color = CelestialColors.TextSecondary
                        )
                    }
                }
                Spacer(Modifier.height(14.dp))
                GoldActionButton("Return to Guest Environment", onClick = { viewModel.navigate(PortalRoute.GUEST) })
            }
        }
    }
}

@Composable
fun StoreLockedScreen(viewModel: CelestialViewModel) {
    BackHandler { viewModel.navigate(PortalRoute.GUEST) }
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Column(modifier = Modifier.widthIn(max = 560.dp)) {
            PortalTopline(backLabel = "Guest Environment", onBack = { viewModel.navigate(PortalRoute.GUEST) }, sectionBadge = "STORE ACCESS")
            CelestialGlassCard {
                Text("Guest Store Access Not Yet Available", style = MaterialTheme.typography.headlineMedium, color = CelestialColors.TextPrimary)
                Spacer(Modifier.height(8.dp))
                Text(
                    "Complete the onboarding journey and accept the CELESTIAL LOBBY Terms and Policies before entering the Guest Store.",
                    fontSize = 13.sp,
                    color = CelestialColors.TextSecondary
                )
                Spacer(Modifier.height(14.dp))
                GoldActionButton("Return to Guest Environment", onClick = { viewModel.navigate(PortalRoute.GUEST) })
            }
        }
    }
}

@Composable
fun OnboardingSubmittedScreen(viewModel: CelestialViewModel) {
    BackHandler { viewModel.navigate(PortalRoute.GUEST) }
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Column(modifier = Modifier.widthIn(max = 560.dp)) {
            PortalTopline(backLabel = null, onBack = null, sectionBadge = "NEW CLIENT JOURNEY · SUBMISSION STATUS")
            CelestialGlassCard(borderColor = CelestialColors.BorderGold) {
                Text("✦", fontSize = 30.sp, color = CelestialColors.GoldLight, modifier = Modifier.align(Alignment.CenterHorizontally))
                Spacer(Modifier.height(8.dp))
                Text(
                    "ONBOARDING SUBMISSION RECEIVED",
                    style = MaterialTheme.typography.headlineSmall,
                    color = Color.White,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height(8.dp))
                Text(
                    "Your onboarding responses have been completed in this device demo. This is not an account approval or account activation.",
                    fontSize = 13.sp,
                    color = CelestialColors.TextSecondary,
                    textAlign = TextAlign.Center
                )
                Spacer(Modifier.height(14.dp))
                GoldActionButton("Return to Guest Environment", onClick = { viewModel.navigate(PortalRoute.GUEST) })
            }
        }
    }
}

@Composable
private fun OnboardingExitDialog(onStay: () -> Unit, onLeave: () -> Unit) {
    AlertDialog(
        onDismissRequest = onStay,
        containerColor = CelestialColors.MidnightBg,
        title = { Text("YOUR JOURNEY IS STILL IN PROGRESS", color = CelestialColors.GoldLight, fontWeight = FontWeight.Bold, fontSize = 15.sp) },
        text = {
            Text(
                "Leaving now will end this onboarding session and activate a 24-hour viewing-only restriction. Your current step will be saved where local storage allows.",
                color = CelestialColors.TextSecondary,
                fontSize = 13.sp
            )
        },
        confirmButton = {
            TextButton(onClick = onStay) { Text("Continue Onboarding", color = CelestialColors.GoldLight) }
        },
        dismissButton = {
            TextButton(onClick = onLeave) { Text("Leave Onboarding", color = CelestialColors.Rose) }
        }
    )
}
